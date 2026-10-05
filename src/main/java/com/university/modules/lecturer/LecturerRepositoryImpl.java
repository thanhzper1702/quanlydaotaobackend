package com.university.modules.lecturer;

import com.university.core.model.DiemDanh;
import com.university.core.model.GiangVien;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.repository.AbstractJpaRepository;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;

public class LecturerRepositoryImpl extends AbstractJpaRepository<GiangVien, String> implements LecturerRepository {

    public LecturerRepositoryImpl() {
        super(GiangVien.class);
    }

    @Override
    public Optional<GiangVien> findByMaGV(String maGV) {
        return execute(em -> {
            try {
                GiangVien gv = em.createQuery(
                        "SELECT g FROM GiangVien g WHERE g.maNV = :maGV AND g.deleted = false", GiangVien.class)
                        .setParameter("maGV", maGV)
                        .getSingleResult();
                return Optional.of(gv);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public List<LopHocPhan> findTeachingClasses(String maGV, String maHocKy) {
        return execute(em -> {
            String jpql = "SELECT l FROM LopHocPhan l JOIN FETCH l.monHoc JOIN FETCH l.hocKy " +
                    "WHERE l.giangVien.maNV = :maGV AND l.deleted = false";
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                jpql += " AND l.hocKy.maHocKy = :maHocKy";
            }
            jpql += " ORDER BY l.maLHP ASC";

            var query = em.createQuery(jpql, LopHocPhan.class).setParameter("maGV", maGV);
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                query.setParameter("maHocKy", maHocKy);
            }
            return query.getResultList();
        });
    }

    @Override
    public List<LopHocPhan> findLecturerAssignedClasses(String maGV, String maHocKy) {
        return execute(em -> {
            String jpql = "SELECT l FROM LopHocPhan l WHERE l.giangVien.maNV = :maGV AND l.deleted = false";
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                jpql += " AND l.hocKy.maHocKy = :maHocKy";
            }
            var query = em.createQuery(jpql, LopHocPhan.class).setParameter("maGV", maGV);
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                query.setParameter("maHocKy", maHocKy);
            }
            return query.getResultList();
        });
    }

    @Override
    public Optional<LopHocPhan> findClassById(String maLHP) {
        return execute(em -> {
            try {
                LopHocPhan lhp = em.createQuery(
                        "SELECT l FROM LopHocPhan l JOIN FETCH l.monHoc JOIN FETCH l.hocKy WHERE l.maLHP = :maLHP AND l.deleted = false", LopHocPhan.class)
                        .setParameter("maLHP", maLHP)
                        .getSingleResult();
                return Optional.of(lhp);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public List<HoSoHocPhan> findStudentsAndGradesInClass(String maLHP) {
        return execute(em -> em.createQuery(
                "SELECT h FROM HoSoHocPhan h JOIN FETCH h.sinhVien s JOIN FETCH s.nguoiDung " +
                "WHERE h.lopHocPhan.maLHP = :maLHP AND h.deleted = false ORDER BY s.maSV ASC", HoSoHocPhan.class)
                .setParameter("maLHP", maLHP)
                .getResultList());
    }

    @Override
    public Optional<HoSoHocPhan> findGradeRecord(Long maHoSo) {
        return execute(em -> {
            try {
                HoSoHocPhan h = em.createQuery(
                        "SELECT h FROM HoSoHocPhan h WHERE h.maHoSo = :maHoSo AND h.deleted = false", HoSoHocPhan.class)
                        .setParameter("maHoSo", maHoSo)
                        .getSingleResult();
                return Optional.of(h);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public HoSoHocPhan saveGrade(HoSoHocPhan record) {
        return execute(em -> {
            if (record.getMaHoSo() == null) {
                em.persist(record);
                return record;
            } else {
                return em.merge(record);
            }
        });
    }

    @Override
    public List<LichHoc> findTeachingSchedule(String maGV, String maHocKy) {
        return execute(em -> {
            String jpql = "SELECT l FROM LichHoc l JOIN FETCH l.lopHocPhan lhp JOIN FETCH lhp.monHoc " +
                    "WHERE lhp.giangVien.maNV = :maGV AND l.deleted = false";
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                jpql += " AND lhp.hocKy.maHocKy = :maHocKy";
            }
            jpql += " ORDER BY l.ngayHoc ASC";

            var query = em.createQuery(jpql, LichHoc.class).setParameter("maGV", maGV);
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                query.setParameter("maHocKy", maHocKy);
            }
            return query.getResultList();
        });
    }

    @Override
    public Optional<LichHoc> findScheduleSession(String maLichHoc) {
        return execute(em -> {
            try {
                LichHoc l = em.createQuery(
                        "SELECT l FROM LichHoc l JOIN FETCH l.lopHocPhan WHERE l.maLichHoc = :maLichHoc AND l.deleted = false", LichHoc.class)
                        .setParameter("maLichHoc", maLichHoc)
                        .getSingleResult();
                return Optional.of(l);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public List<DiemDanh> findAttendanceBySession(String maLichHoc) {
        return execute(em -> em.createQuery(
                "SELECT d FROM DiemDanh d JOIN FETCH d.sinhVien s JOIN FETCH s.nguoiDung " +
                "WHERE d.lichHoc.maLichHoc = :maLichHoc AND d.deleted = false ORDER BY s.maSV ASC", DiemDanh.class)
                .setParameter("maLichHoc", maLichHoc)
                .getResultList());
    }

    @Override
    public Optional<DiemDanh> findAttendanceBySessionAndStudent(String maLichHoc, String maSV) {
        return execute(em -> {
            try {
                DiemDanh d = em.createQuery(
                        "SELECT d FROM DiemDanh d WHERE d.lichHoc.maLichHoc = :maLichHoc AND d.sinhVien.maSV = :maSV AND d.deleted = false", DiemDanh.class)
                        .setParameter("maLichHoc", maLichHoc)
                        .setParameter("maSV", maSV)
                        .getSingleResult();
                return Optional.of(d);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public DiemDanh saveAttendance(DiemDanh diemDanh) {
        return execute(em -> {
            if (diemDanh.getMaDiemDanh() == null) {
                em.persist(diemDanh);
                return diemDanh;
            } else {
                return em.merge(diemDanh);
            }
        });
    }
}
