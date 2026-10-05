package com.university.core.util;

import com.university.core.config.EnvironmentLoader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Utility for JJWT 0.12.x.
 */
public final class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final String DEFAULT_SECRET = "UniversityAcademicManagementSecretKeyShouldBeAtLeast256BitsLongForHS256SecurityRequirement!";
    private static final long DEFAULT_ACCESS_TOKEN_EXPIRATION = 3600000L; // 1 hour
    private static final long DEFAULT_REFRESH_TOKEN_EXPIRATION = 604800000L; // 7 days

    private static final SecretKey SECRET_KEY;
    private static final long ACCESS_TOKEN_EXPIRATION;
    private static final long REFRESH_TOKEN_EXPIRATION;

    static {
        String secret = EnvironmentLoader.get("JWT_SECRET", DEFAULT_SECRET);
        SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        ACCESS_TOKEN_EXPIRATION = EnvironmentLoader.getInt("JWT_EXPIRATION_MS", (int) DEFAULT_ACCESS_TOKEN_EXPIRATION);
        REFRESH_TOKEN_EXPIRATION = EnvironmentLoader.getInt("JWT_REFRESH_EXPIRATION_MS", (int) DEFAULT_REFRESH_TOKEN_EXPIRATION);
    }

    private JwtUtil() {
    }

    public static String generateAccessToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String generateAccessToken(String cccd, String userCode, String role, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userCode", userCode);
        claims.put("role", role);
        claims.put("email", email);
        return generateAccessToken(cccd, claims);
    }

    public static String generateRefreshToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
                .subject(subject)
                .claim("type", "REFRESH")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }

    public static boolean validateToken(String token) {
        return parseToken(token) != null;
    }

    public static String getSubject(String token) {
        Claims claims = parseToken(token);
        return (claims != null) ? claims.getSubject() : null;
    }

    public static String extractCccd(String token) {
        return getSubject(token);
    }

    public static String extractUserCode(String token) {
        Claims claims = parseToken(token);
        return (claims != null) ? claims.get("userCode", String.class) : null;
    }

    public static String extractRole(String token) {
        Claims claims = parseToken(token);
        return (claims != null) ? claims.get("role", String.class) : null;
    }

    public static String extractEmail(String token) {
        Claims claims = parseToken(token);
        return (claims != null) ? claims.get("email", String.class) : null;
    }
}
