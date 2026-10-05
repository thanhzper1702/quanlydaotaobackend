package com.university.modules.headoffice;
import com.university.modules.headoffice.requestDTO.*;
import com.university.modules.headoffice.responseDTO.*;


public class HeadOfficeServiceImpl implements HeadOfficeService {

    @Override
    public HeadOfficeCourseResponseDTO getCourses(int page, int limit) {
        return null;
    }

    @Override
    public CreateCourseResponseDTO createCourse(CreateCourseRequestDTO request) {
        return null;
    }

    @Override
    public UpdateCourseResponseDTO updateCourse(String maHP, UpdateCourseRequestDTO request) {
        return null;
    }

    @Override
    public HeadOfficeSemesterResponseDTO getSemesters() {
        return null;
    }

    @Override
    public CreateSemesterResponseDTO createSemester(CreateSemesterRequestDTO request) {
        return null;
    }

    @Override
    public HeadOfficeLecturerResponseDTO getLecturers(String khoa, int page, int limit) {
        return null;
    }

    @Override
    public HeadOfficeStudentResponseDTO getStudents(String nienKhoa, String khoa, int page, int limit) {
        return null;
    }

    @Override
    public HeadOfficeStatisticsResponseDTO getStatistics(String maHocKy) {
        return null;
    }
}
