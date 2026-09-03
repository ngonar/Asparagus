package com.util;

import model.ProductSupplierConfigurations;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.Date;

import static org.junit.Assert.*;

public class SupplierConfigManagerTest {

    @BeforeClass
    public static void setUpClass() {
        DatabaseInitializer.initializeDatabase();
    }

    @org.junit.Before
    public void setUp() {
        EntityManager em = SwitchPriorityManager.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("UPDATE ProductSwitchPriorities p SET p.active = true, p.failureCount = 0, p.disabledTimestamp = null WHERE p.productCode = 'PLN20'").executeUpdate();
        em.createQuery("UPDATE ProductSupplierConfigurations c SET c.active = true WHERE c.productCode = 'PLN20'").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testGetSupplierConfig() {
        // Reset switch state to active
        SwitchPriorityManager.recordOutcome("PLN20", "MAIN_SWITCH", true);

        // Query pre-seeded supplier config for PLN20 / MAIN_SWITCH
        ProductSupplierConfigurations mainConfig = SupplierConfigManager.getSupplierConfig("PLN20", "MAIN_SWITCH");
        assertNotNull("Main supplier configuration should exist", mainConfig);
        assertEquals("PLN20", mainConfig.getProductCode());
        assertEquals("MAIN_SWITCH", mainConfig.getSupplierName());
        assertEquals("api.main-supplier.com", mainConfig.getHost());
        assertEquals("192.168.1.10", mainConfig.getIp());
        assertEquals(Integer.valueOf(8080), mainConfig.getPort());
        assertEquals("pln_main_user", mainConfig.getUsername());
        assertEquals("pln_main_pass", mainConfig.getPassword());
        assertEquals("key_main_123", mainConfig.getApiKey());
        assertEquals("secret_main_abc", mainConfig.getSecretKey());

        // Query pre-seeded supplier config for PLN20 / BACKUP_SWITCH
        ProductSupplierConfigurations backupConfig = SupplierConfigManager.getSupplierConfig("PLN20", "BACKUP_SWITCH");
        assertNotNull("Backup supplier configuration should exist", backupConfig);
        assertEquals("api.backup-supplier.com", backupConfig.getHost());
        assertEquals("192.168.1.20", backupConfig.getIp());
        assertEquals("pln_backup_user", backupConfig.getUsername());

        // Get currently active supplier config for PLN20
        ProductSupplierConfigurations activeConfig = SupplierConfigManager.getActiveSupplierConfig("PLN20");
        assertNotNull("Active supplier config should exist", activeConfig);
        assertEquals("PLN20", activeConfig.getProductCode());
    }

    @Test
    public void testCreateAndRetrieveCustomSupplierConfig() {
        String testProduct = "PROD_TEST_" + System.currentTimeMillis();
        String testSupplier = "SUPPLIER_XYZ";

        EntityManager em = SwitchPriorityManager.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            ProductSupplierConfigurations customConfig = new ProductSupplierConfigurations(
                    testProduct, testSupplier, "supplier-xyz.gateway.com", "10.10.0.5", 9090);
            customConfig.setId(System.currentTimeMillis());
            customConfig.setUsername("custom_user");
            customConfig.setPassword("custom_pass");
            customConfig.setApiKey("key_xyz_789");
            customConfig.setSecretKey("secret_xyz_000");
            customConfig.setActive(true);
            customConfig.setCreateDate(new Date());
            customConfig.setCreateBy("TEST");
            em.persist(customConfig);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        ProductSupplierConfigurations fetched = SupplierConfigManager.getSupplierConfig(testProduct, testSupplier);
        assertNotNull(fetched);
        assertEquals(testProduct, fetched.getProductCode());
        assertEquals(testSupplier, fetched.getSupplierName());
        assertEquals("supplier-xyz.gateway.com", fetched.getHost());
        assertEquals("10.10.0.5", fetched.getIp());
        assertEquals(Integer.valueOf(9090), fetched.getPort());
        assertEquals("custom_user", fetched.getUsername());
        assertEquals("custom_pass", fetched.getPassword());
        assertEquals("key_xyz_789", fetched.getApiKey());
        assertEquals("secret_xyz_000", fetched.getSecretKey());
    }
}
