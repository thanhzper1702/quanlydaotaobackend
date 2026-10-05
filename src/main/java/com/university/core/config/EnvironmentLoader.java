package com.university.core.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Utility class to load environment variables from system environment or local .env file.
 */
public final class EnvironmentLoader {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentLoader.class);
    private static Dotenv dotenv;

    static {
        try {
            File envFile = new File(".env");
            if (envFile.exists()) {
                dotenv = Dotenv.configure()
                        .ignoreIfMissing()
                        .load();
                log.info(".env file successfully loaded.");
            } else {
                log.info("No local .env file found; falling back to system environment variables.");
            }
        } catch (Exception e) {
            log.warn("Failed to initialize dotenv: {}", e.getMessage());
        }
    }

    private EnvironmentLoader() {
    }

    public static String get(String key) {
        String val = System.getenv(key);
        if (val != null && !val.trim().isEmpty()) {
            return val;
        }
        if (dotenv != null) {
            return dotenv.get(key);
        }
        return null;
    }

    public static String get(String key, String defaultValue) {
        String val = get(key);
        return (val != null && !val.trim().isEmpty()) ? val : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String val = get(key);
        if (val != null && !val.trim().isEmpty()) {
            try {
                return Integer.parseInt(val.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }
}
