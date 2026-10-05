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

    void deleteClass(String maLHP);

    boolean assignLecturer(String maLHP, String maGV);

    boolean updateRegistrationPeriod(String maLHP, OffsetDateTime bddk, OffsetDateTime ktdk);

    List<HoSoHocPhan> findStudentsByClass(String maLHP);

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
