package com.university.core.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-safe Singleton managing the JPA EntityManagerFactory.
 */
public final class JpaUtil {

    private static final Logger log = LoggerFactory.getLogger(JpaUtil.class);
    private static final String PERSISTENCE_UNIT_NAME = "AcademicPU";
    private static volatile EntityManagerFactory emf;

    private JpaUtil() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (JpaUtil.class) {
                if (emf == null) {
                    try {
                        log.info("Initializing EntityManagerFactory for unit: {}", PERSISTENCE_UNIT_NAME);
                        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, DbConfig.getJpaProperties());
                        log.info("EntityManagerFactory successfully initialized.");
                    } catch (Exception e) {
                        log.error("Failed to initialize EntityManagerFactory: {}", e.getMessage(), e);
                        throw new ExceptionInInitializerError(e);
                    }
                }
            }
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            log.info("Closing EntityManagerFactory.");
            emf.close();
        }
    }
}
