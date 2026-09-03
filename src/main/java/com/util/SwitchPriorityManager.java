package com.util;

import com.api.Asparagus;
import model.ProductSwitchPriorities;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Manager for tracking request/response outcomes, threshold-based switch disabling,
 * and automatic respawning after the configured timeout in config/settings.xml.
 */
public class SwitchPriorityManager {

    private static final Logger logger = Logger.getLogger(SwitchPriorityManager.class);

    private static EntityManagerFactory emf = null;

    public static synchronized EntityManagerFactory getEmf() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory("API", System.getProperties());
        }
        return emf;
    }

    /**
     * Checks if a disabled switch priority should be automatically re-enabled
     * after respawn_setting seconds from settings.xml.
     */
    public static boolean checkAndRespawn(ProductSwitchPriorities priority, EntityManager em) {
        if (priority == null) return false;

        boolean active = Boolean.TRUE.equals(priority.getActive());
        if (!active && priority.getDisabledTimestamp() != null) {
            long now = System.currentTimeMillis();
            long disabledTime = priority.getDisabledTimestamp().getTime();
            int respawnSeconds = Asparagus.getRespawnSetting();
            long respawnMillis = respawnSeconds * 1000L;

            if ((now - disabledTime) >= respawnMillis) {
                logger.info("Switch priority [" + priority.getSwitchName() + "] for product ["
                        + priority.getProductCode() + "] automatically ENABLED after "
                        + respawnSeconds + " seconds respawn setting.");

                boolean transactionOwned = false;
                if (!em.getTransaction().isActive()) {
                    em.getTransaction().begin();
                    transactionOwned = true;
                }

                priority.setActive(true);
                priority.setFailureCount(0);
                priority.setDisabledTimestamp(null);
                priority.setUpdateDate(new Date());
                priority.setUpdateBy("SYSTEM_RESPAWN");
                em.merge(priority);

                if (transactionOwned) {
                    em.getTransaction().commit();
                }
                return true;
            }
        }
        return active;
    }

    /**
     * Records a request response outcome (success or failure) using an existing EntityManager.
     */
    public static void recordOutcome(EntityManager em, String productCode, String switchName, boolean isSuccess) {
        if (productCode == null || switchName == null || em == null) return;

        em.clear();
        Query query = em.createNamedQuery("ProductSwitchPriorities.findByProductCodeAndSwitchName");
        query.setParameter("productCode", productCode);
        query.setParameter("switchName", switchName);

        @SuppressWarnings("unchecked")
        List<ProductSwitchPriorities> list = query.getResultList();
        if (list == null || list.isEmpty()) {
            logger.info("No ProductSwitchPriorities found for productCode=" + productCode + ", switchName=" + switchName + ". Creating new record.");
            ProductSwitchPriorities newPriority = new ProductSwitchPriorities(productCode, switchName, 1);
            newPriority.setId(System.currentTimeMillis());
            newPriority.setThreshold(5);
            newPriority.setFailureCount(isSuccess ? 0 : 1);
            newPriority.setActive(true);
            newPriority.setCreateDate(new Date());
            newPriority.setCreateBy("SYSTEM");

            boolean tx = false;
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                tx = true;
            }
            em.persist(newPriority);
            if (tx) {
                em.getTransaction().commit();
            }
            return;
        }

        boolean tx = false;
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
            tx = true;
        }

        for (ProductSwitchPriorities priority : list) {
            if (priority == null) continue;

            if (isSuccess) {
                logger.info("Response SUCCESS for switch [" + switchName + "] product [" + productCode + "]. Resetting failure count.");
                priority.setFailureCount(0);
                priority.setActive(true);
                priority.setDisabledTimestamp(null);
                priority.setUpdateDate(new Date());
                priority.setUpdateBy("SYSTEM");
            } else {
                int currentFailures = (priority.getFailureCount() == null ? 0 : priority.getFailureCount()) + 1;
                priority.setFailureCount(currentFailures);
                priority.setUpdateDate(new Date());
                priority.setUpdateBy("SYSTEM");

                int threshold = (priority.getThreshold() == null || priority.getThreshold() <= 0) ? 5 : priority.getThreshold();
                logger.warn("Response FAILURE for switch [" + switchName + "] product [" + productCode
                        + "]. Current failure count: " + currentFailures + "/" + threshold);

                if (currentFailures >= threshold) {
                    logger.error("Threshold (" + threshold + ") reached for switch [" + switchName
                            + "] product [" + productCode + "]. Changing status to DISABLED for "
                            + Asparagus.getRespawnSetting() + " seconds.");
                    priority.setActive(false);
                    priority.setDisabledTimestamp(new Date());
                }
            }
            em.merge(priority);
        }

        em.flush();
        if (tx) {
            em.getTransaction().commit();
        }
    }

    /**
     * Records a request response outcome (success or failure) for a switch priority.
     * Increments failure count on failure; if failure count reaches threshold, switch is DISABLED.
     * Resets failure count on success.
     */
    public static void recordOutcome(String productCode, String switchName, boolean isSuccess) {
        if (productCode == null || switchName == null) return;

        EntityManager em = null;
        try {
            em = getEmf().createEntityManager();
            recordOutcome(em, productCode, switchName, isSuccess);
        } catch (Exception e) {
            logger.error("Error recording outcome for switch priority: " + e.getMessage(), e);
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Helper to read a configuration parameter value from system_settings DB table.
     */
    public static String getSettingValue(String parameter, String defaultValue) {
        EntityManager em = null;
        try {
            em = getEmf().createEntityManager();
            @SuppressWarnings("unchecked")
            List<model.Settings> list = em.createNamedQuery("Settings.findByParameter")
                    .setParameter("parameter", parameter)
                    .getResultList();

            if (list != null && !list.isEmpty() && list.get(0) != null) {
                String val = list.get(0).getValue();
                if (val != null && !val.trim().isEmpty()) {
                    return val.trim();
                }
            }
        } catch (Exception e) {
            logger.warn("Could not load system setting [" + parameter + "]: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return defaultValue;
    }

    /**
     * Checks if a target string matches any token in a comma-separated configuration string.
     */
    public static boolean isValueInCommaSeparatedList(String target, String commaSeparatedList) {
        if (target == null || commaSeparatedList == null) return false;
        String[] tokens = commaSeparatedList.split(",");
        for (String token : tokens) {
            if (target.trim().equalsIgnoreCase(token.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience method to record response based on Response Code (RC) and Status.
     * Conditions for success and failure are dynamically configurable via parameters
     * in the system_settings table:
     * - "success_rc_list": Comma-separated list of RC values indicating success (default: "0000")
     * - "failure_status_list": Comma-separated list of Status values indicating failure (default: "GAGAL")
     */
    public static void recordResponse(String productCode, String switchName, String rc, String status) {
        String successRcList = getSettingValue("success_rc_list", "0000");
        String failureStatusList = getSettingValue("failure_status_list", "GAGAL");

        boolean isRcSuccess = isValueInCommaSeparatedList(rc, successRcList);
        boolean isStatusFailure = isValueInCommaSeparatedList(status, failureStatusList);

        boolean isSuccess = isRcSuccess && !isStatusFailure;
        logger.info("Recording response for [" + productCode + "/" + switchName + "] RC=" + rc + ", Status=" + status
                + " -> evaluated isSuccess=" + isSuccess + " (success_rc_list=" + successRcList
                + ", failure_status_list=" + failureStatusList + ")");

        recordOutcome(productCode, switchName, isSuccess);
    }

    /**
     * Fetches active ProductSwitchPriorities for a product, automatically triggering respawn checks.
     */
    public static ProductSwitchPriorities getActiveSwitchPriority(String productCode) {
        EntityManager em = null;
        try {
            em = getEmf().createEntityManager();

            Query query = em.createQuery("SELECT p FROM ProductSwitchPriorities p WHERE p.productCode = :productCode ORDER BY p.priority ASC");
            query.setParameter("productCode", productCode);

            @SuppressWarnings("unchecked")
            List<ProductSwitchPriorities> list = query.getResultList();
            if (list != null) {
                for (ProductSwitchPriorities p : list) {
                    if (checkAndRespawn(p, em)) {
                        return p;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching active switch priority: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return null;
    }
}
