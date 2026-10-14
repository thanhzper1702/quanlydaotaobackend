package com.university.modules.headoffice;
import com.university.modules.headoffice.HeadOfficeRequestDTO.*;
import com.university.modules.headoffice.HeadOfficeResponseDTO.*;


public interface HeadOfficeService {

    HeadOfficeCourseResponseDTO getCourses(int page, int limit);

    CreateCourseResponseDTO createCourse(CreateCourseRequestDTO request);

    UpdateCourseResponseDTO updateCourse(String maHP, UpdateCourseRequestDTO request);

    HeadOfficeSemesterResponseDTO getSemesters();

    CreateSemesterResponseDTO createSemester(CreateSemesterRequestDTO request);

    void updateSemester(String maHocKy, UpdateSemesterRequestDTO request);

    HeadOfficeLecturerResponseDTO getLecturers(String khoa, int page, int limit);
    HeadOfficeLecturerResponseDTO getLecturers(String khoa, String keyword, int page, int limit);

    HeadOfficeStudentResponseDTO getStudents(String nienKhoa, String khoa, int page, int limit);
    HeadOfficeStudentResponseDTO getStudents(String nienKhoa, String khoa, String keyword, int page, int limit);

    HeadOfficeStatisticsResponseDTO getStatistics(String maHocKy);

    SemesterAcademicStatisticsResponseDTO getSemesterAcademicStatistics(String maHocKy, String nienKhoa,
                                                                          String khoa, String hocLuc);
}
