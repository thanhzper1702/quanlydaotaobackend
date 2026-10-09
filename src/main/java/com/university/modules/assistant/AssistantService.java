package com.university.modules.assistant;

import java.util.List;

public interface AssistantService {

    AssistantResponses.Result<List<AssistantResponses.SemesterDTO>> getSemesters();

    AssistantResponses.Result<List<AssistantResponses.ClassDTO>> getClassesBySemester(
            String maHocKy, int page, int limit);

    AssistantResponses.Result<AssistantResponses.CreateClassData> createClass(
            String maHocKy, String maNV, AssistantRequests.CreateClassRequest request);

    AssistantResponses.Result<Void> updateClass(
            String maLHP, AssistantRequests.UpdateClassRequest request);

    AssistantResponses.Result<Void> deleteClass(String maLHP);

    AssistantResponses.Result<Void> assignLecturer(
            String maLHP, AssistantRequests.AssignLecturerRequest request);

    AssistantResponses.Result<Void> openRegistrationPeriod(
            AssistantRequests.OpenRegistrationPeriodRequest request);

    AssistantResponses.Result<List<AssistantResponses.StudentDTO>> getClassStudents(
            String maLHP, int page, int limit, boolean export);

    AssistantResponses.Result<List<AssistantResponses.ScheduleDTO>> getClassSchedules(String maLHP);

    AssistantResponses.Result<Void> updateSchedule(
            String maLHP, AssistantRequests.UpdateScheduleRequest request);

    AssistantResponses.Result<Void> deleteSchedule(String maLH);

    AssistantResponses.Result<List<AssistantResponses.ClassRequestDTO>> getClassRequests(
            String trangThai, int page, int limit);

    AssistantResponses.Result<Void> updateClassRequestStatus(
            String maYeuCau, AssistantRequests.UpdateClassRequestStatusRequest request);
}
