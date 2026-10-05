package com.university.modules.headoffice;
import com.university.modules.headoffice.requestDTO.*;
import com.university.modules.headoffice.responseDTO.*;


public interface HeadOfficeService {

    HeadOfficeCourseResponseDTO getCourses(int page, int limit);

    CreateCourseResponseDTO createCourse(CreateCourseRequestDTO request);

    UpdateCourseResponseDTO updateCourse(String maHP, UpdateCourseRequestDTO request);

    HeadOfficeSemesterResponseDTO getSemesters();

    CreateSemesterResponseDTO createSemester(CreateSemesterRequestDTO request);

    HeadOfficeLecturerResponseDTO getLecturers(String khoa, int page, int limit);

    HeadOfficeStudentResponseDTO getStudents(String nienKhoa, String khoa, int page, int limit);

    HeadOfficeStatisticsResponseDTO getStatistics(String maHocKy);
}
