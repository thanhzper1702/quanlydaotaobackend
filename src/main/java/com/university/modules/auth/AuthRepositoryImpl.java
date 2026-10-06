package com.university.modules.auth;

import com.university.core.model.GiangVien;
import com.university.core.model.NguoiDung;
import com.university.core.model.NhanVien;
import com.university.core.model.SinhVien;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.TruongPhongDaoTao;
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
    public Optional<NguoiDung> findByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                NguoiDung user = em.createQuery(
                        "SELECT u FROM NguoiDung u WHERE u.refreshToken = :token AND u.deleted = false", NguoiDung.class)
                        .setParameter("token", refreshToken)
                        .getSingleResult();
                return Optional.of(user);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public boolean updateRefreshToken(String cccd, String refreshToken) {
        return executeInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE NguoiDung u SET u.refreshToken = :token WHERE u.cccd = :cccd AND u.deleted = false")
                    .setParameter("token", refreshToken)
                    .setParameter("cccd", cccd)
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public boolean clearRefreshToken(String cccd) {
        return executeInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE NguoiDung u SET u.refreshToken = NULL WHERE u.cccd = :cccd AND u.deleted = false")
                    .setParameter("cccd", cccd)
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public boolean updatePassword(String cccd, String newHashedPassword) {
        return executeInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE NguoiDung u SET u.matKhauHashed = :pwd WHERE u.cccd = :cccd AND u.deleted = false")
                    .setParameter("pwd", newHashedPassword)
                    .setParameter("cccd", cccd)
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public Optional<SinhVien> findStudentByCccd(String cccd) {
        return execute(em -> {
            try {
                SinhVien sv = em.createQuery(
                        "SELECT s FROM SinhVien s WHERE s.nguoiDung.cccd = :cccd AND s.deleted = false", SinhVien.class)
                        .setParameter("cccd", cccd)
                        .getSingleResult();
                return Optional.of(sv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<NhanVien> findStaffByCccd(String cccd) {
        return execute(em -> {
            try {
                NhanVien nv = em.createQuery(
                        "SELECT n FROM NhanVien n WHERE n.nguoiDung.cccd = :cccd AND n.deleted = false", NhanVien.class)
                        .setParameter("cccd", cccd)
                        .getSingleResult();
                return Optional.of(nv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<GiangVien> findLecturerByCccd(String cccd) {
        return execute(em -> {
            try {
                GiangVien gv = em.createQuery(
                        "SELECT g FROM GiangVien g WHERE g.nguoiDung.cccd = :cccd AND g.deleted = false", GiangVien.class)
                        .setParameter("cccd", cccd)
                        .getSingleResult();
                return Optional.of(gv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<TroLyDaoTao> findAssistantByCccd(String cccd) {
        return execute(em -> {
            try {
                TroLyDaoTao tl = em.createQuery(
                        "SELECT t FROM TroLyDaoTao t WHERE t.nguoiDung.cccd = :cccd AND t.deleted = false", TroLyDaoTao.class)
                        .setParameter("cccd", cccd)
                        .getSingleResult();
                return Optional.of(tl);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<TruongPhongDaoTao> findHeadOfficeByCccd(String cccd) {
        return execute(em -> {
            try {
                TruongPhongDaoTao tp = em.createQuery(
                        "SELECT t FROM TruongPhongDaoTao t WHERE t.nguoiDung.cccd = :cccd AND t.deleted = false", TruongPhongDaoTao.class)
                        .setParameter("cccd", cccd)
                        .getSingleResult();
                return Optional.of(tp);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }
}
