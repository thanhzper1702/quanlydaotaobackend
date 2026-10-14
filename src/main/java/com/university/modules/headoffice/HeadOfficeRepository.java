package com.university.modules.headoffice;

import com.university.core.model.GiangVien;
import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.SinhVien;
import com.university.core.model.TruongPhongDaoTao;
import com.university.core.repository.GenericRepository;
import java.util.List;
import java.util.Optional;

public interface HeadOfficeRepository extends GenericRepository<TruongPhongDaoTao, String> {

    List<HocPhan> findCourses(int page, int limit);

    long countCourses();

    Optional<HocPhan> findCourseById(String maHP);

    HocPhan saveCourse(HocPhan course);

    List<HocKy> findAllSemesters();

    Optional<HocKy> findSemesterById(String maHocKy);

    HocKy saveSemester(HocKy semester);

    List<GiangVien> findLecturers(String khoa, int page, int limit);
    List<GiangVien> findLecturers(String khoa, String keyword, int page, int limit);

    long countLecturers(String khoa);
    long countLecturers(String khoa, String keyword);

    long countClassesByLecturer(String maNV);

    List<SinhVien> findStudents(String nienKhoa, String khoa, int page, int limit);
    List<SinhVien> findStudents(String nienKhoa, String khoa, String keyword, int page, int limit);

    long countStudents(String nienKhoa, String khoa);
    long countStudents(String nienKhoa, String khoa, String keyword);

    long countTotalStudents();

    long countTotalLecturers();

    long countTotalClassesInSemester(String maHocKy);

    long countTotalClasses();

    long countFailedGrades(String maHocKy);

    long countSuspendedExamRecords(String maHocKy);

    List<Object[]> countStudentsByNienKhoa();

    List<Object[]> countStudentsByKhoa();

    List<Object[]> countStudentsByGender();

    List<Object[]> countAcademicResults(String maHocKy);

    List<Object[]> findSemesterAcademicAverages(String maHocKy, String nienKhoa, String khoa);

    long countTotalCourses();
}
