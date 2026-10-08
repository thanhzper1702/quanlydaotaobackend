package com.university.modules.student;

import com.university.core.model.HocPhan;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.SinhVien;
import com.university.core.model.YeuCauMoLop;
import com.university.core.model.enums.TrangThaiHocPhan;
import com.university.core.model.enums.TrangThaiLop;
import com.university.core.repository.AbstractJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class StudentRepositoryImpl extends AbstractJpaRepository<SinhVien, String> implements StudentRepository {

    public StudentRepositoryImpl() {
        super(SinhVien.class);
    }

    @Override
    public Optional<SinhVien> findByMaSV(String maSV) {
        return execute(em -> {
            try {
                SinhVien sv = em.createQuery(
                        "SELECT s FROM SinhVien s LEFT JOIN FETCH s.nguoiDung WHERE s.maSV = :maSV AND s.deleted = false", SinhVien.class)
                        .setParameter("maSV", maSV)
                        .getSingleResult();
                return Optional.of(sv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<HocPhan> findHocPhanByMaHP(String maHP) {
        return execute(em -> {
            try {
                HocPhan hp = em.createQuery(
                        "SELECT h FROM HocPhan h WHERE h.maHP = :maHP AND h.deleted = false", HocPhan.class)
                        .setParameter("maHP", maHP)
                        .getSingleResult();
                return Optional.of(hp);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public String findPhongHocByMaLHP(String maLHP) {
        return execute(em -> {
            List<String> list = em.createQuery(
                    "SELECT l.phongHoc FROM LichHoc l WHERE l.lopHocPhan.maLHP = :maLHP AND l.deleted = false ORDER BY l.ngayHoc ASC", String.class)
                    .setParameter("maLHP", maLHP)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public List<LopHocPhan> findOpenClasses(String maHocKy, String keyword, int page, int limit) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder(
                    "SELECT l FROM LopHocPhan l " +
                    "LEFT JOIN FETCH l.monHoc " +
                    "LEFT JOIN FETCH l.giangVien gv " +
                    "LEFT JOIN FETCH gv.nguoiDung " +
                    "LEFT JOIN FETCH l.hocKy " +
                    "WHERE l.deleted = false AND l.trangThai = :trangThai");
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                sb.append(" AND l.hocKy.maHocKy = :maHocKy");
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                sb.append(" AND (LOWER(l.tenLHP) LIKE :kw OR LOWER(l.maLHP) LIKE :kw OR LOWER(l.monHoc.tenHP) LIKE :kw)");
            }
            sb.append(" ORDER BY l.maLHP ASC");

            TypedQuery<LopHocPhan> query = em.createQuery(sb.toString(), LopHocPhan.class)
                    .setParameter("trangThai", TrangThaiLop.MODANGKY);

            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                query.setParameter("maHocKy", maHocKy);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            }

            int firstResult = (page - 1) * limit;
            query.setFirstResult(firstResult);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }

    @Override
    public long countOpenClasses(String maHocKy, String keyword) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder("SELECT COUNT(l) FROM LopHocPhan l WHERE l.deleted = false AND l.trangThai = :trangThai");
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                sb.append(" AND l.hocKy.maHocKy = :maHocKy");
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                sb.append(" AND (LOWER(l.tenLHP) LIKE :kw OR LOWER(l.maLHP) LIKE :kw OR LOWER(l.monHoc.tenHP) LIKE :kw)");
            }

            TypedQuery<Long> query = em.createQuery(sb.toString(), Long.class)
                    .setParameter("trangThai", TrangThaiLop.MODANGKY);

            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                query.setParameter("maHocKy", maHocKy);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            }

            return query.getSingleResult();
        });
    }

    @Override
    public Optional<LopHocPhan> findClassByMaLHP(String maLHP) {
        return execute(em -> {
            try {
                LopHocPhan lhp = em.createQuery(
                        "SELECT l FROM LopHocPhan l " +
                        "LEFT JOIN FETCH l.monHoc " +
                        "LEFT JOIN FETCH l.giangVien gv " +
                        "LEFT JOIN FETCH gv.nguoiDung " +
                        "LEFT JOIN FETCH l.hocKy " +
                        "WHERE l.maLHP = :maLHP AND l.deleted = false", LopHocPhan.class)
                        .setParameter("maLHP", maLHP)
                        .getSingleResult();
                return Optional.of(lhp);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public LopHocPhan updateClass(LopHocPhan lhp) {
        return executeInTransaction((Function<EntityManager, LopHocPhan>) em -> em.merge(lhp));
    }

    @Override
    public List<HoSoHocPhan> findRegisteredClasses(String maSV) {
        return execute(em -> em.createQuery(
                "SELECT h FROM HoSoHocPhan h " +
                "JOIN FETCH h.sinhVien sv " +
                "LEFT JOIN FETCH sv.nguoiDung " +
                "JOIN FETCH h.lopHocPhan l " +
                "JOIN FETCH l.monHoc " +
                "LEFT JOIN FETCH l.giangVien gv " +
                "LEFT JOIN FETCH gv.nguoiDung " +
                "LEFT JOIN FETCH l.hocKy " +
                "WHERE h.sinhVien.maSV = :maSV AND h.deleted = false ORDER BY h.ngayDangKy DESC", HoSoHocPhan.class)
                .setParameter("maSV", maSV)
                .getResultList());
    }

    @Override
    public List<HoSoHocPhan> findRegisteredClassesByHocKy(String maSV, String maHocKy) {
        return execute(em -> em.createQuery(
                "SELECT h FROM HoSoHocPhan h " +
                "JOIN FETCH h.sinhVien sv " +
                "LEFT JOIN FETCH sv.nguoiDung " +
                "JOIN FETCH h.lopHocPhan l " +
                "JOIN FETCH l.monHoc " +
                "LEFT JOIN FETCH l.giangVien gv " +
                "LEFT JOIN FETCH gv.nguoiDung " +
                "LEFT JOIN FETCH l.hocKy " +
                "WHERE h.sinhVien.maSV = :maSV AND l.hocKy.maHocKy = :maHocKy AND h.deleted = false", HoSoHocPhan.class)
                .setParameter("maSV", maSV)
                .setParameter("maHocKy", maHocKy)
                .getResultList());
    }

    @Override
    public Optional<HoSoHocPhan> findEnrollment(String maSV, String maLHP) {
        return execute(em -> {
            try {
                HoSoHocPhan hs = em.createQuery(
                        "SELECT h FROM HoSoHocPhan h " +
                        "LEFT JOIN FETCH h.sinhVien sv " +
                        "LEFT JOIN FETCH sv.nguoiDung " +
                        "LEFT JOIN FETCH h.lopHocPhan l " +
                        "LEFT JOIN FETCH l.monHoc " +
                        "LEFT JOIN FETCH l.giangVien gv " +
                        "LEFT JOIN FETCH gv.nguoiDung " +
                        "LEFT JOIN FETCH l.hocKy " +
                        "WHERE h.sinhVien.maSV = :maSV AND h.lopHocPhan.maLHP = :maLHP AND h.deleted = false", HoSoHocPhan.class)
                        .setParameter("maSV", maSV)
                        .setParameter("maLHP", maLHP)
                        .getSingleResult();
                return Optional.of(hs);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<HoSoHocPhan> findEnrollmentById(Long maHoSo) {
        return execute(em -> {
            try {
                HoSoHocPhan hs = em.createQuery(
                        "SELECT h FROM HoSoHocPhan h " +
                        "LEFT JOIN FETCH h.sinhVien sv " +
                        "LEFT JOIN FETCH sv.nguoiDung " +
                        "LEFT JOIN FETCH h.lopHocPhan l " +
                        "LEFT JOIN FETCH l.monHoc " +
                        "LEFT JOIN FETCH l.giangVien gv " +
                        "LEFT JOIN FETCH gv.nguoiDung " +
                        "LEFT JOIN FETCH l.hocKy " +
                        "WHERE h.maHoSo = :maHoSo AND h.deleted = false", HoSoHocPhan.class)
                        .setParameter("maHoSo", maHoSo)
                        .getSingleResult();
                return Optional.of(hs);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public HoSoHocPhan saveEnrollment(HoSoHocPhan hoSo) {
        return executeInTransaction((Function<EntityManager, HoSoHocPhan>) em -> {
            if (hoSo.getMaHoSo() == null) {
                em.persist(hoSo);
                return hoSo;
            } else {
                return em.merge(hoSo);
            }
        });
    }

    @Override
    public void deleteEnrollment(HoSoHocPhan hoSo) {
        executeInTransaction((Consumer<EntityManager>) em -> {
            HoSoHocPhan managed = em.find(HoSoHocPhan.class, hoSo.getMaHoSo());
            if (managed != null) {
                managed.setDeleted(true);
                managed.setTrangThai(TrangThaiHocPhan.DAHUY);
                em.merge(managed);
            }
        });
    }

    @Override
    public List<HoSoHocPhan> findStudentGrades(String maSV) {
        return execute(em -> em.createQuery(
                "SELECT h FROM HoSoHocPhan h " +
                "JOIN FETCH h.sinhVien sv " +
                "LEFT JOIN FETCH sv.nguoiDung " +
                "JOIN FETCH h.lopHocPhan l " +
                "JOIN FETCH l.monHoc " +
                "JOIN FETCH l.hocKy " +
                "WHERE h.sinhVien.maSV = :maSV AND h.deleted = false ORDER BY l.hocKy.thoiGianBD DESC", HoSoHocPhan.class)
                .setParameter("maSV", maSV)
                .getResultList());
    }

    @Override
    public List<LichHoc> findStudentSchedule(String maSV, String maHocKy) {
        return execute(em -> em.createQuery(
                "SELECT l FROM LichHoc l " +
                "JOIN FETCH l.lopHocPhan lhp " +
                "LEFT JOIN FETCH lhp.monHoc " +
                "LEFT JOIN FETCH lhp.giangVien gv " +
                "LEFT JOIN FETCH gv.nguoiDung " +
                "WHERE lhp.hocKy.maHocKy = :maHocKy " +
                "  AND lhp.maLHP IN (" +
                "      SELECT h.lopHocPhan.maLHP FROM HoSoHocPhan h " +
                "      WHERE h.sinhVien.maSV = :maSV AND h.deleted = false" +
                "  ) " +
                "  AND l.deleted = false ORDER BY l.ngayHoc ASC", LichHoc.class)
                .setParameter("maSV", maSV)
                .setParameter("maHocKy", maHocKy)
                .getResultList());
    }

    @Override
    public List<LopHocPhan> findStudentEnrolledClasses(String maSV, String maHocKy) {
        return execute(em -> em.createQuery(
                "SELECT lhp FROM LopHocPhan lhp " +
                "LEFT JOIN FETCH lhp.monHoc " +
                "LEFT JOIN FETCH lhp.hocKy " +
                "LEFT JOIN FETCH lhp.giangVien gv " +
                "LEFT JOIN FETCH gv.nguoiDung " +
                "WHERE lhp.hocKy.maHocKy = :maHocKy " +
                "  AND lhp.maLHP IN (" +
                "      SELECT h.lopHocPhan.maLHP FROM HoSoHocPhan h " +
                "      WHERE h.sinhVien.maSV = :maSV AND h.deleted = false AND (h.trangThai = :dangHoc OR h.trangThai = :daDangKy)" +
                "  ) " +
                "  AND lhp.deleted = false", LopHocPhan.class)
                .setParameter("maSV", maSV)
                .setParameter("maHocKy", maHocKy)
                .setParameter("dangHoc", TrangThaiHocPhan.DANGHOC)
                .setParameter("daDangKy", TrangThaiHocPhan.DADANGKY)
                .getResultList());
    }

    @Override
    public YeuCauMoLop saveClassRequest(YeuCauMoLop request) {
        return executeInTransaction((Function<EntityManager, YeuCauMoLop>) em -> {
            if (request.getMaYeuCau() == null) {
                em.persist(request);
                return request;
            } else {
                YeuCauMoLop existing = em.find(YeuCauMoLop.class, request.getMaYeuCau());
                if (existing == null) {
                    em.persist(request);
                    return request;
                } else {
                    return em.merge(request);
                }
            }
        });
    }

    @Override
    public List<YeuCauMoLop> findClassRequestsByStudent(String maSV) {
        return execute(em -> em.createQuery(
                "SELECT y FROM YeuCauMoLop y " +
                "JOIN FETCH y.hocPhan " +
                "JOIN FETCH y.sinhVien sv " +
                "LEFT JOIN FETCH sv.nguoiDung " +
                "WHERE y.sinhVien.maSV = :maSV AND y.deleted = false ORDER BY y.ngayTao DESC", YeuCauMoLop.class)
                .setParameter("maSV", maSV)
                .getResultList());
    }
}
