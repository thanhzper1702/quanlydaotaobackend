package com.university.modules.student;
import com.university.modules.student.requestDTO.*;
import com.university.modules.student.responseDTO.*;


public interface StudentService {

    StudentGradeResponseDTO getGrades(String maSV);

    StudentScheduleResponseDTO getSchedule(String maSV, String maHocKy);

    OpenedEnrollingClass getOpenClasses(String maHP, String keyword, int page, int limit);

    RegisteredClassResponseDTO getRegisteredClasses(String maSV);

    EnrollCourseResponseDTO enrollCourse(String maSV, EnrollCourseRequestDTO request);

    CancelEnrollmentResponseDTO cancelEnrollment(String maSV, Long maHoSo);

    OpenClassRequestResponseDTO requestOpenCourse(String maSV, OpenClassRequestRequestDTO request);
}
