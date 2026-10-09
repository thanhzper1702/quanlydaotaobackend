package com.university.modules.headoffice;

import com.university.core.model.GiangVien;
import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.LopHocPhan;
import com.university.core.model.SinhVien;
import com.university.core.model.TruongPhongDaoTao;
import com.university.core.model.YeuCauMoLop;
import com.university.core.model.enums.TrangThaiYeuCau;
import com.university.core.model.enums.KetQua;
import com.university.core.model.enums.TrangThaiHocPhan;
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
        return executeInTransaction(em -> {
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
    public boolean softDeleteCourseIfUnused(String maHP) {
        return executeInTransaction(em -> {
            int updated = em.createQuery(
                            "UPDATE HocPhan h SET h.deleted = true " +
                                    "WHERE h.maHP = :maHP AND h.deleted = false " +
                                    "AND NOT EXISTS (SELECT l FROM LopHocPhan l WHERE l.monHoc = h) " +
                                    "AND NOT EXISTS (SELECT y FROM YeuCauMoLop y WHERE y.hocPhan = h)")
                    .setParameter("maHP", maHP)
                    .executeUpdate();
            return updated > 0;
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
        return executeInTransaction(em -> {
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
    public List<LopHocPhan> findClasses(String maHocKy, int page, int limit) {
        return execute(em -> {
            TypedQuery<LopHocPhan> query = em.createQuery(
                    "SELECT l FROM LopHocPhan l JOIN FETCH l.monHoc JOIN FETCH l.hocKy " +
                            "LEFT JOIN FETCH l.giangVien g LEFT JOIN FETCH g.nguoiDung " +
                            "WHERE l.hocKy.maHocKy = :maHocKy AND l.deleted = false ORDER BY l.maLHP ASC", LopHocPhan.class)
                    .setParameter("maHocKy", maHocKy);
            query.setFirstResult(Math.max(0, (page - 1) * limit));
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    @Override
    public long countClasses(String maHocKy) {
        return execute(em -> em.createQuery(
                "SELECT COUNT(l) FROM LopHocPhan l WHERE l.hocKy.maHocKy = :maHocKy AND l.deleted = false", Long.class)
                .setParameter("maHocKy", maHocKy).getSingleResult());
    }

    @Override
    public Optional<LopHocPhan> findClassById(String maLHP) {
        return execute(em -> {
            try {
                return Optional.of(em.createQuery(
                                "SELECT l FROM LopHocPhan l JOIN FETCH l.monHoc JOIN FETCH l.hocKy " +
                                        "WHERE l.maLHP = :maLHP AND l.deleted = false", LopHocPhan.class)
                        .setParameter("maLHP", maLHP).getSingleResult());
            } catch (NoResultException exception) {
                return Optional.empty();
            }
        });
    }

    @Override
    public List<HoSoHocPhan> findClassGrades(String maLHP) {
        return execute(em -> em.createQuery(
                        "SELECT h FROM HoSoHocPhan h JOIN FETCH h.sinhVien s JOIN FETCH s.nguoiDung " +
                                "WHERE h.lopHocPhan.maLHP = :maLHP AND h.deleted = false ORDER BY s.maSV ASC", HoSoHocPhan.class)
                .setParameter("maLHP", maLHP).getResultList());
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
    public long countClassesByLecturer(String maNV) {
        return execute(em -> em.createQuery(
                "SELECT COUNT(l) FROM LopHocPhan l WHERE l.giangVien.maNV = :maNV AND l.deleted = false",
                Long.class)
                .setParameter("maNV", maNV)
                .getSingleResult());
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
    public long countTotalClasses() {
        return execute(em -> em.createQuery(
                "SELECT COUNT(l) FROM LopHocPhan l WHERE l.deleted = false", Long.class)
                .getSingleResult());
    }

    @Override
    public long countFailedGrades(String maHocKy) {
        return execute(em -> em.createQuery(
                        "SELECT COUNT(DISTINCT h.sinhVien.maSV) FROM HoSoHocPhan h JOIN h.lopHocPhan l " +
                                "WHERE h.deleted = false AND l.deleted = false " +
                                "AND h.ketQua = :ketQua AND l.hocKy.maHocKy = :maHocKy", Long.class)
                .setParameter("ketQua", KetQua.F)
                .setParameter("maHocKy", maHocKy)
                .getSingleResult());
    }

    @Override
    public long countSuspendedExamRecords(String maHocKy) {
        return execute(em -> em.createQuery(
                        "SELECT COUNT(DISTINCT h.sinhVien.maSV) FROM HoSoHocPhan h JOIN h.lopHocPhan l " +
                                "WHERE h.deleted = false AND l.deleted = false " +
                                "AND h.trangThai = :trangThai AND l.hocKy.maHocKy = :maHocKy", Long.class)
                .setParameter("trangThai", TrangThaiHocPhan.DINHCHITHI)
                .setParameter("maHocKy", maHocKy)
                .getSingleResult());
    }

    @Override
    public List<Object[]> countStudentsByNienKhoa() {
        return execute(em -> em.createQuery(
                        "SELECT s.nienKhoa, COUNT(s) FROM SinhVien s " +
                                "WHERE s.deleted = false GROUP BY s.nienKhoa ORDER BY s.nienKhoa", Object[].class)
                .getResultList());
    }

    @Override
    public List<Object[]> countStudentsByKhoa() {
        return execute(em -> em.createQuery(
                        "SELECT s.khoa, COUNT(s) FROM SinhVien s " +
                                "WHERE s.deleted = false GROUP BY s.khoa ORDER BY s.khoa", Object[].class)
                .getResultList());
    }

    @Override
    public List<Object[]> countStudentsByGender() {
        return execute(em -> em.createQuery(
                        "SELECT s.nguoiDung.gioiTinh, COUNT(s) FROM SinhVien s " +
                                "WHERE s.deleted = false GROUP BY s.nguoiDung.gioiTinh ORDER BY s.nguoiDung.gioiTinh", Object[].class)
                .getResultList());
    }

    @Override
    public List<Object[]> countAcademicResults(String maHocKy) {
        return execute(em -> {
            String jpql = "SELECT h.ketQua, COUNT(DISTINCT h.sinhVien.maSV) " +
                    "FROM HoSoHocPhan h JOIN h.lopHocPhan l " +
                    "WHERE h.deleted = false AND l.deleted = false";
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                jpql += " AND l.hocKy.maHocKy = :maHocKy";
            }
            jpql += " GROUP BY h.ketQua ORDER BY h.ketQua";
            var query = em.createQuery(jpql, Object[].class);
            if (maHocKy != null && !maHocKy.trim().isEmpty()) {
                query.setParameter("maHocKy", maHocKy.trim());
            }
            return query.getResultList();
        });
    }

    @Override
    public long countTotalCourses() {
        return countCourses();
    }

    @Override
    public boolean updateGradeLock(String maLHP, boolean isLocked) {
        return executeInTransaction(em -> {
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
        return executeInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE YeuCauMoLop y SET y.trangThai = :status WHERE y.maYeuCau = :maYeuCau AND y.deleted = false")
                    .setParameter("status", status)
                    .setParameter("maYeuCau", maYeuCau)
                    .executeUpdate();
            return updated > 0;
        });
    }
}
