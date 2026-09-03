package com.util;

import model.ProductSupplierConfigurations;
import model.ProductSwitchPriorities;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

/**
 * Manager class to retrieve product supplier configuration details (host, IP, port, credentials).
 */
public class SupplierConfigManager {

    private static final Logger logger = Logger.getLogger(SupplierConfigManager.class);

    /**
     * Fetches supplier configuration for a specific product code and supplier name.
     */
    public static ProductSupplierConfigurations getSupplierConfig(String productCode, String supplierName) {
        if (productCode == null || supplierName == null) return null;

        EntityManagerFactory emf = SwitchPriorityManager.getEmf();
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            Query query = em.createNamedQuery("ProductSupplierConfigurations.findByProductCodeAndSupplierName");
            query.setParameter("productCode", productCode);
            query.setParameter("supplierName", supplierName);

            @SuppressWarnings("unchecked")
            List<ProductSupplierConfigurations> list = query.getResultList();
            if (list != null && !list.isEmpty()) {
                for (ProductSupplierConfigurations config : list) {
                    if (config != null && Boolean.TRUE.equals(config.getActive())) {
                        return config;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching supplier config for product [" + productCode + "] supplier [" + supplierName + "]: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return null;
    }

    /**
     * Convenience method to fetch the supplier configuration for the currently active switch priority of a product.
     */
    public static ProductSupplierConfigurations getActiveSupplierConfig(String productCode) {
        ProductSwitchPriorities activeSwitch = SwitchPriorityManager.getActiveSwitchPriority(productCode);
        if (activeSwitch == null) {
            logger.warn("No active switch priority found for product [" + productCode + "]");
            return null;
        }
        return getSupplierConfig(productCode, activeSwitch.getSwitchName());
    }
}
