package com.university.modules.headoffice;
import com.university.modules.headoffice.HeadOfficeRequestDTO.*;
import com.university.modules.headoffice.HeadOfficeResponseDTO.*;


public interface HeadOfficeService {

    HeadOfficeCourseResponseDTO getCourses(int page, int limit);

    CreateCourseResponseDTO createCourse(CreateCourseRequestDTO request);

    UpdateCourseResponseDTO updateCourse(String maHP, UpdateCourseRequestDTO request);

    void deleteCourse(String maHP);

    HeadOfficeSemesterResponseDTO getSemesters();

    CreateSemesterResponseDTO createSemester(CreateSemesterRequestDTO request);

    HeadOfficeClassResponseDTO getClasses(String maHocKy, int page, int limit);

    HeadOfficeClassGradeResponseDTO getClassGrades(String maLHP);

    HeadOfficeClassResponseDTO.ClassItem updateGradeLock(String maLHP, boolean khoaDiem);

    HeadOfficeLecturerResponseDTO getLecturers(String khoa, int page, int limit);

    HeadOfficeStudentResponseDTO getStudents(String nienKhoa, String khoa, int page, int limit);

    HeadOfficeStatisticsResponseDTO getStatistics(String maHocKy);

    byte[] exportStatisticsExcel(String maHocKy);

    byte[] exportLecturersExcel(String khoa);

    byte[] exportStudentsExcel(String nienKhoa, String khoa);

    byte[] exportClassGradesExcel(String maLHP);
}
