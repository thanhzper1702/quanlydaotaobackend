package com.university.modules.student;
import com.university.modules.student.requestDTO.*;
import com.university.modules.student.responseDTO.*;


public class StudentServiceImpl implements StudentService {

    @Override
    public StudentGradeResponseDTO getGrades(String maSV) {
        return null;
    }

    @Override
    public StudentScheduleResponseDTO getSchedule(String maSV, String maHocKy) {
        return null;
    }

    @Override
    public OpenedEnrollingClass getOpenClasses(String maHP, String keyword, int page, int limit) {
        return null;
    }

    @Override
    public RegisteredClassResponseDTO getRegisteredClasses(String maSV) {
        return null;
    }

    @Override
    public EnrollCourseResponseDTO enrollCourse(String maSV, EnrollCourseRequestDTO request) {
        return null;
    }

    @Override
    public CancelEnrollmentResponseDTO cancelEnrollment(String maSV, Long maHoSo) {
        return null;
    }

    @Override
    public OpenClassRequestResponseDTO requestOpenCourse(String maSV, OpenClassRequestRequestDTO request) {
        return null;
    }
}
