package com.university.modules.profile;

import com.university.core.model.GiangVien;
import com.university.core.model.NguoiDung;
import com.university.core.model.NhanVien;
import com.university.core.model.SinhVien;
import com.university.core.repository.AbstractJpaRepository;
import jakarta.persistence.NoResultException;
import java.util.Optional;

public class ProfileRepositoryImpl extends AbstractJpaRepository<NguoiDung, String> implements ProfileRepository {

    public ProfileRepositoryImpl() {
        super(NguoiDung.class);
    }

    @Override
    public Optional<NguoiDung> findUserByCccd(String cccd) {
        return findById(cccd);
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
    public boolean updateContactInfo(String cccd, String soDienThoai, String diaChi) {
        return execute(em -> {
            int updated = em.createQuery(
                    "UPDATE NguoiDung u SET u.soDienThoai = :phone, u.diaChi = :addr " +
                    "WHERE u.cccd = :cccd AND u.deleted = false")
                    .setParameter("phone", soDienThoai)
                    .setParameter("addr", diaChi)
                    .setParameter("cccd", cccd)
                    .executeUpdate();
            return updated > 0;
        });
    }
}
