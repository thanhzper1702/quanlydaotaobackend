package com.university.core.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for BCrypt password hashing and verification.
 */
public final class PasswordUtil {

    private static final int WORKLOAD = 12;

    private PasswordUtil() {
    }

    public static String hash(String plainTextPassword) {
        if (plainTextPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(plainTextPassword, salt);
    }

    public static boolean verify(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Aliases for convenience
    public static String hashPassword(String plainText) {
        return hash(plainText);
    }

    public static boolean checkPassword(String plainText, String hashed) {
        return verify(plainText, hashed);
    }
}
