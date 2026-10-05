package com.university.core.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps environment variables to JPA/Hibernate and HikariCP settings.
 */
public final class DbConfig {

    private DbConfig() {
    }

    public static Map<String, Object> getJpaProperties() {
        Map<String, Object> props = new HashMap<>();

        String dbUrl = EnvironmentLoader.get("DB_URL", "jdbc:postgresql://localhost:5432/quanlydaotao");
        String dbUser = EnvironmentLoader.get("DB_USER", "postgres");
        String dbPassword = EnvironmentLoader.get("DB_PASSWORD", "postgres");
        String dbDriver = EnvironmentLoader.get("DB_DRIVER", "org.postgresql.Driver");

        props.put("jakarta.persistence.jdbc.url", dbUrl);
        props.put("jakarta.persistence.jdbc.user", dbUser);
        props.put("jakarta.persistence.jdbc.password", dbPassword);
        props.put("jakarta.persistence.jdbc.driver", dbDriver);

        // HikariCP integration
        props.put("hibernate.hikari.maximumPoolSize", EnvironmentLoader.get("DB_POOL_MAX_SIZE", "10"));
        props.put("hibernate.hikari.minimumIdle", EnvironmentLoader.get("DB_POOL_MIN_IDLE", "2"));
        props.put("hibernate.hikari.idleTimeout", EnvironmentLoader.get("DB_POOL_IDLE_TIMEOUT", "30000"));
        props.put("hibernate.hikari.maxLifetime", EnvironmentLoader.get("DB_POOL_MAX_LIFETIME", "600000"));
        props.put("hibernate.hikari.connectionTimeout", EnvironmentLoader.get("DB_POOL_CONN_TIMEOUT", "20000"));

        // Schema & Dialect
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.show_sql", EnvironmentLoader.get("HIBERNATE_SHOW_SQL", "true"));
        props.put("hibernate.format_sql", EnvironmentLoader.get("HIBERNATE_FORMAT_SQL", "true"));

        return props;
    }
}
