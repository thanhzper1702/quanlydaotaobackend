package com.university.core.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServletContextListener that initializes JPA EntityManagerFactory on startup
 * and shuts it down cleanly on undeploy/context destroyed.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Starting up Academic Management Backend Application...");
        try {
            JpaUtil.getEntityManagerFactory();
            log.info("JPA EntityManagerFactory initialized successfully on application startup.");
        } catch (Exception e) {
            log.error("Failed to initialize JPA on startup: {}", e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Shutting down Academic Management Backend Application...");
        JpaUtil.shutdown();
        log.info("JPA EntityManagerFactory shutdown completed.");
    }
}
