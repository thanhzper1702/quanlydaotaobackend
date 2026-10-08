package com.university.modules.profile;

import com.university.core.model.GiangVien;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.NguoiDung;
import com.university.core.model.NhanVien;
import com.university.core.model.SinhVien;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.TruongPhongDaoTao;
import com.university.core.repository.AbstractJpaRepository;
import jakarta.persistence.NoResultException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProfileRepositoryImpl extends AbstractJpaRepository<NguoiDung, String> implements ProfileRepository {

    public ProfileRepositoryImpl() {
        super(NguoiDung.class);
    }

    @Override
    public Optional<NguoiDung> findUserByCccd(String cccd) {
        if (cccd == null || cccd.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                NguoiDung u = em.createQuery(
                        "SELECT u FROM NguoiDung u WHERE u.cccd = :cccd AND u.deleted = false", NguoiDung.class)
                        .setParameter("cccd", cccd.trim())
                        .getSingleResult();
                return Optional.of(u);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<NguoiDung> findUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                NguoiDung u = em.createQuery(
                        "SELECT u FROM NguoiDung u WHERE LOWER(u.email) = :email AND u.deleted = false", NguoiDung.class)
                        .setParameter("email", email.trim().toLowerCase())
                        .getSingleResult();
                return Optional.of(u);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<SinhVien> findStudentByCccd(String cccd) {
        if (cccd == null || cccd.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                SinhVien sv = em.createQuery(
                        "SELECT s FROM SinhVien s LEFT JOIN FETCH s.nguoiDung WHERE s.nguoiDung.cccd = :cccd AND s.deleted = false", SinhVien.class)
                        .setParameter("cccd", cccd.trim())
                        .getSingleResult();
                return Optional.of(sv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<SinhVien> findStudentByMaSV(String maSV) {
        if (maSV == null || maSV.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                SinhVien sv = em.createQuery(
                        "SELECT s FROM SinhVien s LEFT JOIN FETCH s.nguoiDung WHERE s.maSV = :maSV AND s.deleted = false", SinhVien.class)
                        .setParameter("maSV", maSV.trim())
                        .getSingleResult();
                return Optional.of(sv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<GiangVien> findLecturerByCccd(String cccd) {
        if (cccd == null || cccd.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                GiangVien gv = em.createQuery(
                        "SELECT g FROM GiangVien g LEFT JOIN FETCH g.nguoiDung WHERE g.nguoiDung.cccd = :cccd AND g.deleted = false", GiangVien.class)
                        .setParameter("cccd", cccd.trim())
                        .getSingleResult();
                return Optional.of(gv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<GiangVien> findLecturerByMaNV(String maNV) {
        if (maNV == null || maNV.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                GiangVien gv = em.createQuery(
                        "SELECT g FROM GiangVien g LEFT JOIN FETCH g.nguoiDung WHERE g.maNV = :maNV AND g.deleted = false", GiangVien.class)
                        .setParameter("maNV", maNV.trim())
                        .getSingleResult();
                return Optional.of(gv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<TroLyDaoTao> findAssistantByCccd(String cccd) {
        if (cccd == null || cccd.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                TroLyDaoTao tl = em.createQuery(
                        "SELECT t FROM TroLyDaoTao t LEFT JOIN FETCH t.nguoiDung WHERE t.nguoiDung.cccd = :cccd AND t.deleted = false", TroLyDaoTao.class)
                        .setParameter("cccd", cccd.trim())
                        .getSingleResult();
                return Optional.of(tl);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<TroLyDaoTao> findAssistantByMaNV(String maNV) {
        if (maNV == null || maNV.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                TroLyDaoTao tl = em.createQuery(
                        "SELECT t FROM TroLyDaoTao t LEFT JOIN FETCH t.nguoiDung WHERE t.maNV = :maNV AND t.deleted = false", TroLyDaoTao.class)
                        .setParameter("maNV", maNV.trim())
                        .getSingleResult();
                return Optional.of(tl);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<TruongPhongDaoTao> findHeadOfficeByCccd(String cccd) {
        if (cccd == null || cccd.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                TruongPhongDaoTao tp = em.createQuery(
                        "SELECT t FROM TruongPhongDaoTao t LEFT JOIN FETCH t.nguoiDung WHERE t.nguoiDung.cccd = :cccd AND t.deleted = false", TruongPhongDaoTao.class)
                        .setParameter("cccd", cccd.trim())
                        .getSingleResult();
                return Optional.of(tp);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<TruongPhongDaoTao> findHeadOfficeByMaNV(String maNV) {
        if (maNV == null || maNV.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                TruongPhongDaoTao tp = em.createQuery(
                        "SELECT t FROM TruongPhongDaoTao t LEFT JOIN FETCH t.nguoiDung WHERE t.maNV = :maNV AND t.deleted = false", TruongPhongDaoTao.class)
                        .setParameter("maNV", maNV.trim())
                        .getSingleResult();
                return Optional.of(tp);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<NhanVien> findStaffByCccd(String cccd) {
        if (cccd == null || cccd.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                NhanVien nv = em.createQuery(
                        "SELECT n FROM NhanVien n LEFT JOIN FETCH n.nguoiDung WHERE n.nguoiDung.cccd = :cccd AND n.deleted = false", NhanVien.class)
                        .setParameter("cccd", cccd.trim())
                        .getSingleResult();
                return Optional.of(nv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<NhanVien> findStaffByMaNV(String maNV) {
        if (maNV == null || maNV.isBlank()) {
            return Optional.empty();
        }
        return execute(em -> {
            try {
                NhanVien nv = em.createQuery(
                        "SELECT n FROM NhanVien n LEFT JOIN FETCH n.nguoiDung WHERE n.maNV = :maNV AND n.deleted = false", NhanVien.class)
                        .setParameter("maNV", maNV.trim())
                        .getSingleResult();
                return Optional.of(nv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public List<HoSoHocPhan> findStudentGradeRecords(String maSV) {
        if (maSV == null || maSV.isBlank()) {
            return Collections.emptyList();
        }
        return execute(em -> em.createQuery(
                "SELECT h FROM HoSoHocPhan h " +
                "JOIN FETCH h.lopHocPhan l " +
                "JOIN FETCH l.monHoc " +
                "WHERE h.sinhVien.maSV = :maSV AND h.deleted = false", HoSoHocPhan.class)
                .setParameter("maSV", maSV.trim())
                .getResultList());
    }

    @Override
    public boolean updatePassword(String cccd, String newHashedPassword) {
        if (cccd == null || newHashedPassword == null) {
            return false;
        }
        return executeInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE NguoiDung u SET u.matKhauHashed = :newHash WHERE u.cccd = :cccd AND u.deleted = false")
                    .setParameter("newHash", newHashedPassword)
                    .setParameter("cccd", cccd.trim())
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public boolean updateContactInfo(String cccd, String soDienThoai, String diaChi) {
        if (cccd == null) {
            return false;
        }
        return executeInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE NguoiDung u SET u.soDienThoai = :phone, u.diaChi = :addr " +
                    "WHERE u.cccd = :cccd AND u.deleted = false")
                    .setParameter("phone", soDienThoai)
                    .setParameter("addr", diaChi)
                    .setParameter("cccd", cccd.trim())
                    .executeUpdate();
            return updated > 0;
        });
    }
}
