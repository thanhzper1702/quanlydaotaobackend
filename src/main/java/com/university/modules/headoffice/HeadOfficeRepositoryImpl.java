package com.university.modules.headoffice;

import com.university.core.model.GiangVien;
import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.LopHocPhan;
import com.university.core.model.SinhVien;
import com.university.core.model.TruongPhongDaoTao;
import com.university.core.model.enums.KetQua;
import com.university.core.model.enums.TrangThaiHocPhan;
import com.university.core.repository.AbstractJpaRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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


    private static List<String> resolveKhoaAliases(String khoa) {
        if (khoa == null || khoa.trim().isEmpty() || "all".equalsIgnoreCase(khoa.trim())) {
            return List.of();
        }
        String k = khoa.trim();
        String lower = k.toLowerCase();
        Set<String> aliases = new LinkedHashSet<>();
        aliases.add(k);
        aliases.add(lower);
        if ("cntt".equals(lower) || lower.contains("công nghệ thông tin")) {
            aliases.add("CNTT");
            aliases.add("Công nghệ thông tin");
            aliases.add("Công nghệ Thông tin");
            aliases.add("Khoa Công nghệ thông tin");
            aliases.add("Khoa Công nghệ Thông tin");
        } else if ("kt".equals(lower) || lower.contains("kinh tế")) {
            aliases.add("KT");
            aliases.add("Kinh tế");
            aliases.add("Khoa Kinh tế");
        } else if ("ddt".equals(lower) || lower.contains("điện")) {
            aliases.add("DDT");
            aliases.add("Điện - Điện tử");
            aliases.add("Khoa Điện - Điện tử");
        } else if ("ckot".equals(lower) || lower.contains("cơ khí") || lower.contains("ô tô")) {
            aliases.add("CKOT");
            aliases.add("Cơ khí Ô tô");
            aliases.add("Khoa Cơ khí Ô tô");
        }
        return new ArrayList<>(aliases);
    }

    @Override
    public List<GiangVien> findLecturers(String khoa, int page, int limit) {
        return findLecturers(khoa, null, page, limit);
    }

    @Override
    public List<GiangVien> findLecturers(String khoa, String keyword, int page, int limit) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder("SELECT g FROM GiangVien g JOIN FETCH g.nguoiDung WHERE g.deleted = false");
            boolean hasKhoa = khoa != null && !khoa.trim().isEmpty() && !"all".equalsIgnoreCase(khoa.trim());
            boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

            if (hasKhoa) {
                sb.append(" AND (LOWER(g.khoa) = :khoaLower OR g.khoa = :khoa OR g.khoa IN :khoaAliases OR LOWER(g.khoa) LIKE :khoaLike)");
            }
            if (hasKeyword) {
                sb.append(" AND (LOWER(g.maNV) LIKE :kw OR LOWER(g.nguoiDung.hoTen) LIKE :kw OR LOWER(g.nguoiDung.email) LIKE :kw OR LOWER(g.nguoiDung.soDienThoai) LIKE :kw)");
            }
            sb.append(" ORDER BY g.maNV ASC");

            TypedQuery<GiangVien> query = em.createQuery(sb.toString(), GiangVien.class);
            if (hasKhoa) {
                query.setParameter("khoaLower", khoa.trim().toLowerCase());
                query.setParameter("khoa", khoa.trim());
                query.setParameter("khoaAliases", resolveKhoaAliases(khoa));
                query.setParameter("khoaLike", "%" + khoa.trim().toLowerCase() + "%");
            }
            if (hasKeyword) {
                query.setParameter("kw", "%" + keyword.trim().toLowerCase() + "%");
            }

            int firstResult = Math.max(0, (page - 1) * limit);
            query.setFirstResult(firstResult);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }

    @Override
    public long countLecturers(String khoa) {
        return countLecturers(khoa, null);
    }

    @Override
    public long countLecturers(String khoa, String keyword) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder("SELECT COUNT(g) FROM GiangVien g JOIN g.nguoiDung u WHERE g.deleted = false");
            boolean hasKhoa = khoa != null && !khoa.trim().isEmpty() && !"all".equalsIgnoreCase(khoa.trim());
            boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

            if (hasKhoa) {
                sb.append(" AND (LOWER(g.khoa) = :khoaLower OR g.khoa = :khoa OR g.khoa IN :khoaAliases OR LOWER(g.khoa) LIKE :khoaLike)");
            }
            if (hasKeyword) {
                sb.append(" AND (LOWER(g.maNV) LIKE :kw OR LOWER(u.hoTen) LIKE :kw OR LOWER(u.email) LIKE :kw OR LOWER(u.soDienThoai) LIKE :kw)");
            }
            TypedQuery<Long> query = em.createQuery(sb.toString(), Long.class);
            if (hasKhoa) {
                query.setParameter("khoaLower", khoa.trim().toLowerCase());
                query.setParameter("khoa", khoa.trim());
                query.setParameter("khoaAliases", resolveKhoaAliases(khoa));
                query.setParameter("khoaLike", "%" + khoa.trim().toLowerCase() + "%");
            }
            if (hasKeyword) {
                query.setParameter("kw", "%" + keyword.trim().toLowerCase() + "%");
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
    public List<SinhVien> findStudents(String nienKhoa, String khoa, String keyword, int page, int limit) {
        return execute(em -> {
            StringBuilder sb = new StringBuilder("SELECT s FROM SinhVien s JOIN FETCH s.nguoiDung WHERE s.deleted = false");
            boolean hasNienKhoa = nienKhoa != null && !nienKhoa.trim().isEmpty() && !"all".equalsIgnoreCase(nienKhoa.trim());
            boolean hasKhoa = khoa != null && !khoa.trim().isEmpty() && !"all".equalsIgnoreCase(khoa.trim());
            boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

            if (hasNienKhoa) {
                sb.append(" AND s.nienKhoa = :nienKhoa");
            }
            if (hasKhoa) {
                sb.append(" AND (LOWER(s.khoa) = :khoaLower OR s.khoa = :khoa)");
            }
            if (hasKeyword) {
                sb.append(" AND (LOWER(s.maSV) LIKE :kw OR LOWER(s.nguoiDung.hoTen) LIKE :kw)");
            }
            sb.append(" ORDER BY s.maSV ASC");

            TypedQuery<SinhVien> query = em.createQuery(sb.toString(), SinhVien.class);
            if (hasNienKhoa) {
                query.setParameter("nienKhoa", nienKhoa.trim());
            }
            if (hasKhoa) {
                query.setParameter("khoaLower", khoa.trim().toLowerCase());
                query.setParameter("khoa", khoa.trim());
            }
            if (hasKeyword) {
                query.setParameter("kw", "%" + keyword.trim().toLowerCase() + "%");
            }

            int firstResult = Math.max(0, (page - 1) * limit);
            query.setFirstResult(firstResult);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }

    @Override
    public long countStudents(String nienKhoa, String khoa, String keyword) {
        return execute(em -> {
            boolean hasNienKhoa = nienKhoa != null && !nienKhoa.trim().isEmpty() && !"all".equalsIgnoreCase(nienKhoa.trim());
            boolean hasKhoa = khoa != null && !khoa.trim().isEmpty() && !"all".equalsIgnoreCase(khoa.trim());
            boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

            StringBuilder sb = new StringBuilder("SELECT COUNT(s) FROM SinhVien s");
            if (hasKeyword) {
                sb.append(" JOIN s.nguoiDung u");
            }
            sb.append(" WHERE s.deleted = false");

            if (hasNienKhoa) {
                sb.append(" AND s.nienKhoa = :nienKhoa");
            }
            if (hasKhoa) {
                sb.append(" AND (LOWER(s.khoa) = :khoaLower OR s.khoa = :khoa)");
            }
            if (hasKeyword) {
                sb.append(" AND (LOWER(s.maSV) LIKE :kw OR LOWER(u.hoTen) LIKE :kw)");
            }

            TypedQuery<Long> query = em.createQuery(sb.toString(), Long.class);
            if (hasNienKhoa) {
                query.setParameter("nienKhoa", nienKhoa.trim());
            }
            if (hasKhoa) {
                query.setParameter("khoaLower", khoa.trim().toLowerCase());
                query.setParameter("khoa", khoa.trim());
            }
            if (hasKeyword) {
                query.setParameter("kw", "%" + keyword.trim().toLowerCase() + "%");
            }

            return query.getSingleResult();
        });
    }

    @Override
    public List<SinhVien> findStudents(String nienKhoa, String khoa, int page, int limit) {
        return findStudents(nienKhoa, khoa, null, page, limit);
    }

    @Override
    public long countStudents(String nienKhoa, String khoa) {
        return countStudents(nienKhoa, khoa, null);
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
    @SuppressWarnings("unchecked")
    public List<Object[]> findSemesterAcademicAverages(String maHocKy, String nienKhoa, String khoa) {
        return execute(em -> {
            StringBuilder sql = new StringBuilder("""
                    SELECT s.masv,
                           COALESCE(SUM(CASE WHEN l.mahocky = :maHocKy AND h.diemtongket IS NOT NULL
                                THEN h.diemtongket * hp.sotinchi ELSE 0 END), 0) AS weighted_score,
                           COALESCE(SUM(CASE WHEN l.mahocky = :maHocKy AND h.diemtongket IS NOT NULL
                                THEN hp.sotinchi ELSE 0 END), 0) AS scored_credits
                    FROM sinhvien s
                    LEFT JOIN hosohocphan h ON h.masv = s.masv AND h.deleted = false
                    LEFT JOIN lophocphan l ON l.malhp = h.malhp AND l.deleted = false
                    LEFT JOIN hocphan hp ON hp.mahp = l.mahp AND hp.deleted = false
                    WHERE s.deleted = false
                    """);
            if (nienKhoa != null && !nienKhoa.isBlank() && !"all".equalsIgnoreCase(nienKhoa)) {
                sql.append(" AND s.nienkhoa = :nienKhoa");
            }
            if (khoa != null && !khoa.isBlank() && !"all".equalsIgnoreCase(khoa)) {
                sql.append(" AND LOWER(s.khoa) = LOWER(:khoa)");
            }
            sql.append(" GROUP BY s.masv ORDER BY s.masv");

            var query = em.createNativeQuery(sql.toString()).setParameter("maHocKy", maHocKy);
            if (nienKhoa != null && !nienKhoa.isBlank() && !"all".equalsIgnoreCase(nienKhoa)) {
                query.setParameter("nienKhoa", nienKhoa);
            }
            if (khoa != null && !khoa.isBlank() && !"all".equalsIgnoreCase(khoa)) {
                query.setParameter("khoa", khoa);
            }
            return query.getResultList();
        });
    }

    @Override
    public long countTotalCourses() {
        return countCourses();
    }
}
