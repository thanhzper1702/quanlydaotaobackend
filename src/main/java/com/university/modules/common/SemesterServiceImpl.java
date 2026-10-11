package com.university.modules.common;

import com.university.core.config.JpaUtil;
import com.university.core.model.HocKy;
import com.university.core.scheduler.SemesterLifecycleScheduler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class SemesterServiceImpl implements SemesterService {

    private static final Logger log = LoggerFactory.getLogger(SemesterServiceImpl.class);

    @Override
    public SemesterDTO getCurrentSemester() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<HocKy> list = em.createQuery(
                    "SELECT h FROM HocKy h WHERE h.isCurrent = true AND h.deleted = false", HocKy.class)
                    .getResultList();

            if (!list.isEmpty()) {
                HocKy sem = list.get(0);
                if (sem.isExpired() && !Boolean.TRUE.equals(sem.getManualOverride())) {
                    SemesterLifecycleScheduler.triggerImmediateScan();
                }
                return toDTO(sem);
            }

            LocalDate today = LocalDate.now();
            List<HocKy> activeList = em.createQuery(
                    "SELECT h FROM HocKy h WHERE h.deleted = false AND h.thoiGianBD <= :today AND h.thoiGianKT >= :today", HocKy.class)
                    .setParameter("today", today)
                    .getResultList();

            if (!activeList.isEmpty()) {
                return toDTO(activeList.get(0));
            }

            List<HocKy> latestList = em.createQuery(
                    "SELECT h FROM HocKy h WHERE h.deleted = false ORDER BY h.thoiGianBD DESC", HocKy.class)
                    .setMaxResults(1)
                    .getResultList();

            if (!latestList.isEmpty()) {
                return toDTO(latestList.get(0));
            }

            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<SemesterDTO> getAllSemesters() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<HocKy> list = em.createQuery(
                    "SELECT h FROM HocKy h WHERE h.deleted = false ORDER BY h.thoiGianBD DESC", HocKy.class)
                    .getResultList();
            List<SemesterDTO> dtoList = new ArrayList<>();
            for (HocKy h : list) {
                dtoList.add(toDTO(h));
            }
            return dtoList;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean switchCurrentSemester(String maHocKy, String maNVTroLy) {
        if (maHocKy == null || maHocKy.isBlank()) {
            return false;
        }
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            HocKy target = em.find(HocKy.class, maHocKy.trim());
            if (target == null || target.isDeleted()) {
                return false;
            }

            em.createQuery("UPDATE HocKy h SET h.isCurrent = false WHERE h.isCurrent = true")
                    .executeUpdate();

            target.setIsCurrent(true);
            target.setManualOverride(true);
            target.setUpdatedByTroLy(maNVTroLy);
            target.setThoiGianCapNhat(java.time.LocalDateTime.now());
            em.merge(target);

            tx.commit();
            log.info("Assistant {} switched current semester to: {}", maNVTroLy, maHocKy);
            SemesterLifecycleScheduler.triggerImmediateScan();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Failed to switch current semester: {}", e.getMessage(), e);
            return false;
        } finally {
            em.close();
        }
    }

    private SemesterDTO toDTO(HocKy h) {
        return new SemesterDTO(
                h.getMaHocKy(),
                h.getTenHocKy() != null ? h.getTenHocKy().name() : "",
                h.getThoiGianBD(),
                h.getThoiGianKT(),
                h.getIsCurrent(),
                h.getManualOverride()
        );
    }
}
