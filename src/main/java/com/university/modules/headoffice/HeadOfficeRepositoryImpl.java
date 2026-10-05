package com.university.modules.headoffice;

import com.university.core.model.GiangVien;
import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.SinhVien;
import com.university.core.model.TruongPhongDaoTao;
import com.university.core.model.YeuCauMoLop;
import com.university.core.model.enums.TrangThaiYeuCau;
import com.university.core.repository.AbstractJpaRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class HeadOfficeRepositoryImpl extends AbstractJpaRepository<TruongPhongDaoTao, String> implements HeadOfficeRepository {

    public HeadOfficeRepositoryImpl() {
        super(TruongPhongDaoTao.class);
    }

    @Override
    public List<HocPhan> findCourses(int page, int limit) {
        return execute(em -> {
            TypedQuery<HocPhan> query = em.createQuery(
                    "SELECT h FROM HocPhan h WHERE h.deleted = false ORDER BY h.maHP ASC", HocPhan.class);
            int firstResult = Math.max(0, (page - 1) * limit);
            query.setFirstResult(firstResult);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    @Override
    public long countCourses() {
        return execute(em -> em.createQuery(
                "SELECT COUNT(h) FROM HocPhan h WHERE h.deleted = false", Long.class)
                .getSingleResult());
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
    public HocPhan saveCourse(HocPhan course) {
        return execute(em -> {
            HocPhan existing = em.find(HocPhan.class, course.getMaHP());
            if (existing == null) {
                em.persist(course);
                return course;
            } else {
                return em.merge(course);
            }
        });
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
    public HocKy saveSemester(HocKy semester) {
        return execute(em -> {
            HocKy existing = em.find(HocKy.class, semester.getMaHocKy());
            if (existing == null) {
                em.persist(semester);
                return semester;
            } else {
                return em.merge(semester);
            }
        });
    }

    @Override
    public List<GiangVien> findLecturers(String khoa, int page, int limit) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder("SELECT g FROM GiangVien g JOIN FETCH g.nguoiDung WHERE g.deleted = false");
            if (khoa != null && !khoa.trim().isEmpty()) {
                sb.append(" AND g.khoa = :khoa");
            }
            sb.append(" ORDER BY g.maNV ASC");

            TypedQuery<GiangVien> query = em.createQuery(sb.toString(), GiangVien.class);
            if (khoa != null && !khoa.trim().isEmpty()) {
                query.setParameter("khoa", khoa);
            }

            int firstResult = Math.max(0, (page - 1) * limit);
            query.setFirstResult(firstResult);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }

    @Override
    public long countLecturers(String khoa) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder("SELECT COUNT(g) FROM GiangVien g WHERE g.deleted = false");
            if (khoa != null && !khoa.trim().isEmpty()) {
                sb.append(" AND g.khoa = :khoa");
            }
            TypedQuery<Long> query = em.createQuery(sb.toString(), Long.class);
            if (khoa != null && !khoa.trim().isEmpty()) {
                query.setParameter("khoa", khoa);
            }
            return query.getSingleResult();
        });
    }

    @Override
    public List<SinhVien> findStudents(String nienKhoa, String khoa, int page, int limit) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder("SELECT s FROM SinhVien s JOIN FETCH s.nguoiDung WHERE s.deleted = false");
            if (nienKhoa != null && !nienKhoa.trim().isEmpty()) {
                sb.append(" AND s.nienKhoa = :nienKhoa");
            }
            if (khoa != null && !khoa.trim().isEmpty()) {
                sb.append(" AND s.khoa = :khoa");
            }
            sb.append(" ORDER BY s.maSV ASC");

            TypedQuery<SinhVien> query = em.createQuery(sb.toString(), SinhVien.class);
            if (nienKhoa != null && !nienKhoa.trim().isEmpty()) {
                query.setParameter("nienKhoa", nienKhoa);
            }
            if (khoa != null && !khoa.trim().isEmpty()) {
                query.setParameter("khoa", khoa);
            }

            int firstResult = Math.max(0, (page - 1) * limit);
            query.setFirstResult(firstResult);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }

    @Override
    public long countStudents(String nienKhoa, String khoa) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder("SELECT COUNT(s) FROM SinhVien s WHERE s.deleted = false");
            if (nienKhoa != null && !nienKhoa.trim().isEmpty()) {
                sb.append(" AND s.nienKhoa = :nienKhoa");
            }
            if (khoa != null && !khoa.trim().isEmpty()) {
                sb.append(" AND s.khoa = :khoa");
            }
            TypedQuery<Long> query = em.createQuery(sb.toString(), Long.class);
            if (nienKhoa != null && !nienKhoa.trim().isEmpty()) {
                query.setParameter("nienKhoa", nienKhoa);
            }
            if (khoa != null && !khoa.trim().isEmpty()) {
                query.setParameter("khoa", khoa);
            }
            return query.getSingleResult();
        });
    }

    @Override
    public long countTotalStudents() {
        return execute(em -> em.createQuery(
                "SELECT COUNT(s) FROM SinhVien s WHERE s.deleted = false", Long.class)
                .getSingleResult());
    }

    @Override
    public long countTotalLecturers() {
        return execute(em -> em.createQuery(
                "SELECT COUNT(g) FROM GiangVien g WHERE g.deleted = false", Long.class)
                .getSingleResult());
    }

    @Override
    public long countTotalClassesInSemester(String maHocKy) {
        return execute(em -> em.createQuery(
                "SELECT COUNT(l) FROM LopHocPhan l WHERE l.hocKy.maHocKy = :maHocKy AND l.deleted = false", Long.class)
                .setParameter("maHocKy", maHocKy)
                .getSingleResult());
    }

    @Override
    public long countTotalCourses() {
        return countCourses();
    }

    @Override
    public boolean updateGradeLock(String maLHP, boolean isLocked) {
        return execute(em -> {
            int updated = em.createQuery(
                    "UPDATE LopHocPhan l SET l.khoaDiem = :locked WHERE l.maLHP = :maLHP AND l.deleted = false")
                    .setParameter("locked", isLocked)
                    .setParameter("maLHP", maLHP)
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public List<YeuCauMoLop> findPendingClassApprovals() {
        return execute(em -> em.createQuery(
                "SELECT y FROM YeuCauMoLop y JOIN FETCH y.sinhVien s JOIN FETCH s.nguoiDung JOIN FETCH y.hocPhan " +
                "WHERE (y.trangThai = :choduyet OR y.trangThai = :datiepnhan) AND y.deleted = false ORDER BY y.ngayTao ASC", YeuCauMoLop.class)
                .setParameter("choduyet", TrangThaiYeuCau.CHODUYET)
                .setParameter("datiepnhan", TrangThaiYeuCau.DATIEPNHAN)
                .getResultList());
    }

    @Override
    public boolean updateApprovalStatus(String maYeuCau, TrangThaiYeuCau status) {
        return execute(em -> {
            int updated = em.createQuery(
                    "UPDATE YeuCauMoLop y SET y.trangThai = :status WHERE y.maYeuCau = :maYeuCau AND y.deleted = false")
                    .setParameter("status", status)
                    .setParameter("maYeuCau", maYeuCau)
                    .executeUpdate();
            return updated > 0;
        });
    }
}
