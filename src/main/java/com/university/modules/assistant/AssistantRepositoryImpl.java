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
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
                    "LEFT JOIN FETCH l.giangVien g LEFT JOIN FETCH g.nguoiDung " +
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
                        "LEFT JOIN FETCH l.giangVien g LEFT JOIN FETCH g.nguoiDung " +
                        "LEFT JOIN FETCH l.troLyDaoTao " +
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
        return executeInTransaction(em -> {
            return save(em, LopHocPhan.class, lhp.getMaLHP(), lhp);
        });
    }

    @Override
    public LopHocPhan createClassWithSchedules(LopHocPhan lhp, List<LichHoc> schedules) {
        return executeInTransaction(em -> {
            if (em.find(LopHocPhan.class, lhp.getMaLHP()) != null) {
                throw new IllegalArgumentException("Class already exists: " + lhp.getMaLHP());
            }
            validateRoomAvailability(em, lhp, schedules, null);
            em.persist(lhp);
            persistSchedules(em, lhp, schedules, false);
            return lhp;
        });
    }

    @Override
    public LopHocPhan updateClassWithSchedules(LopHocPhan lhp, List<LichHoc> schedules) {
        return executeInTransaction(em -> {
            LopHocPhan existing = em.find(LopHocPhan.class, lhp.getMaLHP(), LockModeType.PESSIMISTIC_WRITE);
            if (existing == null || Boolean.TRUE.equals(existing.getDeleted())) {
                throw new IllegalArgumentException("Class not found: " + lhp.getMaLHP());
            }
            validateRoomAvailability(em, lhp, schedules, lhp.getMaLHP());
            LopHocPhan managed = em.merge(lhp);
            em.createQuery("UPDATE LichHoc l SET l.deleted = true " +
                            "WHERE l.lopHocPhan.maLHP = :maLHP AND l.deleted = false")
                    .setParameter("maLHP", lhp.getMaLHP())
                    .executeUpdate();
            persistSchedules(em, managed, schedules, true);
            return managed;
        });
    }

    @Override
    public void deleteClass(String maLHP) {
        executeInTransaction(em -> {
            LopHocPhan lhp = em.find(LopHocPhan.class, maLHP);
            if (lhp != null && !Boolean.TRUE.equals(lhp.getDeleted())) {
                lhp.setDeleted(true);
            }
        });
    }

    @Override
    public boolean deleteClassWithSchedulesIfNoStudents(String maLHP) {
        return executeInTransaction(em -> {
            LopHocPhan lhp = em.find(LopHocPhan.class, maLHP, LockModeType.PESSIMISTIC_WRITE);
            if (lhp == null || Boolean.TRUE.equals(lhp.getDeleted())) {
                return false;
            }
            long studentCount = em.createQuery(
                            "SELECT COUNT(h) FROM HoSoHocPhan h " +
                                    "WHERE h.lopHocPhan.maLHP = :maLHP AND h.deleted = false", Long.class)
                    .setParameter("maLHP", maLHP)
                    .getSingleResult();
            if (studentCount > 0) {
                return false;
            }
            lhp.setDeleted(true);
            em.createQuery("UPDATE LichHoc l SET l.deleted = true " +
                            "WHERE l.lopHocPhan.maLHP = :maLHP AND l.deleted = false")
                    .setParameter("maLHP", maLHP)
                    .executeUpdate();
            return true;
        });
    }

    @Override
    public boolean assignLecturer(String maLHP, String maGV) {
        return executeInTransaction(em -> {
            GiangVien gv = em.find(GiangVien.class, maGV);
            LopHocPhan lhp = em.find(LopHocPhan.class, maLHP);
            if (lhp != null && gv != null && !Boolean.TRUE.equals(lhp.getDeleted())
                    && !Boolean.TRUE.equals(gv.getDeleted())) {
                lhp.setGiangVien(gv);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean updateRegistrationPeriod(String maLHP, OffsetDateTime bddk, OffsetDateTime ktdk) {
        return executeInTransaction(em -> {
            LopHocPhan lhp = em.find(LopHocPhan.class, maLHP);
            if (lhp != null && !Boolean.TRUE.equals(lhp.getDeleted())) {
                lhp.setThoiGianBDDK(bddk);
                lhp.setThoiGianKTDK(ktdk);
                return true;
            }
            return false;
        });
    }

    @Override
    public void updateRegistrationPeriods(
            List<String> classIds, OffsetDateTime bddk, OffsetDateTime ktdk) {
        executeInTransaction(em -> {
            if (bddk == null || ktdk == null || !bddk.isBefore(ktdk)) {
                throw new IllegalArgumentException("Registration period must have a valid start and end");
            }
            Set<String> requestedIds = new HashSet<>(classIds);
            if (requestedIds.isEmpty() || requestedIds.size() != classIds.size()) {
                throw new IllegalArgumentException("Class ids must be non-empty and unique");
            }
            List<LopHocPhan> classes = em.createQuery(
                            "SELECT l FROM LopHocPhan l LEFT JOIN FETCH l.giangVien " +
                                    "WHERE l.maLHP IN :ids AND l.deleted = false", LopHocPhan.class)
                    .setParameter("ids", requestedIds)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList();
            if (classes.size() != requestedIds.size()) {
                throw new IllegalArgumentException("One or more classes do not exist");
            }
            if (classes.stream().anyMatch(l -> l.getGiangVien() == null)) {
                throw new IllegalStateException("Every class must have a lecturer before registration opens");
            }
            // Cho phép cập nhật lại đợt đăng ký nếu hợp lệ
            classes.forEach(l -> {
                l.setThoiGianBDDK(bddk);
                l.setThoiGianKTDK(ktdk);
            });
        });
    }

    @Override
    public List<HoSoHocPhan> findStudentsByClass(String maLHP, int page, int limit) {
        return execute(em -> em.createQuery(
                "SELECT h FROM HoSoHocPhan h JOIN FETCH h.sinhVien s JOIN FETCH s.nguoiDung " +
                "WHERE h.lopHocPhan.maLHP = :maLHP AND h.deleted = false ORDER BY s.maSV ASC", HoSoHocPhan.class)
                .setParameter("maLHP", maLHP)
                .setFirstResult(Math.max(0, (page - 1) * limit))
                .setMaxResults(limit)
                .getResultList());
    }

    @Override
    public long countStudentsByClass(String maLHP) {
        return execute(em -> em.createQuery(
                        "SELECT COUNT(h) FROM HoSoHocPhan h " +
                                "WHERE h.lopHocPhan.maLHP = :maLHP AND h.deleted = false", Long.class)
                .setParameter("maLHP", maLHP)
                .getSingleResult());
    }

    @Override
    public boolean hasLecturerConflict(String maGV, String maHocKy, int thu, int tietBatDau,
                                       int soTiet, String excludedClassId) {
        return execute(em -> {
            String jpql = "SELECT COUNT(l) FROM LopHocPhan l " +
                    "WHERE l.deleted = false AND l.giangVien.maNV = :maGV " +
                    "AND l.hocKy.maHocKy = :maHocKy AND l.thu = :thu " +
                    "AND l.tietBatDau <= :tietKetThuc " +
                    "AND :tietBatDau <= l.tietBatDau + l.soTiet - 1";
            if (excludedClassId != null && !excludedClassId.isBlank()) {
                jpql += " AND l.maLHP <> :excludedClassId";
            }
            TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                    .setParameter("maGV", maGV)
                    .setParameter("maHocKy", maHocKy)
                    .setParameter("thu", thu)
                    .setParameter("tietBatDau", tietBatDau)
                    .setParameter("tietKetThuc", tietBatDau + soTiet - 1);
            if (excludedClassId != null && !excludedClassId.isBlank()) {
                query.setParameter("excludedClassId", excludedClassId);
            }
            return query.getSingleResult() > 0;
        });
    }

    @Override
    public boolean hasRoomConflict(LocalDate ngayHoc, String phongHoc, int tietBatDau,
                                   int soTiet, String excludedScheduleId) {
        return execute(em -> hasRoomConflict(
                em, ngayHoc, phongHoc, tietBatDau, soTiet, excludedScheduleId, null));
    }

    @Override
    public boolean hasRoomConflictExcludingClass(LocalDate ngayHoc, String phongHoc, int tietBatDau,
                                                 int soTiet, String excludedClassId) {
        return execute(em -> hasRoomConflict(
                em, ngayHoc, phongHoc, tietBatDau, soTiet, null, excludedClassId));
    }

    @Override
    public List<LichHoc> findSchedulesByClass(String maLHP) {
        return execute(em -> em.createQuery(
                "SELECT l FROM LichHoc l WHERE l.lopHocPhan.maLHP = :maLHP AND l.deleted = false " +
                        "ORDER BY l.ngayHoc ASC, l.maLichHoc ASC", LichHoc.class)
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
        return executeInTransaction(em -> {
            return save(em, LichHoc.class, lichHoc.getMaLichHoc(), lichHoc);
        });
    }

    @Override
    public void deleteScheduleById(String maLichHoc) {
        executeInTransaction(em -> {
            LichHoc l = em.find(LichHoc.class, maLichHoc);
            if (l != null && !Boolean.TRUE.equals(l.getDeleted())) {
                l.setDeleted(true);
            }
        });
    }

    @Override
    public void deleteSchedulesByClass(String maLHP) {
        executeInTransaction(em -> {
            em.createQuery(
                        "UPDATE LichHoc l SET l.deleted = true " +
                                "WHERE l.lopHocPhan.maLHP = :maLHP AND l.deleted = false")
                    .setParameter("maLHP", maLHP)
                    .executeUpdate();
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
        return executeInTransaction(em -> {
            return save(em, YeuCauMoLop.class, req.getMaYeuCau(), req);
        });
    }

    private void persistSchedules(
            EntityManager em, LopHocPhan lhp, List<LichHoc> schedules, boolean allowExistingForSameClass) {
        if (schedules == null) {
            throw new IllegalArgumentException("Schedules must not be null");
        }
        Set<String> scheduleIds = new HashSet<>();
        for (LichHoc schedule : schedules) {
            if (schedule == null || schedule.getMaLichHoc() == null || schedule.getMaLichHoc().isBlank()
                    || !scheduleIds.add(schedule.getMaLichHoc())) {
                throw new IllegalArgumentException("Schedule ids must be non-empty and unique");
            }
            LichHoc existing = em.find(LichHoc.class, schedule.getMaLichHoc());
            if (existing != null && (!allowExistingForSameClass
                    || existing.getLopHocPhan() == null
                    || !lhp.getMaLHP().equals(existing.getLopHocPhan().getMaLHP()))) {
                throw new IllegalArgumentException("Schedule already belongs to another class: "
                        + schedule.getMaLichHoc());
            }
        }
        for (LichHoc schedule : schedules) {
            schedule.setLopHocPhan(lhp);
            if (em.find(LichHoc.class, schedule.getMaLichHoc()) == null) {
                em.persist(schedule);
            } else {
                em.merge(schedule);
            }
        }
    }

    private void validateRoomAvailability(EntityManager em, LopHocPhan lhp,
                                          List<LichHoc> schedules, String excludedClassId) {
        if (schedules == null) {
            throw new IllegalArgumentException("Schedules must not be null");
        }
        for (LichHoc schedule : schedules) {
            if (schedule != null && hasRoomConflict(em, schedule.getNgayHoc(), schedule.getPhongHoc(),
                    lhp.getTietBatDau(), lhp.getSoTiet(), null, excludedClassId)) {
                throw new IllegalArgumentException("Room conflict: " + schedule.getPhongHoc()
                        + " on " + schedule.getNgayHoc());
            }
        }
    }

    private boolean hasRoomConflict(EntityManager em, LocalDate ngayHoc, String phongHoc,
                                    int tietBatDau, int soTiet, String excludedScheduleId,
                                    String excludedClassId) {
        String jpql = "SELECT COUNT(l) FROM LichHoc l JOIN l.lopHocPhan c " +
                "WHERE l.deleted = false AND c.deleted = false " +
                "AND l.ngayHoc = :ngayHoc AND l.phongHoc = :phongHoc " +
                "AND c.tietBatDau <= :tietKetThuc " +
                "AND :tietBatDau <= c.tietBatDau + c.soTiet - 1";
        if (excludedScheduleId != null && !excludedScheduleId.isBlank()) {
            jpql += " AND l.maLichHoc <> :excludedScheduleId";
        }
        if (excludedClassId != null && !excludedClassId.isBlank()) {
            jpql += " AND c.maLHP <> :excludedClassId";
        }
        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                .setParameter("ngayHoc", ngayHoc)
                .setParameter("phongHoc", phongHoc)
                .setParameter("tietBatDau", tietBatDau)
                .setParameter("tietKetThuc", tietBatDau + soTiet - 1);
        if (excludedScheduleId != null && !excludedScheduleId.isBlank()) {
            query.setParameter("excludedScheduleId", excludedScheduleId);
        }
        if (excludedClassId != null && !excludedClassId.isBlank()) {
            query.setParameter("excludedClassId", excludedClassId);
        }
        return query.getSingleResult() > 0;
    }

    private <E> E save(EntityManager em, Class<E> type, Object id, E entity) {
        if (em.find(type, id) == null) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }
}
