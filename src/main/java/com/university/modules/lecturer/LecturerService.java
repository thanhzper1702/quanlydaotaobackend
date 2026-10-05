package com.university.modules.lecturer;
import com.university.modules.lecturer.requestDTO.*;
import com.university.modules.lecturer.responseDTO.*;


public interface LecturerService {

    LecturerTeachingClassResponseDTO getTeachingClasses(String maGV, String maHocKy);

    LecturerClassGradeResponseDTO getClassGrades(String maGV, String maLHP);

    UpdateGradesResponseDTO updateClassGrades(String maGV, String maLHP, UpdateGradesRequestDTO request);

    byte[] exportGradesExcel(String maGV, String maLHP);

    LecturerTeachingScheduleResponseDTO getTeachingSchedule(String maGV, String maHocKy);

    LecturerAttendanceResponseDTO getAttendanceList(String maGV, String maLichHoc);

    UpdateAttendanceResponseDTO updateAttendance(String maGV, String maLichHoc, UpdateAttendanceRequestDTO request);
}
