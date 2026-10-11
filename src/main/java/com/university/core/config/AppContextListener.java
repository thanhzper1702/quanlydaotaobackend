package com.university.core.config;

import com.university.core.scheduler.SemesterLifecycleScheduler;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Starting up Academic Management Backend Application...");
        try {
            JpaUtil.getEntityManagerFactory();
            log.info("JPA EntityManagerFactory initialized successfully on application startup.");
            SemesterLifecycleScheduler.start();
        } catch (Exception e) {
            log.error("Failed to initialize backend on startup: {}", e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Shutting down Academic Management Backend Application...");
        SemesterLifecycleScheduler.stop();
        JpaUtil.shutdown();
        log.info("Backend shutdown completed cleanly.");
    }
}
