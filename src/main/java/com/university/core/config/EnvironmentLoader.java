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
    private static final java.util.Map<String, String> classpathEnvMap = new java.util.concurrent.ConcurrentHashMap<>();

    static {
        try {
            File envFile = new File(".env");
            if (envFile.exists()) {
                dotenv = Dotenv.configure()
                        .ignoreIfMissing()
                        .load();
                log.info(".env file successfully loaded from working directory.");
            } else {
                loadFromClasspath();
            }
        } catch (Exception e) {
            log.warn("Failed to initialize dotenv: {}", e.getMessage());
            loadFromClasspath();
        }
    }

    private static void loadFromClasspath() {
        try (java.io.InputStream is = EnvironmentLoader.class.getResourceAsStream("/.env")) {
            if (is != null) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String k = line.substring(0, eqIdx).trim();
                            String v = line.substring(eqIdx + 1).trim();
                            classpathEnvMap.put(k, v);
                        }
                    }
                    log.info(".env file successfully loaded from classpath resources.");
                }
            } else {
                log.info("No local .env file found in working directory or classpath; falling back to system environment variables.");
            }
        } catch (Exception ex) {
            log.warn("Could not read .env from classpath: {}", ex.getMessage());
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
            String dotVal = dotenv.get(key);
            if (dotVal != null && !dotVal.trim().isEmpty()) {
                return dotVal;
            }
        }
        String cpVal = classpathEnvMap.get(key);
        if (cpVal != null && !cpVal.trim().isEmpty()) {
            return cpVal;
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

    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key);
        if (val != null && !val.trim().isEmpty()) {
            return Boolean.parseBoolean(val.trim());
        }
        return defaultValue;
    }
}
