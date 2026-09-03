package com.util;

import com.api.Asparagus;
import model.ProductSwitchPriorities;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;

public class SwitchPriorityManagerTest {

    @BeforeClass
    public static void setUp() {
        DatabaseInitializer.initializeDatabase();
        Asparagus.setRespawnSetting(2); // Set 2 seconds respawn window for test
    }

    private ProductSwitchPriorities findValidPriority(String productCode, String switchName) {
        EntityManager em = SwitchPriorityManager.getEmf().createEntityManager();
        try {
            @SuppressWarnings("unchecked")
            List<ProductSwitchPriorities> list = em.createNamedQuery("ProductSwitchPriorities.findByProductCodeAndSwitchName")
                    .setParameter("productCode", productCode)
                    .setParameter("switchName", switchName)
                    .getResultList();

            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Test
    public void testFailureThresholdAndRespawn() throws Exception {
        String productCode = "TEST_PLN20";
        String switchName = "TEST_SWITCH";

        // Clean any old test records
        EntityManager em = SwitchPriorityManager.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM ProductSwitchPriorities p WHERE p.productCode = :pc AND p.switchName = :sn")
                .setParameter("pc", productCode)
                .setParameter("sn", switchName)
                .executeUpdate();
        em.getTransaction().commit();
        em.close();

        // 1. Initial success outcome creates switch priority record
        SwitchPriorityManager.recordOutcome(productCode, switchName, true);

        ProductSwitchPriorities priority = findValidPriority(productCode, switchName);
        assertNotNull("ProductSwitchPriorities entity should exist after recordOutcome", priority);
        assertTrue("Status should initially be active", Boolean.TRUE.equals(priority.getActive()));
        assertEquals("Failure count should be reset to 0 on success", Integer.valueOf(0), priority.getFailureCount());

        // Set threshold to 3 for testing
        em = SwitchPriorityManager.getEmf().createEntityManager();
        em.getTransaction().begin();
        ProductSwitchPriorities pToUpdate = em.find(ProductSwitchPriorities.class, priority.getId());
        pToUpdate.setThreshold(3);
        em.getTransaction().commit();
        em.close();

        int threshold = 3;

        // 2. Record failures up to threshold - 1 (2 failures)
        for (int i = 0; i < threshold - 1; i++) {
            SwitchPriorityManager.recordOutcome(productCode, switchName, false);
        }

        priority = findValidPriority(productCode, switchName);
        assertNotNull(priority);
        assertTrue("Status should remain active before threshold is reached", Boolean.TRUE.equals(priority.getActive()));
        assertEquals("Failure count should equal threshold - 1", Integer.valueOf(2), priority.getFailureCount());

        // 3. Final failure reaching threshold (3rd failure)
        SwitchPriorityManager.recordOutcome(productCode, switchName, false);

        priority = findValidPriority(productCode, switchName);
        assertNotNull(priority);
        assertFalse("Status should change to disabled (false) when threshold is reached", Boolean.TRUE.equals(priority.getActive()));
        assertNotNull("Disabled timestamp should be recorded when threshold is reached", priority.getDisabledTimestamp());

        // 4. Immediately check respawn before timeout (should remain disabled)
        em = SwitchPriorityManager.getEmf().createEntityManager();
        boolean isRespawnedNow = SwitchPriorityManager.checkAndRespawn(priority, em);
        em.close();
        assertFalse("Should remain disabled before respawn_setting timeout (2s)", isRespawnedNow);

        // 5. Wait for respawn setting (2.5 seconds > 2 seconds setting)
        Thread.sleep(2500);

        em = SwitchPriorityManager.getEmf().createEntityManager();
        boolean isRespawnedAfterWait = SwitchPriorityManager.checkAndRespawn(priority, em);
        em.close();
        assertTrue("Should be automatically enabled after respawn_setting timeout", isRespawnedAfterWait);

        priority = findValidPriority(productCode, switchName);
        assertNotNull(priority);
        assertTrue("Status should be active (true) after respawn", Boolean.TRUE.equals(priority.getActive()));
        assertEquals("Failure count should be reset to 0 after respawn", Integer.valueOf(0), priority.getFailureCount());
        assertNull("Disabled timestamp should be cleared after respawn", priority.getDisabledTimestamp());
    }

    @Test
    public void testConfigurableResponseConditions() {
        String productCode = "TEST_PLN20_CFG";
        String switchName = "TEST_SWITCH_CFG";

        // Clean any old test records
        EntityManager em = SwitchPriorityManager.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM ProductSwitchPriorities p WHERE p.productCode = :pc AND p.switchName = :sn")
                .setParameter("pc", productCode)
                .setParameter("sn", switchName)
                .executeUpdate();

        // Ensure custom success RC list in system_settings
        @SuppressWarnings("unchecked")
        List<model.Settings> sList = em.createNamedQuery("Settings.findByParameter")
                .setParameter("parameter", "success_rc_list")
                .getResultList();

        model.Settings cfgSetting;
        if (sList != null && !sList.isEmpty()) {
            cfgSetting = sList.get(0);
            cfgSetting.setValue("0000,00,OK_CODE");
            em.merge(cfgSetting);
        } else {
            cfgSetting = new model.Settings("success_rc_list", "0000,00,OK_CODE", "Success RCs");
            cfgSetting.setId(System.currentTimeMillis());
            em.persist(cfgSetting);
        }
        em.getTransaction().commit();
        em.close();

        // Test custom RC "OK_CODE" with status "BERHASIL" -> evaluated as SUCCESS (failureCount = 0)
        SwitchPriorityManager.recordResponse(productCode, switchName, "OK_CODE", "BERHASIL");
        ProductSwitchPriorities priority = findValidPriority(productCode, switchName);
        assertNotNull(priority);
        assertEquals(Integer.valueOf(0), priority.getFailureCount());

        // Test RC "UNKNOWN_RC" -> evaluated as FAILURE (failureCount = 1)
        SwitchPriorityManager.recordResponse(productCode, switchName, "UNKNOWN_RC", "BERHASIL");
        priority = findValidPriority(productCode, switchName);
        assertNotNull(priority);
        assertEquals(Integer.valueOf(1), priority.getFailureCount());

        // Test RC "0000" with status "GAGAL" -> evaluated as FAILURE (failureCount = 2)
        SwitchPriorityManager.recordResponse(productCode, switchName, "0000", "GAGAL");
        priority = findValidPriority(productCode, switchName);
        assertNotNull(priority);
        assertEquals(Integer.valueOf(2), priority.getFailureCount());
    }
}
