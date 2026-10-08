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
import com.university.core.repository.GenericRepository;
import java.util.List;
import java.util.Optional;

public interface HeadOfficeRepository extends GenericRepository<TruongPhongDaoTao, String> {

    List<HocPhan> findCourses(int page, int limit);

    long countCourses();

    Optional<HocPhan> findCourseById(String maHP);

    HocPhan saveCourse(HocPhan course);

    boolean softDeleteCourseIfUnused(String maHP);

    List<HocKy> findAllSemesters();

    Optional<HocKy> findSemesterById(String maHocKy);

    HocKy saveSemester(HocKy semester);

    List<LopHocPhan> findClasses(String maHocKy, int page, int limit);

    long countClasses(String maHocKy);

    Optional<LopHocPhan> findClassById(String maLHP);

    List<HoSoHocPhan> findClassGrades(String maLHP);

    List<GiangVien> findLecturers(String khoa, int page, int limit);

    long countLecturers(String khoa);

    List<SinhVien> findStudents(String nienKhoa, String khoa, int page, int limit);

    long countStudents(String nienKhoa, String khoa);

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

    long countTotalCourses();

    boolean updateGradeLock(String maLHP, boolean isLocked);

    List<YeuCauMoLop> findPendingClassApprovals();

    boolean updateApprovalStatus(String maYeuCau, TrangThaiYeuCau status);
}
