package com.university.modules.student;

import com.university.core.model.HoSoHocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.SinhVien;
import com.university.core.model.YeuCauMoLop;
import com.university.core.repository.GenericRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends GenericRepository<SinhVien, String> {

    Optional<SinhVien> findByMaSV(String maSV);

    List<LopHocPhan> findOpenClasses(String maHocKy, String keyword, int page, int limit);

    long countOpenClasses(String maHocKy, String keyword);

    Optional<LopHocPhan> findClassByMaLHP(String maLHP);

    List<HoSoHocPhan> findRegisteredClasses(String maSV);

    List<HoSoHocPhan> findRegisteredClassesByHocKy(String maSV, String maHocKy);

    Optional<HoSoHocPhan> findEnrollment(String maSV, String maLHP);

    Optional<HoSoHocPhan> findEnrollmentById(Long maHoSo);

    HoSoHocPhan saveEnrollment(HoSoHocPhan hoSo);

    void deleteEnrollment(HoSoHocPhan hoSo);

    List<HoSoHocPhan> findStudentGrades(String maSV);

    List<LichHoc> findStudentSchedule(String maSV, String maHocKy);

    List<LopHocPhan> findStudentEnrolledClasses(String maSV, String maHocKy);

    YeuCauMoLop saveClassRequest(YeuCauMoLop request);

    List<YeuCauMoLop> findClassRequestsByStudent(String maSV);
}
