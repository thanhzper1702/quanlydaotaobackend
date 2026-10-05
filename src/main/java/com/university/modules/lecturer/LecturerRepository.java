package com.university.modules.lecturer;

import com.university.core.model.DiemDanh;
import com.university.core.model.GiangVien;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.repository.GenericRepository;
import java.util.List;
import java.util.Optional;

public interface LecturerRepository extends GenericRepository<GiangVien, String> {

    Optional<GiangVien> findByMaGV(String maGV);

    List<LopHocPhan> findTeachingClasses(String maGV, String maHocKy);

    List<LopHocPhan> findLecturerAssignedClasses(String maGV, String maHocKy);

    Optional<LopHocPhan> findClassById(String maLHP);

    List<HoSoHocPhan> findStudentsAndGradesInClass(String maLHP);

    Optional<HoSoHocPhan> findGradeRecord(Long maHoSo);

    HoSoHocPhan saveGrade(HoSoHocPhan record);

    List<LichHoc> findTeachingSchedule(String maGV, String maHocKy);

    Optional<LichHoc> findScheduleSession(String maLichHoc);

    List<DiemDanh> findAttendanceBySession(String maLichHoc);

    Optional<DiemDanh> findAttendanceBySessionAndStudent(String maLichHoc, String maSV);

    DiemDanh saveAttendance(DiemDanh diemDanh);
}
