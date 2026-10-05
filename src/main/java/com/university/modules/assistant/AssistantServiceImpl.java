package com.university.modules.assistant;
import com.university.modules.assistant.requestDTO.*;
import com.university.modules.assistant.responseDTO.*;


public class AssistantServiceImpl implements AssistantService {

    @Override
    public AssistantSemesterResponseDTO getSemesters() {
        return null;
    }

    @Override
    public AssistantClassResponseDTO getClassesBySemester(String maHocKy, int page, int limit) {
        return null;
    }

    @Override
    public CreateClassResponseDTO createClass(String maHocKy, CreateClassRequestDTO request) {
        return null;
    }

    @Override
    public UpdateClassResponseDTO updateClass(String maLHP, UpdateClassRequestDTO request) {
        return null;
    }

    @Override
    public DeleteClassResponseDTO deleteClass(String maLHP) {
        return null;
    }

    @Override
    public AssignLecturerResponseDTO assignLecturer(String maLHP, AssignLecturerRequestDTO request) {
        return null;
    }

    @Override
    public OpenRegistrationPeriodResponseDTO openRegistrationPeriod(OpenRegistrationPeriodRequestDTO request) {
        return null;
    }

    @Override
    public AssistantClassStudentResponseDTO getClassStudents(String maLHP, boolean export) {
        return null;
    }

    @Override
    public AssistantScheduleResponseDTO getClassSchedules(String maLHP) {
        return null;
    }

    @Override
    public UpdateScheduleResponseDTO updateSchedule(String maLHP, UpdateScheduleRequestDTO request) {
        return null;
    }

    @Override
    public DeleteScheduleResponseDTO deleteSchedule(String maLHP) {
        return null;
    }

    @Override
    public AssistantClassRequestResponseDTO getClassRequests(String trangThai, int page, int limit) {
        return null;
    }

    @Override
    public UpdateClassRequestStatusResponseDTO updateClassRequestStatus(String maYeuCau, UpdateClassRequestStatusRequestDTO request) {
        return null;
    }
}
