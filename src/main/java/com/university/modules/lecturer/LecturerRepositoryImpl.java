package com.university.modules.lecturer;

import com.university.core.config.JpaUtil;
import com.university.core.model.DiemDanh;
import com.university.core.model.GiangVien;
import com.university.core.model.HocKy;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.enums.TrangThaiHocPhan;
import com.university.core.repository.AbstractJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class LecturerRepositoryImpl extends AbstractJpaRepository<GiangVien, String> implements LecturerRepository {

    private final EntityManager scopedEntityManager;
    private volatile boolean scopedActive;

    public LecturerRepositoryImpl() {
        super(GiangVien.class);
        this.scopedEntityManager = null;
        this.scopedActive = false;
    }

    private LecturerRepositoryImpl(EntityManager entityManager) {
        super(GiangVien.class);
        this.scopedEntityManager = entityManager;
        this.scopedActive = true;
    }

    @Override
    protected <R> R execute(Function<EntityManager, R> action) {
        if (scopedEntityManager != null) {
            ensureScopeActive();
            try {
                return action.apply(scopedEntityManager);
            } catch (Throwable failure) {
                throw repositoryFailure(failure);
            }
        }

        EntityManager entityManager = null;
        try {
            entityManager = JpaUtil.getEntityManager();
            return action.apply(entityManager);
        } catch (Throwable failure) {
            throw repositoryFailure(failure);
        } finally {
            closeQuietly(entityManager);
        }
    }

    @Override
    protected <R> R executeInTransaction(Function<EntityManager, R> action) {
        if (scopedEntityManager != null) {
            ensureScopeActive();
            try {
                if (!scopedEntityManager.getTransaction().isActive()) {
                    throw new IllegalStateException("Scoped lecturer transaction is no longer active");
                }
                return action.apply(scopedEntityManager);
            } catch (Throwable failure) {
                throw repositoryFailure(failure);
            }
        }

        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = JpaUtil.getEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            R result = action.apply(entityManager);
            transaction.commit();
            return result;
        } catch (Throwable failure) {
            rollbackQuietly(transaction);
            throw repositoryFailure(failure);
        } finally {
            closeQuietly(entityManager);
        }
    }

    @Override
    public Optional<GiangVien> findByMaGV(String maGV) {
        return execute(entityManager -> entityManager.createQuery(
                        "SELECT g FROM GiangVien g JOIN FETCH g.nguoiDung nd "
                                + "WHERE g.maNV = :maGV AND g.deleted = false AND nd.deleted = false",
                        GiangVien.class)
                .setParameter("maGV", maGV)
                .getResultStream()
                .findFirst());
    }

    @Override
    public Optional<HocKy> findSemesterById(String maHocKy) {
        return execute(entityManager -> entityManager.createQuery(
                        "SELECT h FROM HocKy h WHERE h.maHocKy = :maHocKy AND h.deleted = false",
                        HocKy.class)
                .setParameter("maHocKy", maHocKy)
                .getResultStream()
                .findFirst());
    }

    @Override
    public Optional<HocKy> findCurrentSemester(LocalDate date) {
        if (date == null) {
            return Optional.empty();
        }
        return execute(entityManager -> entityManager.createQuery(
                        "SELECT h FROM HocKy h WHERE h.deleted = false AND h.thoiGianBD <= :date "
                                + "AND h.thoiGianKT >= :date ORDER BY h.thoiGianBD DESC, h.maHocKy ASC",
                        HocKy.class)
                .setParameter("date", date)
                .setMaxResults(1)
                .getResultStream()
                .findFirst());
    }

    @Override
    public List<LopHocPhan> findTeachingClasses(String maGV, String maHocKy) {
        return execute(entityManager -> {
            String jpql = "SELECT l FROM LopHocPhan l JOIN FETCH l.monHoc hp JOIN FETCH l.hocKy hk "
                    + "JOIN FETCH l.giangVien g JOIN FETCH g.nguoiDung nd "
                    + "WHERE l.deleted = false AND hp.deleted = false AND hk.deleted = false "
                    + "AND g.deleted = false AND nd.deleted = false AND g.maNV = :maGV";
            if (hasText(maHocKy)) {
                jpql += " AND hk.maHocKy = :maHocKy";
            }
            jpql += " ORDER BY l.maLHP ASC";

            var query = entityManager.createQuery(jpql, LopHocPhan.class).setParameter("maGV", maGV);
            if (hasText(maHocKy)) {
                query.setParameter("maHocKy", maHocKy.trim());
            }
            return query.getResultList();
        });
    }

    @Override
    public List<LopHocPhan> findLecturerAssignedClasses(String maGV, String maHocKy) {
        return findTeachingClasses(maGV, maHocKy);
    }

    @Override
    public Optional<LopHocPhan> findClassById(String maLHP) {
        return execute(entityManager -> entityManager.createQuery(
                        "SELECT l FROM LopHocPhan l JOIN FETCH l.monHoc hp JOIN FETCH l.hocKy hk "
                                + "JOIN FETCH l.giangVien g JOIN FETCH g.nguoiDung nd "
                                + "WHERE l.maLHP = :maLHP AND l.deleted = false AND hp.deleted = false "
                                + "AND hk.deleted = false AND g.deleted = false AND nd.deleted = false",
                        LopHocPhan.class)
                .setParameter("maLHP", maLHP)
                .getResultStream()
                .findFirst());
    }

    @Override
    public List<HoSoHocPhan> findStudentsAndGradesInClass(String maLHP) {
        return execute(entityManager -> entityManager.createQuery(
                        "SELECT h FROM HoSoHocPhan h JOIN FETCH h.sinhVien s JOIN FETCH s.nguoiDung svnd "
                                + "JOIN FETCH h.lopHocPhan l JOIN FETCH l.monHoc hp JOIN FETCH l.hocKy hk "
                                + "JOIN FETCH l.giangVien g JOIN FETCH g.nguoiDung gvnd "
                                + "WHERE h.lopHocPhan.maLHP = :maLHP AND h.deleted = false "
                                + "AND h.trangThai <> :cancelled AND s.deleted = false AND svnd.deleted = false "
                                + "AND l.deleted = false AND hp.deleted = false AND hk.deleted = false "
                                + "AND g.deleted = false AND gvnd.deleted = false ORDER BY s.maSV ASC",
                        HoSoHocPhan.class)
                .setParameter("maLHP", maLHP)
                .setParameter("cancelled", TrangThaiHocPhan.DAHUY)
                .getResultList());
    }

    @Override
    public Optional<HoSoHocPhan> findGradeRecord(Long maHoSo) {
        return execute(entityManager -> entityManager.createQuery(
                        "SELECT h FROM HoSoHocPhan h JOIN FETCH h.sinhVien s JOIN FETCH s.nguoiDung svnd "
                                + "JOIN FETCH h.lopHocPhan l JOIN FETCH l.monHoc hp JOIN FETCH l.hocKy hk "
                                + "JOIN FETCH l.giangVien g JOIN FETCH g.nguoiDung gvnd "
                                + "WHERE h.maHoSo = :maHoSo AND h.deleted = false AND h.trangThai <> :cancelled "
                                + "AND s.deleted = false AND svnd.deleted = false AND l.deleted = false "
                                + "AND hp.deleted = false AND hk.deleted = false AND g.deleted = false "
                                + "AND gvnd.deleted = false",
                        HoSoHocPhan.class)
                .setParameter("maHoSo", maHoSo)
                .setParameter("cancelled", TrangThaiHocPhan.DAHUY)
                .getResultStream()
                .findFirst());
    }

    @Override
    public HoSoHocPhan saveGrade(HoSoHocPhan record) {
        return executeInTransaction(entityManager -> {
            if (record.getMaHoSo() == null) {
                entityManager.persist(record);
                return record;
            }
            return entityManager.merge(record);
        });
    }

    @Override
    public List<LichHoc> findTeachingSchedule(String maGV, String maHocKy) {
        return findTeachingSchedule(maGV, maHocKy, null, null);
    }

    @Override
    public List<LichHoc> findTeachingSchedule(String maGV, String maHocKy, LocalDate from, LocalDate through) {
        return execute(entityManager -> {
            String jpql = "SELECT s FROM LichHoc s JOIN FETCH s.lopHocPhan l JOIN FETCH l.monHoc hp "
                    + "JOIN FETCH l.hocKy hk JOIN FETCH l.giangVien g JOIN FETCH g.nguoiDung nd "
                    + "WHERE s.deleted = false AND l.deleted = false AND hp.deleted = false "
                    + "AND hk.deleted = false AND g.deleted = false AND nd.deleted = false AND g.maNV = :maGV";
            if (hasText(maHocKy)) {
                jpql += " AND hk.maHocKy = :maHocKy";
            }
            if (from != null) {
                jpql += " AND s.ngayHoc >= :fromDate";
            }
            if (through != null) {
                jpql += " AND s.ngayHoc <= :throughDate";
            }
            jpql += " ORDER BY s.ngayHoc ASC, l.tietBatDau ASC, s.maLichHoc ASC";

            var query = entityManager.createQuery(jpql, LichHoc.class).setParameter("maGV", maGV);
            if (hasText(maHocKy)) {
                query.setParameter("maHocKy", maHocKy.trim());
            }
            if (from != null) {
                query.setParameter("fromDate", from);
            }
            if (through != null) {
                query.setParameter("throughDate", through);
            }
            return query.getResultList();
        });
    }

    @Override
    public Optional<LichHoc> findScheduleSession(String maLichHoc) {
        return execute(entityManager -> entityManager.createQuery(
                        "SELECT s FROM LichHoc s JOIN FETCH s.lopHocPhan l JOIN FETCH l.monHoc hp "
                                + "JOIN FETCH l.hocKy hk JOIN FETCH l.giangVien g JOIN FETCH g.nguoiDung nd "
                                + "WHERE s.maLichHoc = :maLichHoc AND s.deleted = false AND l.deleted = false "
                                + "AND hp.deleted = false AND hk.deleted = false AND g.deleted = false "
                                + "AND nd.deleted = false",
                        LichHoc.class)
                .setParameter("maLichHoc", maLichHoc)
                .getResultStream()
                .findFirst());
    }

    @Override
    public List<DiemDanh> findAttendanceBySession(String maLichHoc) {
        return execute(entityManager -> entityManager.createQuery(
                        "SELECT d FROM DiemDanh d JOIN FETCH d.sinhVien s JOIN FETCH s.nguoiDung svnd "
                                + "JOIN FETCH d.lichHoc sh JOIN FETCH sh.lopHocPhan l JOIN FETCH l.monHoc hp "
                                + "JOIN FETCH l.hocKy hk JOIN FETCH l.giangVien g JOIN FETCH g.nguoiDung gvnd "
                                + "WHERE sh.maLichHoc = :maLichHoc AND d.deleted = false AND sh.deleted = false "
                                + "AND s.deleted = false AND svnd.deleted = false AND l.deleted = false "
                                + "AND hp.deleted = false AND hk.deleted = false AND g.deleted = false "
                                + "AND gvnd.deleted = false AND EXISTS (SELECT h.maHoSo FROM HoSoHocPhan h "
                                + "WHERE h.sinhVien = s AND h.lopHocPhan = l AND h.deleted = false "
                                + "AND h.trangThai <> :cancelled) ORDER BY s.maSV ASC",
                        DiemDanh.class)
                .setParameter("maLichHoc", maLichHoc)
                .setParameter("cancelled", TrangThaiHocPhan.DAHUY)
                .getResultList());
    }

    @Override
    public Optional<DiemDanh> findAttendanceBySessionAndStudent(String maLichHoc, String maSV) {
        return execute(entityManager -> entityManager.createQuery(
                        "SELECT d FROM DiemDanh d JOIN FETCH d.sinhVien s JOIN FETCH s.nguoiDung svnd "
                                + "JOIN FETCH d.lichHoc sh JOIN FETCH sh.lopHocPhan l JOIN FETCH l.monHoc hp "
                                + "JOIN FETCH l.hocKy hk JOIN FETCH l.giangVien g JOIN FETCH g.nguoiDung gvnd "
                                + "WHERE sh.maLichHoc = :maLichHoc AND s.maSV = :maSV AND d.deleted = false "
                                + "AND sh.deleted = false AND s.deleted = false AND svnd.deleted = false "
                                + "AND l.deleted = false AND hp.deleted = false AND hk.deleted = false "
                                + "AND g.deleted = false AND gvnd.deleted = false AND EXISTS "
                                + "(SELECT h.maHoSo FROM HoSoHocPhan h WHERE h.sinhVien = s AND h.lopHocPhan = l "
                                + "AND h.deleted = false AND h.trangThai <> :cancelled)",
                        DiemDanh.class)
                .setParameter("maLichHoc", maLichHoc)
                .setParameter("maSV", maSV)
                .setParameter("cancelled", TrangThaiHocPhan.DAHUY)
                .getResultStream()
                .findFirst());
    }

    @Override
    public DiemDanh saveAttendance(DiemDanh diemDanh) {
        return executeInTransaction(entityManager -> {
            if (diemDanh.getMaDiemDanh() == null) {
                entityManager.persist(diemDanh);
                return diemDanh;
            }
            return entityManager.merge(diemDanh);
        });
    }

    @Override
    public <T> T withClassTransaction(String maLHP, Function<LecturerRepository, T> callback) {
        if (scopedEntityManager != null) {
            ensureScopeActive();
            throw new IllegalStateException("A lecturer class transaction cannot be nested");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback is required");
        }

        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        LecturerRepositoryImpl scopedRepository = null;
        try {
            entityManager = JpaUtil.getEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            LopHocPhan classSection = entityManager.find(
                    LopHocPhan.class, maLHP, LockModeType.PESSIMISTIC_WRITE);
            if (classSection == null || classSection.isDeleted()) {
                throw new LecturerException(404, "Lớp học phần không tồn tại");
            }

            scopedRepository = new LecturerRepositoryImpl(entityManager);
            T result = callback.apply(scopedRepository);
            scopedRepository.scopedActive = false;
            transaction.commit();
            return result;
        } catch (Throwable failure) {
            rollbackQuietly(transaction);
            throw repositoryFailure(failure);
        } finally {
            if (scopedRepository != null) {
                scopedRepository.scopedActive = false;
            }
            closeQuietly(entityManager);
        }
    }

    private void ensureScopeActive() {
        if (!scopedActive || !scopedEntityManager.isOpen()) {
            throw new IllegalStateException("Lecturer transaction scope is closed");
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static RuntimeException repositoryFailure(Throwable failure) {
        LecturerException lecturerException = findCause(failure, LecturerException.class);
        if (lecturerException != null) {
            return lecturerException;
        }
        if (failure instanceof Error error) {
            throw error;
        }
        if (isConflict(failure)) {
            return new LecturerException(409, "Dữ liệu đang được cập nhật bởi yêu cầu khác. Vui lòng thử lại.");
        }
        return new RuntimeException("Không thể xử lý dữ liệu giảng viên lúc này.", failure);
    }

    private static boolean isConflict(Throwable failure) {
        if (findCause(failure, PessimisticLockException.class) != null
                || findCause(failure, OptimisticLockException.class) != null
                || findCause(failure, LockTimeoutException.class) != null) {
            return true;
        }
        for (Throwable current : causeChain(failure)) {
            if (current instanceof SQLException sqlException) {
                String state = sqlException.getSQLState();
                if ("23505".equals(state) || "40001".equals(state) || "40P01".equals(state)
                        || "55P03".equals(state)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static <T extends Throwable> T findCause(Throwable failure, Class<T> type) {
        for (Throwable current : causeChain(failure)) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
        }
        return null;
    }

    private static Iterable<Throwable> causeChain(Throwable failure) {
        if (failure == null) {
            return Collections.emptyList();
        }
        ArrayDeque<Throwable> pending = new ArrayDeque<>();
        Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        pending.add(failure);
        ArrayList<Throwable> chain = new ArrayList<>();
        while (!pending.isEmpty()) {
            Throwable current = pending.removeFirst();
            if (!seen.add(current)) {
                continue;
            }
            chain.add(current);
            if (current.getCause() != null) {
                pending.addLast(current.getCause());
            }
            if (current instanceof SQLException sqlException && sqlException.getNextException() != null) {
                pending.addLast(sqlException.getNextException());
            }
            for (Throwable suppressed : current.getSuppressed()) {
                pending.addLast(suppressed);
            }
        }
        return chain;
    }

    private static void rollbackQuietly(EntityTransaction transaction) {
        if (transaction == null) {
            return;
        }
        try {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Throwable ignored) {
        }
    }

    private static void closeQuietly(EntityManager entityManager) {
        if (entityManager == null) {
            return;
        }
        try {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        } catch (Throwable ignored) {
        }
    }
}
