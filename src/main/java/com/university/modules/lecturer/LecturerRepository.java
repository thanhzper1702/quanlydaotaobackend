package com.university.modules.lecturer;

import com.university.core.model.DiemDanh;
import com.university.core.model.GiangVien;
import com.university.core.model.HocKy;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.repository.GenericRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface LecturerRepository extends GenericRepository<GiangVien, String> {

    Optional<GiangVien> findByMaGV(String maGV);

    Optional<HocKy> findSemesterById(String maHocKy);

    Optional<HocKy> findCurrentSemester(LocalDate date);

    List<LopHocPhan> findTeachingClasses(String maGV, String maHocKy);

    List<LopHocPhan> findLecturerAssignedClasses(String maGV, String maHocKy);

    Optional<LopHocPhan> findClassById(String maLHP);

    List<HoSoHocPhan> findStudentsAndGradesInClass(String maLHP);

    Optional<HoSoHocPhan> findGradeRecord(Long maHoSo);

    HoSoHocPhan saveGrade(HoSoHocPhan record);

    List<LichHoc> findTeachingSchedule(String maGV, String maHocKy);

    List<LichHoc> findTeachingSchedule(String maGV, String maHocKy, LocalDate from, LocalDate through);

    Optional<LichHoc> findScheduleSession(String maLichHoc);

    List<DiemDanh> findAttendanceBySession(String maLichHoc);

    Optional<DiemDanh> findAttendanceBySessionAndStudent(String maLichHoc, String maSV);

    DiemDanh saveAttendance(DiemDanh diemDanh);

    <T> T withClassTransaction(String maLHP, Function<LecturerRepository, T> callback);
}
