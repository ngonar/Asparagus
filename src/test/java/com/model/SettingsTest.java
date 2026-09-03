package com.model;

import com.util.DatabaseInitializer;
import com.util.SwitchPriorityManager;
import model.Settings;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class SettingsTest {

    @BeforeClass
    public static void setUp() {
        DatabaseInitializer.initializeDatabase();
    }

    @Test
    public void testSettingsPersistenceAndQueries() {
        EntityManagerFactory factory = SwitchPriorityManager.getEmf();
        EntityManager em = factory.createEntityManager();

        try {
            // Fetch all settings
            @SuppressWarnings("unchecked")
            List<Settings> allSettings = em.createNamedQuery("Settings.findAll").getResultList();
            assertNotNull("Settings list should not be null", allSettings);
            assertFalse("Settings list should not be empty", allSettings.isEmpty());

            // Fetch specific setting by parameter
            @SuppressWarnings("unchecked")
            List<Settings> respawnSettingList = em.createNamedQuery("Settings.findByParameter")
                    .setParameter("parameter", "respawn_setting")
                    .getResultList();

            assertNotNull(respawnSettingList);
            assertFalse(respawnSettingList.isEmpty());
            Settings respawnSetting = respawnSettingList.get(0);
            assertEquals("respawn_setting", respawnSetting.getParameter());
            assertEquals("60", respawnSetting.getValue());

            // Create and persist a new custom setting parameter/value
            String paramName = "TEST_PARAM_" + System.currentTimeMillis();
            String paramValue = "TEST_VALUE_123";

            em.getTransaction().begin();
            Settings customSetting = new Settings(paramName, paramValue, "Test configuration item");
            customSetting.setId(System.currentTimeMillis());
            customSetting.setCreateDate(new Date());
            customSetting.setCreateBy("TEST_USER");
            em.persist(customSetting);
            em.getTransaction().commit();

            // Verify custom setting query
            em.clear();
            @SuppressWarnings("unchecked")
            List<Settings> queriedList = em.createNamedQuery("Settings.findByParameter")
                    .setParameter("parameter", paramName)
                    .getResultList();

            assertNotNull(queriedList);
            assertFalse(queriedList.isEmpty());
            Settings queriedSetting = queriedList.get(0);
            assertEquals(paramName, queriedSetting.getParameter());
            assertEquals(paramValue, queriedSetting.getValue());

            // Update value
            em.getTransaction().begin();
            queriedSetting.setValue("UPDATED_VALUE_456");
            queriedSetting.setUpdateDate(new Date());
            queriedSetting.setUpdateBy("TEST_USER");
            em.merge(queriedSetting);
            em.getTransaction().commit();

            // Verify updated value
            em.clear();
            Settings updatedSetting = em.find(Settings.class, queriedSetting.getId());
            assertNotNull(updatedSetting);
            assertEquals("UPDATED_VALUE_456", updatedSetting.getValue());

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
