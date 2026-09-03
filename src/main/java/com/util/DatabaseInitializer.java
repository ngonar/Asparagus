package com.util;

import model.ProductCategories;
import model.ProductSwitchPriorities;
import model.Products;
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
                plnCat.setDescription("PLN Prepaid Token & Postpaid Services");
                plnCat.setCreateDate(new Date());
                plnCat.setCreateBy("SYSTEM");
                em.persist(plnCat);

                ProductCategories pulsaCat = new ProductCategories("PULSA", "Cellular Credit");
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
                p1.setProductId(1L);
                p1.setWeight(100);
                p1.setCreateDate(new Date());
                p1.setCreateBy("SYSTEM");
                em.persist(p1);

                ProductSwitchPriorities p2 = new ProductSwitchPriorities("PLN20", "BACKUP_SWITCH", 2);
                p2.setProductId(1L);
                p2.setWeight(50);
                p2.setCreateDate(new Date());
                p2.setCreateBy("SYSTEM");
                em.persist(p2);
            }

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
