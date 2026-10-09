package com.university.modules.assistant;

import com.university.core.model.GiangVien;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.YeuCauMoLop;
import com.university.core.repository.GenericRepository;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssistantRepository extends GenericRepository<TroLyDaoTao, String> {

    List<HocKy> findAllSemesters();

    Optional<HocKy> findSemesterById(String maHocKy);

    List<HocPhan> findAllCourses();

    Optional<HocPhan> findCourseById(String maHP);

    Optional<TroLyDaoTao> findAssistantById(String maNV);

    Optional<GiangVien> findLecturerById(String maGV);

    List<LopHocPhan> findClassesBySemester(String maHocKy, int page, int limit);

    long countClassesBySemester(String maHocKy);

    Optional<LopHocPhan> findClassById(String maLHP);

    LopHocPhan saveClass(LopHocPhan lhp);

    LopHocPhan createClassWithSchedules(LopHocPhan lhp, List<LichHoc> schedules);

    LopHocPhan updateClassWithSchedules(LopHocPhan lhp, List<LichHoc> schedules);

    void deleteClass(String maLHP);

    boolean deleteClassWithSchedulesIfNoStudents(String maLHP);

    boolean assignLecturer(String maLHP, String maGV);

    boolean updateRegistrationPeriod(String maLHP, OffsetDateTime bddk, OffsetDateTime ktdk);

    void updateRegistrationPeriods(List<String> classIds, OffsetDateTime bddk, OffsetDateTime ktdk);

    List<HoSoHocPhan> findStudentsByClass(String maLHP, int page, int limit);

    long countStudentsByClass(String maLHP);

    boolean hasLecturerConflict(String maGV, String maHocKy, int thu, int tietBatDau,
                                int soTiet, String excludedClassId);

    boolean hasRoomConflict(LocalDate ngayHoc, String phongHoc, int tietBatDau,
                            int soTiet, String excludedScheduleId);

    boolean hasRoomConflictExcludingClass(LocalDate ngayHoc, String phongHoc, int tietBatDau,
                                          int soTiet, String excludedClassId);

    List<LichHoc> findSchedulesByClass(String maLHP);

    Optional<LichHoc> findScheduleById(String maLichHoc);

    LichHoc saveSchedule(LichHoc lichHoc);

    void deleteScheduleById(String maLichHoc);

    void deleteSchedulesByClass(String maLHP);

    List<YeuCauMoLop> findClassRequests(String trangThai, int page, int limit);

    long countClassRequests(String trangThai);

    Optional<YeuCauMoLop> findClassRequestById(String maYeuCau);

    YeuCauMoLop saveClassRequest(YeuCauMoLop req);
}
