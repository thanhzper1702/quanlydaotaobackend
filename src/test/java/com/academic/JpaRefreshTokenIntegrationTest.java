package com.academic;

import com.university.core.config.JpaUtil;
import com.university.core.model.NguoiDung;
import com.university.core.util.PasswordUtil;
import com.university.modules.auth.AuthRepository;
import com.university.modules.auth.AuthRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JpaRefreshTokenIntegrationTest {

    private AuthRepository authRepository;
    private final String testCccd = "TEST_CCCD_999999";
    private final String testEmail = "test_auth_user@university.edu.vn";

    @BeforeEach
    public void setup() {
        authRepository = new AuthRepositoryImpl();
        cleanupUser();

        // Create a test user directly with JPA
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            NguoiDung user = new NguoiDung();
            user.setCccd(testCccd);
            user.setHoTen("Nguyễn Test Refresh");
            user.setEmail(testEmail);
            user.setNgaySinh(LocalDate.of(2000, 1, 1));
            user.setMatKhauHashed(PasswordUtil.hash("TestPass@123"));
            user.setRefreshToken("initial-token-12345");
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void cleanup() {
        cleanupUser();
    }

    private void cleanupUser() {
        try {
            EntityManager em = JpaUtil.getEntityManager();
            em.getTransaction().begin();
            em.createQuery("DELETE FROM NguoiDung u WHERE u.cccd = :cccd")
                    .setParameter("cccd", testCccd)
                    .executeUpdate();
            em.getTransaction().commit();
            em.close();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testRefreshTokenPersistenceAndQueries() {
        // 1. Verify findByRefreshToken
        Optional<NguoiDung> found = authRepository.findByRefreshToken("initial-token-12345");
        assertTrue(found.isPresent(), "User must be found by refresh token");
        assertEquals(testCccd, found.get().getCccd());
        assertEquals("initial-token-12345", found.get().getRefreshToken());

        // 2. Verify updateRefreshToken
        boolean updated = authRepository.updateRefreshToken(testCccd, "updated-token-67890");
        assertTrue(updated);

        Optional<NguoiDung> updatedUser = authRepository.findByCccd(testCccd);
        assertTrue(updatedUser.isPresent());
        assertEquals("updated-token-67890", updatedUser.get().getRefreshToken());

        // 3. Verify clearRefreshToken
        boolean cleared = authRepository.clearRefreshToken(testCccd);
        assertTrue(cleared);

        Optional<NguoiDung> clearedUser = authRepository.findByCccd(testCccd);
        assertTrue(clearedUser.isPresent());
        assertNull(clearedUser.get().getRefreshToken());
    }
}
