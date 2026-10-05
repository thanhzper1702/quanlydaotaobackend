package com.university.modules.assistant;
import com.university.modules.assistant.requestDTO.*;
import com.university.modules.assistant.responseDTO.*;


public interface AssistantService {

    AssistantSemesterResponseDTO getSemesters();

    AssistantClassResponseDTO getClassesBySemester(String maHocKy, int page, int limit);

    CreateClassResponseDTO createClass(String maHocKy, CreateClassRequestDTO request);

    UpdateClassResponseDTO updateClass(String maLHP, UpdateClassRequestDTO request);

    DeleteClassResponseDTO deleteClass(String maLHP);

    AssignLecturerResponseDTO assignLecturer(String maLHP, AssignLecturerRequestDTO request);

    OpenRegistrationPeriodResponseDTO openRegistrationPeriod(OpenRegistrationPeriodRequestDTO request);

    AssistantClassStudentResponseDTO getClassStudents(String maLHP, boolean export);

    AssistantScheduleResponseDTO getClassSchedules(String maLHP);

    UpdateScheduleResponseDTO updateSchedule(String maLHP, UpdateScheduleRequestDTO request);

    DeleteScheduleResponseDTO deleteSchedule(String maLHP);

    AssistantClassRequestResponseDTO getClassRequests(String trangThai, int page, int limit);

    UpdateClassRequestStatusResponseDTO updateClassRequestStatus(String maYeuCau, UpdateClassRequestStatusRequestDTO request);
}
