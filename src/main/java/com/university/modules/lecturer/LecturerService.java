package com.university.modules.lecturer;
import com.university.modules.lecturer.LectureRequest.UpdateAttendanceRequest;
import com.university.modules.lecturer.LectureRequest.UpdateGradesRequest;
import com.university.modules.lecturer.LectureResponse.LecturerAttendanceResponse;
import com.university.modules.lecturer.LectureResponse.LecturerClassGradeResponse;
import com.university.modules.lecturer.LectureResponse.LecturerTeachingClassResponse;
import com.university.modules.lecturer.LectureResponse.LecturerTeachingScheduleResponse;


public interface LecturerService {

    LecturerTeachingClassResponse getTeachingClasses(String maGV, String maHocKy);

    LecturerClassGradeResponse getClassGrades(String maGV, String maLHP);

    void updateClassGrades(String maGV, String maLHP, UpdateGradesRequest request);

    byte[] exportGradesExcel(String maGV, String maLHP);

    LecturerTeachingScheduleResponse getTeachingSchedule(String maGV, String maHocKy, Integer tuan);

    LecturerAttendanceResponse getAttendanceList(String maGV, String maLichHoc);

    void updateAttendance(String maGV, String maLichHoc, UpdateAttendanceRequest request);
}
