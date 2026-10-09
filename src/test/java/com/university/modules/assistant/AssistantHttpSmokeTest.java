package com.university.modules.assistant;

import com.university.core.common.ApiResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AssistantHttpSmokeTest {

    @Test
    void allThirteenRoutesReturnTheirExpectedHttpBoundaryResponse() {
        AssistantResponses.Result<Void> ok = AssistantResponses.Result.success(200, "ok");
        AssistantResponses.Result<Void> created = AssistantResponses.Result.success(201, "created");
        AssistantResponses.Result<List<String>> list = AssistantResponses.Result.success(200, "ok", List.of());
        AssistantResponses.FilePayload file = new AssistantResponses.FilePayload(
                new byte[]{1}, "students-LHP-01.xlsx", AssistantExcelExporter.XLSX_CONTENT_TYPE);
        AssistantResponses.Result<List<String>> export = AssistantResponses.Result.file(200, "ok", file);

        AssistantController controller = new AssistantController(service(Map.ofEntries(
                Map.entry("getSemesters", list),
                Map.entry("getClassesBySemester", list),
                Map.entry("createClass", created),
                Map.entry("updateClass", ok),
                Map.entry("deleteClass", ok),
                Map.entry("assignLecturer", ok),
                Map.entry("openRegistrationPeriod", ok),
                Map.entry("getClassStudents", export),
                Map.entry("getClassSchedules", list),
                Map.entry("updateSchedule", ok),
                Map.entry("deleteSchedule", ok),
                Map.entry("getClassRequests", list),
                Map.entry("updateClassRequestStatus", ok))));

        List<Response> jsonResponses = List.of(
                controller.getSemesters(),
                controller.getClassesBySemester("HK-01", 1, 10),
                controller.updateClass("LHP-01", new AssistantRequests.UpdateClassRequest()),
                controller.deleteClass("LHP-01"),
                controller.assignLecturer("LHP-01", new AssistantRequests.AssignLecturerRequest()),
                controller.openRegistrationPeriod(new AssistantRequests.OpenRegistrationPeriodRequest()),
                controller.getClassSchedules("LHP-01"),
                controller.updateSchedule("LHP-01", new AssistantRequests.UpdateScheduleRequest()),
                controller.deleteSchedule("LH-01"),
                controller.getClassRequests(null, 1, 10),
                controller.updateClassRequestStatus("YC-01", new AssistantRequests.UpdateClassRequestStatusRequest()));

        for (Response response : jsonResponses) {
            assertEquals(200, response.getStatus());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            assertInstanceOf(ApiResponse.class, response.getEntity());
        }

        Response create = controller.createClass("HK-01", new AssistantRequests.CreateClassRequest());
        assertEquals(201, create.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, create.getMediaType());
        assertInstanceOf(ApiResponse.class, create.getEntity());

        Response spreadsheet = controller.getClassStudents("LHP-01", 1, 10, true);
        assertEquals(200, spreadsheet.getStatus());
        assertEquals(AssistantExcelExporter.XLSX_CONTENT_TYPE, spreadsheet.getMediaType().toString());
        assertEquals("attachment; filename=\"students-LHP-01.xlsx\"",
                spreadsheet.getHeaderString("Content-Disposition"));
    }

    @Test
    void unexpectedExceptionDoesNotExposeInternalDetails() {
        String internalMessage = "SQLState 42P01 at C:\\secrets\\database.sql";
        Response response = new AssistantExceptionMapper().toResponse(new IllegalStateException(internalMessage));

        assertEquals(500, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        ApiResponse<?> body = assertInstanceOf(ApiResponse.class, response.getEntity());
        assertFalse(body.isSuccess());
        assertNotEquals(internalMessage, body.getMessage());
        assertFalse(body.getMessage().contains("SQLState"));
    }

    private static AssistantService service(Map<String, AssistantResponses.Result<?>> results) {
        return (AssistantService) Proxy.newProxyInstance(
                AssistantService.class.getClassLoader(),
                new Class[]{AssistantService.class},
                (proxy, method, args) -> results.get(method.getName()));
    }
}
