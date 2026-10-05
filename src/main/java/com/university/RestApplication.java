package com.university;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * JAX-RS 3.1 Application configuration extending Jersey ResourceConfig.
 * Scans all feature module resources and core filters under /api base path.
 */
@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {

    public RestApplication() {
        // Scan all feature modules and core JAX-RS providers/filters
        packages("com.university.modules", "com.university.core.filter");

        // Enable Jackson JSON (de)serialization
        register(JacksonFeature.class);
    }
}
