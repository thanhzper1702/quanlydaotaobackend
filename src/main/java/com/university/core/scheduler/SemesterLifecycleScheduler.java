package com.university.core.scheduler;

import com.university.core.config.JpaUtil;
import com.university.core.model.HocKy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SemesterLifecycleScheduler {

    private static final Logger log = LoggerFactory.getLogger(SemesterLifecycleScheduler.class);
    private static ScheduledExecutorService scheduler;

    private SemesterLifecycleScheduler() {
    }

    public static synchronized void start() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "SemesterLifecycleScheduler-Worker");
                t.setDaemon(true);
                return t;
            });
            scheduler.scheduleAtFixedRate(
                    SemesterLifecycleScheduler::runLifecycleScan,
                    10, 15 * 60, TimeUnit.SECONDS
            );
            log.info("SemesterLifecycleScheduler started successfully (interval: 15 minutes).");
        }
    }

    public static synchronized void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            log.info("Stopping SemesterLifecycleScheduler...");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("SemesterLifecycleScheduler stopped.");
        }
    }

    public static void triggerImmediateScan() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.execute(SemesterLifecycleScheduler::runLifecycleScan);
        } else {
            runLifecycleScan();
        }
    }

    public static void runLifecycleScan() {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = JpaUtil.getEntityManagerFactory().createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            LocalDate today = LocalDate.now();
            OffsetDateTime now = OffsetDateTime.now();

            // 1. Transition Current Semester if expired and not manually overridden
            List<HocKy> currentSemesters = em.createQuery(
                    "SELECT h FROM HocKy h WHERE h.isCurrent = true AND h.deleted = false", HocKy.class)
                    .getResultList();

            if (!currentSemesters.isEmpty()) {
                HocKy current = currentSemesters.get(0);
                if (current.isExpired() && !Boolean.TRUE.equals(current.getManualOverride())) {
                    log.info("Current semester {} has expired. Auto-transitioning...", current.getMaHocKy());
                    current.setIsCurrent(false);
                    em.merge(current);

                    List<HocKy> nextSemesters = em.createQuery(
                            "SELECT h FROM HocKy h WHERE h.deleted = false AND h.thoiGianKT >= :today " +
                            "ORDER BY h.thoiGianBD ASC", HocKy.class)
                            .setParameter("today", today)
                            .setMaxResults(1)
                            .getResultList();

                    if (!nextSemesters.isEmpty()) {
                        HocKy next = nextSemesters.get(0);
                        next.setIsCurrent(true);
                        next.setManualOverride(false);
                        next.setThoiGianCapNhat(java.time.LocalDateTime.now());
                        em.merge(next);
                        log.info("Activated new current semester: {}", next.getMaHocKy());
                    }
                }
            }

            // 2. Mark past semester classes as KETTHUC and is_editable = false
            int closedClasses = em.createNativeQuery(
                    "UPDATE lophocphan SET is_editable = false, trangthai = 'KETTHUC' " +
                    "WHERE mahocky IN (SELECT mahocky FROM hocky WHERE thoigiankt < :today AND deleted = false) " +
                    "AND (is_editable = true OR trangthai != 'KETTHUC')")
                    .setParameter("today", today)
                    .executeUpdate();

            if (closedClasses > 0) {
                log.info("Auto-closed {} classes from ended semesters.", closedClasses);
            }

            // 3. Mark classes past registration deadline or belonging to an already started semester as DANGHOC
            int closedReg = em.createNativeQuery(
                    "UPDATE lophocphan SET trangthai = 'DANGHOC' " +
                    "WHERE ((thoigianktdk IS NOT NULL AND thoigianktdk < :now) OR " +
                    "mahocky IN (SELECT mahocky FROM hocky WHERE thoigianbd <= :today AND deleted = false)) " +
                    "AND trangthai = 'MODANGKY' AND deleted = false")
                    .setParameter("now", now)
                    .setParameter("today", today)
                    .executeUpdate();

            if (closedReg > 0) {
                log.info("Transitioned {} classes to DANGHOC (registration closed).", closedReg);
            }

            // 4. Mark classes inside registration window as MODANGKY (ONLY if semester has not started)
            int openedReg = em.createNativeQuery(
                    "UPDATE lophocphan SET trangthai = 'MODANGKY' " +
                    "WHERE thoigianbddk <= :now AND (thoigianktdk IS NULL OR thoigianktdk >= :now) " +
                    "AND trangthai = 'CHUA_MO' AND is_editable = true AND deleted = false " +
                    "AND mahocky IN (SELECT mahocky FROM hocky WHERE thoigianbd > :today AND deleted = false)")
                    .setParameter("now", now)
                    .setParameter("today", today)
                    .executeUpdate();

            if (openedReg > 0) {
                log.info("Transitioned {} classes to MODANGKY (registration opened).", openedReg);
            }

            // 5. Lock schedules and grades for classes with is_editable = false
            em.createNativeQuery(
                    "UPDATE lichhoc SET is_editable = false " +
                    "WHERE malhp IN (SELECT malhp FROM lophocphan WHERE is_editable = false) " +
                    "AND is_editable = true")
                    .executeUpdate();

            em.createNativeQuery(
                    "UPDATE hosohocphan SET is_editable = false " +
                    "WHERE malhp IN (SELECT malhp FROM lophocphan WHERE is_editable = false) " +
                    "AND is_editable = true")
                    .executeUpdate();

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            log.error("SemesterLifecycleScheduler scan error: {}", e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
