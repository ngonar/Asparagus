package com.util;

import model.ProductCategories;
import model.ProductSupplierConfigurations;
import model.ProductSwitchPriorities;
import model.Products;
import model.Settings;
import model.Users;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;

public class DatabaseInitializer {

    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        initializeDatabase();
    }

    public static synchronized void initializeDatabase() {
        logger.info("Initializing SQLite database with Hibernate ORM...");

        EntityManagerFactory factory = null;
        EntityManager em = null;

        try {
            factory = Persistence.createEntityManagerFactory("API", System.getProperties());
            em = factory.createEntityManager();

            em.getTransaction().begin();

            // Native DDL to ensure auto-increment primary key in SQLite
            try {
                em.createNativeQuery("CREATE TABLE IF NOT EXISTS product_categories (id INTEGER PRIMARY KEY AUTOINCREMENT, code VARCHAR(255), name VARCHAR(255), description VARCHAR(255), active BOOLEAN DEFAULT 1, create_date TIMESTAMP, create_by VARCHAR(255), update_date TIMESTAMP, update_by VARCHAR(255))").executeUpdate();
            } catch (Exception ignored) {}

            try {
                em.createNativeQuery("CREATE TABLE IF NOT EXISTS product_switch_priorities (id INTEGER PRIMARY KEY AUTOINCREMENT, product_id BIGINT, product_code VARCHAR(255), switch_name VARCHAR(255), priority INTEGER, weight INTEGER, threshold INTEGER DEFAULT 5, failure_count INTEGER DEFAULT 0, disabled_timestamp TIMESTAMP, active BOOLEAN DEFAULT 1, create_date TIMESTAMP, create_by VARCHAR(255), update_date TIMESTAMP, update_by VARCHAR(255))").executeUpdate();
            } catch (Exception ignored) {}

            try {
                em.createNativeQuery("CREATE TABLE IF NOT EXISTS system_settings (id INTEGER PRIMARY KEY AUTOINCREMENT, parameter VARCHAR(255) UNIQUE, value VARCHAR(255), description VARCHAR(255), create_date TIMESTAMP, create_by VARCHAR(255), update_date TIMESTAMP, update_by VARCHAR(255))").executeUpdate();
            } catch (Exception ignored) {}

            try {
                em.createNativeQuery("CREATE TABLE IF NOT EXISTS product_supplier_configurations (id INTEGER PRIMARY KEY AUTOINCREMENT, product_code VARCHAR(255), supplier_name VARCHAR(255), host VARCHAR(255), ip VARCHAR(255), port INTEGER, username VARCHAR(255), password VARCHAR(255), api_key VARCHAR(255), secret_key VARCHAR(255), active BOOLEAN DEFAULT 1, create_date TIMESTAMP, create_by VARCHAR(255), update_date TIMESTAMP, update_by VARCHAR(255))").executeUpdate();
            } catch (Exception ignored) {}

            // Seed Users if table is empty
            long userCount = (Long) em.createQuery("SELECT COUNT(u) FROM Users u").getSingleResult();
            if (userCount == 0) {
                logger.info("Seeding initial user data...");
                Users admin = new Users(1);
                admin.setUsername("admin");
                // SHA-1 hash for "xxxxxxadmin" where salt = "xxxxxx"
                admin.setPassword("d033e22ae348aeb5660fc2140aec35850c4da997");
                admin.setSecretKey("secret123");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setEmail("admin@asparagus.local");
                admin.setPhone("08123456789");
                admin.setPin("1234");
                em.persist(admin);
            }

            // Seed ProductCategories if table is empty
            long categoryCount = (Long) em.createQuery("SELECT COUNT(c) FROM ProductCategories c").getSingleResult();
            if (categoryCount == 0) {
                logger.info("Seeding initial product category data...");
                ProductCategories plnCat = new ProductCategories("PLN", "Electricity PLN");
                plnCat.setId(1L);
                plnCat.setDescription("PLN Prepaid Token & Postpaid Services");
                plnCat.setCreateDate(new Date());
                plnCat.setCreateBy("SYSTEM");
                em.persist(plnCat);

                ProductCategories pulsaCat = new ProductCategories("PULSA", "Cellular Credit");
                pulsaCat.setId(2L);
                pulsaCat.setDescription("Mobile Top-Up & Data Packages");
                pulsaCat.setCreateDate(new Date());
                pulsaCat.setCreateBy("SYSTEM");
                em.persist(pulsaCat);
            }

            // Seed Products if table is empty
            long productCount = (Long) em.createQuery("SELECT COUNT(p) FROM Products p").getSingleResult();
            if (productCount == 0) {
                logger.info("Seeding initial product data...");
                Products pln20 = new Products();
                pln20.setCode("PLN20");
                pln20.setName("PLN Token 20.000");
                pln20.setDescription("Token PLN Nominal 20K");
                pln20.setNominal(20000);
                pln20.setHargaModal(20000L);
                pln20.setActive(true);
                pln20.setProductCategoryId(1);
                em.persist(pln20);

                Products pln50 = new Products();
                pln50.setCode("PLN50");
                pln50.setName("PLN Token 50.000");
                pln50.setDescription("Token PLN Nominal 50K");
                pln50.setNominal(50000);
                pln50.setHargaModal(50000L);
                pln50.setActive(true);
                pln50.setProductCategoryId(1);
                em.persist(pln50);
            }

            // Seed ProductSwitchPriorities if table is empty
            long switchPriorityCount = (Long) em.createQuery("SELECT COUNT(s) FROM ProductSwitchPriorities s").getSingleResult();
            if (switchPriorityCount == 0) {
                logger.info("Seeding initial product switch priority data...");
                ProductSwitchPriorities p1 = new ProductSwitchPriorities("PLN20", "MAIN_SWITCH", 1);
                p1.setId(1L);
                p1.setProductId(1L);
                p1.setWeight(100);
                p1.setThreshold(3);
                p1.setFailureCount(0);
                p1.setActive(true);
                p1.setCreateDate(new Date());
                p1.setCreateBy("SYSTEM");
                em.persist(p1);

                ProductSwitchPriorities p2 = new ProductSwitchPriorities("PLN20", "BACKUP_SWITCH", 2);
                p2.setId(2L);
                p2.setProductId(1L);
                p2.setWeight(50);
                p2.setThreshold(5);
                p2.setFailureCount(0);
                p2.setActive(true);
                p2.setCreateDate(new Date());
                p2.setCreateBy("SYSTEM");
                em.persist(p2);
            }

            // Seed Settings if table is empty
            long settingsCount = (Long) em.createQuery("SELECT COUNT(s) FROM Settings s").getSingleResult();
            if (settingsCount == 0) {
                logger.info("Seeding initial configuration settings data...");
                String[][] initialConfigs = {
                    {"minthread", "50", "Minimum thread count for server pool"},
                    {"maxthread", "200", "Maximum thread count for server pool"},
                    {"timeoutmilis", "3000", "HTTP request timeout in milliseconds"},
                    {"salt", "xxxxxx", "Security salt string"},
                    {"port", "1985", "Server listening port"},
                    {"mc_host", "localhost", "Memcached host address"},
                    {"mc_port", "11212", "Memcached port number"},
                    {"respawn_setting", "60", "Automatic switch priority respawn window in seconds"},
                    {"success_rc_list", "0000,00,0", "Comma-separated Response Codes considered successful"},
                    {"failure_status_list", "GAGAL,FAILED,ERROR", "Comma-separated Status values considered failure"}
                };

                long idCounter = 1;
                for (String[] cfg : initialConfigs) {
                    Settings setting = new Settings(cfg[0], cfg[1], cfg[2]);
                    setting.setId(idCounter++);
                    setting.setCreateDate(new Date());
                    setting.setCreateBy("SYSTEM");
                    em.persist(setting);
                }
            }

            // Seed ProductSupplierConfigurations if table is empty
            long supplierConfigCount = (Long) em.createQuery("SELECT COUNT(c) FROM ProductSupplierConfigurations c").getSingleResult();
            if (supplierConfigCount == 0) {
                logger.info("Seeding initial product supplier configuration data...");
                ProductSupplierConfigurations cfg1 = new ProductSupplierConfigurations("PLN20", "MAIN_SWITCH", "api.main-supplier.com", "192.168.1.10", 8080);
                cfg1.setId(1L);
                cfg1.setUsername("pln_main_user");
                cfg1.setPassword("pln_main_pass");
                cfg1.setApiKey("key_main_123");
                cfg1.setSecretKey("secret_main_abc");
                cfg1.setActive(true);
                cfg1.setCreateDate(new Date());
                cfg1.setCreateBy("SYSTEM");
                em.persist(cfg1);

                ProductSupplierConfigurations cfg2 = new ProductSupplierConfigurations("PLN20", "BACKUP_SWITCH", "api.backup-supplier.com", "192.168.1.20", 8080);
                cfg2.setId(2L);
                cfg2.setUsername("pln_backup_user");
                cfg2.setPassword("pln_backup_pass");
                cfg2.setApiKey("key_backup_456");
                cfg2.setSecretKey("secret_backup_def");
                cfg2.setActive(true);
                cfg2.setCreateDate(new Date());
                cfg2.setCreateBy("SYSTEM");
                em.persist(cfg2);
            }

            // Ensure no null values exist for active, failureCount, or threshold
            em.createQuery("DELETE FROM ProductSwitchPriorities p WHERE p.id IS NULL OR p.priority IS NULL").executeUpdate();
            em.createQuery("UPDATE ProductSwitchPriorities p SET p.active = true WHERE p.active IS NULL").executeUpdate();
            em.createQuery("UPDATE ProductSwitchPriorities p SET p.failureCount = 0 WHERE p.failureCount IS NULL").executeUpdate();
            em.createQuery("UPDATE ProductSwitchPriorities p SET p.threshold = 5 WHERE p.threshold IS NULL").executeUpdate();

            em.getTransaction().commit();
            logger.info("SQLite database initialization completed successfully.");

        } catch (Exception e) {
            logger.error("Error during database initialization: " + e.getMessage(), e);
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (factory != null && factory.isOpen()) {
                factory.close();
            }
        }
    }
}
