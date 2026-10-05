package com.university.modules.auth;

import com.university.core.model.NguoiDung;
import com.university.core.repository.AbstractJpaRepository;
import jakarta.persistence.NoResultException;
import java.util.Optional;

public class AuthRepositoryImpl extends AbstractJpaRepository<NguoiDung, String> implements AuthRepository {

    public AuthRepositoryImpl() {
        super(NguoiDung.class);
    }

    @Override
    public Optional<NguoiDung> findByEmail(String email) {
        return execute(em -> {
            try {
                NguoiDung user = em.createQuery(
                        "SELECT u FROM NguoiDung u WHERE u.email = :email AND u.deleted = false", NguoiDung.class)
                        .setParameter("email", email)
                        .getSingleResult();
                return Optional.of(user);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<NguoiDung> findByCccd(String cccd) {
        return findById(cccd);
    }

    @Override
    public boolean updatePassword(String cccd, String newHashedPassword) {
        return execute(em -> {
            int updated = em.createQuery(
                    "UPDATE NguoiDung u SET u.matKhauHashed = :pwd WHERE u.cccd = :cccd AND u.deleted = false")
                    .setParameter("pwd", newHashedPassword)
                    .setParameter("cccd", cccd)
                    .executeUpdate();
            return updated > 0;
        });
    }
}
