package com.university.modules.assistant;

import com.university.core.model.GiangVien;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.YeuCauMoLop;
import com.university.core.model.enums.TrangThaiYeuCau;
import com.university.core.repository.AbstractJpaRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class AssistantRepositoryImpl extends AbstractJpaRepository<TroLyDaoTao, String> implements AssistantRepository {

    public AssistantRepositoryImpl() {
        super(TroLyDaoTao.class);
    }

    @Override
    public List<HocKy> findAllSemesters() {
        return execute(em -> em.createQuery(
                "SELECT h FROM HocKy h WHERE h.deleted = false ORDER BY h.thoiGianBD DESC", HocKy.class)
                .getResultList());
    }

    @Override
    public Optional<HocKy> findSemesterById(String maHocKy) {
        return execute(em -> {
            try {
                HocKy h = em.createQuery(
                        "SELECT h FROM HocKy h WHERE h.maHocKy = :maHocKy AND h.deleted = false", HocKy.class)
                        .setParameter("maHocKy", maHocKy)
                        .getSingleResult();
                return Optional.of(h);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public List<HocPhan> findAllCourses() {
        return execute(em -> em.createQuery(
                "SELECT h FROM HocPhan h WHERE h.deleted = false ORDER BY h.maHP ASC", HocPhan.class)
                .getResultList());
    }

    @Override
    public Optional<HocPhan> findCourseById(String maHP) {
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
    public Optional<TroLyDaoTao> findAssistantById(String maNV) {
        return execute(em -> {
            try {
                TroLyDaoTao tl = em.createQuery(
                        "SELECT t FROM TroLyDaoTao t WHERE t.maNV = :maNV AND t.deleted = false", TroLyDaoTao.class)
                        .setParameter("maNV", maNV)
                        .getSingleResult();
                return Optional.of(tl);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<GiangVien> findLecturerById(String maGV) {
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
    public List<LopHocPhan> findClassesBySemester(String maHocKy, int page, int limit) {
        return execute(em -> {
            String jpql = "SELECT l FROM LopHocPhan l JOIN FETCH l.monHoc JOIN FETCH l.hocKy " +
                    "WHERE l.deleted = false";
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                jpql += " AND l.hocKy.maHocKy = :maHocKy";
            }
            jpql += " ORDER BY l.maLHP ASC";

            TypedQuery<LopHocPhan> query = em.createQuery(jpql, LopHocPhan.class);
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                query.setParameter("maHocKy", maHocKy);
            }

            int firstResult = Math.max(0, (page - 1) * limit);
            query.setFirstResult(firstResult);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }

    @Override
    public long countClassesBySemester(String maHocKy) {
        return execute(em -> {
            String jpql = "SELECT COUNT(l) FROM LopHocPhan l WHERE l.deleted = false";
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                jpql += " AND l.hocKy.maHocKy = :maHocKy";
            }
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                query.setParameter("maHocKy", maHocKy);
            }
            return query.getSingleResult();
        });
    }

    @Override
    public Optional<LopHocPhan> findClassById(String maLHP) {
        return execute(em -> {
            try {
                LopHocPhan lhp = em.createQuery(
                        "SELECT l FROM LopHocPhan l JOIN FETCH l.monHoc JOIN FETCH l.hocKy " +
                        "LEFT JOIN FETCH l.giangVien LEFT JOIN FETCH l.troLyDaoTao " +
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
    public LopHocPhan saveClass(LopHocPhan lhp) {
        return execute(em -> {
            LopHocPhan existing = em.find(LopHocPhan.class, lhp.getMaLHP());
            if (existing == null) {
                em.persist(lhp);
                return lhp;
            } else {
                return em.merge(lhp);
            }
        });
    }

    @Override
    public void deleteClass(String maLHP) {
        execute(em -> {
            LopHocPhan lhp = em.find(LopHocPhan.class, maLHP);
            if (lhp != null) {
                lhp.setDeleted(true);
                em.merge(lhp);
            }
            return null;
        });
    }

    @Override
    public boolean assignLecturer(String maLHP, String maGV) {
        return execute(em -> {
            GiangVien gv = em.find(GiangVien.class, maGV);
            LopHocPhan lhp = em.find(LopHocPhan.class, maLHP);
            if (lhp != null && gv != null) {
                lhp.setGiangVien(gv);
                em.merge(lhp);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean updateRegistrationPeriod(String maLHP, OffsetDateTime bddk, OffsetDateTime ktdk) {
        return execute(em -> {
            LopHocPhan lhp = em.find(LopHocPhan.class, maLHP);
            if (lhp != null) {
                lhp.setThoiGianBDDK(bddk);
                lhp.setThoiGianKTDK(ktdk);
                em.merge(lhp);
                return true;
            }
            return false;
        });
    }

    @Override
    public List<HoSoHocPhan> findStudentsByClass(String maLHP) {
        return execute(em -> em.createQuery(
                "SELECT h FROM HoSoHocPhan h JOIN FETCH h.sinhVien s JOIN FETCH s.nguoiDung " +
                "WHERE h.lopHocPhan.maLHP = :maLHP AND h.deleted = false ORDER BY s.maSV ASC", HoSoHocPhan.class)
                .setParameter("maLHP", maLHP)
                .getResultList());
    }

    @Override
    public List<LichHoc> findSchedulesByClass(String maLHP) {
        return execute(em -> em.createQuery(
                "SELECT l FROM LichHoc l WHERE l.lopHocPhan.maLHP = :maLHP AND l.deleted = false ORDER BY l.ngayHoc ASC", LichHoc.class)
                .setParameter("maLHP", maLHP)
                .getResultList());
    }

    @Override
    public Optional<LichHoc> findScheduleById(String maLichHoc) {
        return execute(em -> {
            try {
                LichHoc l = em.createQuery(
                        "SELECT l FROM LichHoc l WHERE l.maLichHoc = :maLichHoc AND l.deleted = false", LichHoc.class)
                        .setParameter("maLichHoc", maLichHoc)
                        .getSingleResult();
                return Optional.of(l);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public LichHoc saveSchedule(LichHoc lichHoc) {
        return execute(em -> {
            LichHoc existing = em.find(LichHoc.class, lichHoc.getMaLichHoc());
            if (existing == null) {
                em.persist(lichHoc);
                return lichHoc;
            } else {
                return em.merge(lichHoc);
            }
        });
    }

    @Override
    public void deleteScheduleById(String maLichHoc) {
        execute(em -> {
            LichHoc l = em.find(LichHoc.class, maLichHoc);
            if (l != null) {
                l.setDeleted(true);
                em.merge(l);
            }
            return null;
        });
    }

    @Override
    public void deleteSchedulesByClass(String maLHP) {
        execute(em -> {
            em.createQuery("UPDATE LichHoc l SET l.deleted = true WHERE l.lopHocPhan.maLHP = :maLHP")
                    .setParameter("maLHP", maLHP)
                    .executeUpdate();
            return null;
        });
    }

    @Override
    public List<YeuCauMoLop> findClassRequests(String trangThai, int page, int limit) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder(
                    "SELECT y FROM YeuCauMoLop y JOIN FETCH y.sinhVien s JOIN FETCH s.nguoiDung JOIN FETCH y.hocPhan " +
                    "WHERE y.deleted = false");
            if (trangThai != null && !trangThai.trim().isEmpty()) {
                sb.append(" AND y.trangThai = :status");
            }
            sb.append(" ORDER BY y.ngayTao DESC");

            TypedQuery<YeuCauMoLop> query = em.createQuery(sb.toString(), YeuCauMoLop.class);
            if (trangThai != null && !trangThai.trim().isEmpty()) {
                query.setParameter("status", TrangThaiYeuCau.valueOf(trangThai.toUpperCase()));
            }

            int firstResult = Math.max(0, (page - 1) * limit);
            query.setFirstResult(firstResult);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }

    @Override
    public long countClassRequests(String trangThai) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder("SELECT COUNT(y) FROM YeuCauMoLop y WHERE y.deleted = false");
            if (trangThai != null && !trangThai.trim().isEmpty()) {
                sb.append(" AND y.trangThai = :status");
            }
            TypedQuery<Long> query = em.createQuery(sb.toString(), Long.class);
            if (trangThai != null && !trangThai.trim().isEmpty()) {
                query.setParameter("status", TrangThaiYeuCau.valueOf(trangThai.toUpperCase()));
            }
            return query.getSingleResult();
        });
    }

    @Override
    public Optional<YeuCauMoLop> findClassRequestById(String maYeuCau) {
        return execute(em -> {
            try {
                YeuCauMoLop req = em.createQuery(
                        "SELECT y FROM YeuCauMoLop y JOIN FETCH y.sinhVien s JOIN FETCH s.nguoiDung JOIN FETCH y.hocPhan " +
                        "WHERE y.maYeuCau = :maYeuCau AND y.deleted = false", YeuCauMoLop.class)
                        .setParameter("maYeuCau", maYeuCau)
                        .getSingleResult();
                return Optional.of(req);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public YeuCauMoLop saveClassRequest(YeuCauMoLop req) {
        return execute(em -> {
            YeuCauMoLop existing = em.find(YeuCauMoLop.class, req.getMaYeuCau());
            if (existing == null) {
                em.persist(req);
                return req;
            } else {
                return em.merge(req);
            }
        });
    }
}
