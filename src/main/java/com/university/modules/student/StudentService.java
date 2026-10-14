package com.university.modules.student;

public interface StudentService {

    StudentResponse.StudentGradeResponse getGrades(String maSV);

    StudentResponse.StudentGradeResponse getGrades(String maSV, String maHocKy);

    StudentResponse.StudentScheduleResponse getSchedule(String maSV, String maHocKy);

    StudentResponse.OpenedEnrollingClass getOpenClasses(String maHP, String keyword, int page, int limit);

    StudentResponse.OpenedEnrollingClass getOpenClasses(String maHP, String keyword, int page, int limit, String maHocKy);

    StudentResponse.RegisteredClassResponse getRegisteredClasses(String maSV);

    StudentResponse.RegisteredClassResponse getRegisteredClasses(String maSV, String maHocKy);

    StudentResponse.EnrollCourseResponse enrollCourse(String maSV, StudentRequest.EnrollCourseRequest request);

    StudentResponse.CancelEnrollmentResponse cancelEnrollment(String maSV, Long maHoSo);

    StudentResponse.OpenClassRequestResponse requestOpenCourse(String maSV, StudentRequest.OpenClassRequestRequest request);
}
