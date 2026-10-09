package com.university.modules.assistant;

import com.university.core.common.ApiResponse;
import com.university.core.model.HocKy;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.enums.TenHocKy;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistantReadEndpointsTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-08T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void semestersReturnCurrentMetaAndEmptyListHasExactMeta() {
        HocKy current = semester("2026_2027_HK1", LocalDate.of(2026, 9, 1), LocalDate.of(2027, 1, 15));
        HocKy past = semester("2025_2026_HK2", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 5, 31));
        AssistantServiceImpl service = service(repo(Map.of("findAllSemesters", List.of(current, past))));

        AssistantResponses.Result<List<AssistantResponses.SemesterDTO>> result = service.getSemesters();

        assertEquals(200, result.getStatusCode());
        assertTrue(result.getData().get(0).isEditable());
        assertFalse(result.getData().get(1).isEditable());
        AssistantResponses.SemesterMeta meta = assertInstanceOf(
                AssistantResponses.SemesterMeta.class, result.getMeta());
        assertEquals(2, meta.getTongSoHocKy());
        assertEquals("2026_2027_HK1", meta.getHocKyHienTai());

        AssistantResponses.Result<List<AssistantResponses.SemesterDTO>> empty =
                service(repo(Map.of("findAllSemesters", List.of()))).getSemesters();
        assertEquals(List.of(), empty.getData());
        AssistantResponses.SemesterMeta emptyMeta = assertInstanceOf(
                AssistantResponses.SemesterMeta.class, empty.getMeta());
        assertEquals(0, emptyMeta.getTongSoHocKy());
        assertNull(emptyMeta.getHocKyHienTai());
    }

    @Test
    void classesValidateExistenceAndCalculateCeilingMeta() {
        HocKy semester = semester("HK1", LocalDate.of(2026, 9, 1), LocalDate.of(2027, 1, 15));
        LopHocPhan courseClass = courseClass("LHP-01", 3, 60);
        LichHoc schedule = schedule("LH-01", "A101");
        Map<String, Object> values = new HashMap<>();
        values.put("findSemesterById", Optional.of(semester));
        values.put("findClassesBySemester", List.of(courseClass));
        values.put("countClassesBySemester", 21L);
        values.put("findSchedulesByClass", List.of(schedule));

        AssistantResponses.Result<List<AssistantResponses.ClassDTO>> result =
                service(repo(values)).getClassesBySemester(" HK1 ", 3, 10);

        assertEquals("A101", result.getData().get(0).getPhongHoc());
        AssistantResponses.ClassMeta meta = assertInstanceOf(
                AssistantResponses.ClassMeta.class, result.getMeta());
        assertEquals(3, meta.getTotalPages());
        assertEquals(21, meta.getTotalItems());

        AssistantResponses.Result<?> invalid = service(repo(Map.of()))
                .getClassesBySemester(" ", 0, 0);
        assertEquals(400, invalid.getStatusCode());
        assertEquals(3, invalid.getErrors().size());

        AssistantResponses.Result<?> missing = service(repo(Map.of("findSemesterById", Optional.empty())))
                .getClassesBySemester("HK-X", 1, 10);
        assertEquals(404, missing.getStatusCode());
    }

    @Test
    void studentJsonAndExportModesUseDifferentQueries() {
        LopHocPhan courseClass = courseClass("LHP-01", 2, 50);
        Map<String, Object> values = new HashMap<>();
        values.put("findClassById", Optional.of(courseClass));
        values.put("findStudentsByClass", List.of());
        values.put("countStudentsByClass", 11L);
        AssistantServiceImpl service = service(repo(values));

        AssistantResponses.Result<?> json = service.getClassStudents("LHP-01", 2, 10, false);
        AssistantResponses.StudentMeta meta = assertInstanceOf(
                AssistantResponses.StudentMeta.class, json.getMeta());
        assertEquals(2, meta.getTotalPages());
        assertEquals(2, meta.getSiSoHienTai());

        AssistantResponses.Result<?> exported = service.getClassStudents("LHP-01", 0, 0, true);
        assertTrue(exported.isFileResponse());
        assertTrue(exported.getFile().getFileName().endsWith(".xlsx"));
        assertTrue(exported.getFile().getContent().length > 0);
    }

    @Test
    void schedulesAndRequestsReturnStableMetaAndRejectInvalidFilter() {
        LopHocPhan courseClass = courseClass("LHP-01", 0, 30);
        Map<String, Object> scheduleValues = new HashMap<>();
        scheduleValues.put("findClassById", Optional.of(courseClass));
        scheduleValues.put("findSchedulesByClass", List.of(schedule("LH-01", "A101")));
        AssistantResponses.Result<?> schedules = service(repo(scheduleValues)).getClassSchedules("LHP-01");
        AssistantResponses.ScheduleMeta scheduleMeta = assertInstanceOf(
                AssistantResponses.ScheduleMeta.class, schedules.getMeta());
        assertEquals(1, scheduleMeta.getTongSoBuoiHoc());

        AssistantResponses.Result<?> invalid = service(repo(Map.of()))
                .getClassRequests("not-a-status", 1, 10);
        assertEquals(400, invalid.getStatusCode());

        Map<String, Object> requestValues = Map.of(
                "findClassRequests", List.of(),
                "countClassRequests", 0L);
        AssistantResponses.Result<?> empty = service(repo(requestValues))
                .getClassRequests(" dagui ", 1, 10);
        AssistantResponses.PageMeta requestMeta = assertInstanceOf(
                AssistantResponses.PageMeta.class, empty.getMeta());
        assertEquals(0, requestMeta.getTotalPages());
        assertEquals(List.of(), empty.getData());
    }

    @Test
    void controllerMapsJsonErrorAndBinaryResponses() {
        AssistantResponses.Result<List<AssistantResponses.SemesterDTO>> jsonResult =
                AssistantResponses.Result.success(200, "ok", List.of(), new AssistantResponses.SemesterMeta(0, null));
        AssistantController jsonController = new AssistantController(serviceProxy(Map.of("getSemesters", jsonResult)));
        Response json = jsonController.getSemesters();
        assertEquals(200, json.getStatus());
        assertInstanceOf(ApiResponse.class, json.getEntity());

        AssistantResponses.FilePayload file = new AssistantResponses.FilePayload(
                new byte[]{1, 2}, "students-LHP.xlsx", AssistantExcelExporter.XLSX_CONTENT_TYPE);
        AssistantResponses.Result<List<AssistantResponses.StudentDTO>> fileResult =
                AssistantResponses.Result.file(200, "ok", file);
        AssistantController fileController = new AssistantController(
                serviceProxy(Map.of("getClassStudents", fileResult)));
        Response binary = fileController.getClassStudents("LHP", 1, 10, true);
        assertEquals(AssistantExcelExporter.XLSX_CONTENT_TYPE, binary.getMediaType().toString());
        assertEquals("attachment; filename=\"students-LHP.xlsx\"",
                binary.getHeaderString("Content-Disposition"));
        assertArrayEquals(new byte[]{1, 2}, (byte[]) binary.getEntity());

        AssistantResponses.Result<List<AssistantResponses.ClassRequestDTO>> errorResult =
                AssistantResponses.Result.error(400, "bad", List.of("invalid"));
        AssistantController errorController = new AssistantController(
                serviceProxy(Map.of("getClassRequests", errorResult)));
        Response error = errorController.getClassRequests("bad", 1, 10);
        ApiResponse<?> errorBody = assertInstanceOf(ApiResponse.class, error.getEntity());
        assertFalse(errorBody.isSuccess());
        assertEquals(List.of("invalid"), errorBody.getErrors());
    }

    private static AssistantServiceImpl service(AssistantRepository repository) {
        return new AssistantServiceImpl(repository, new AssistantExcelExporter(), FIXED_CLOCK);
    }

    private static AssistantRepository repo(Map<String, Object> values) {
        return (AssistantRepository) Proxy.newProxyInstance(
                AssistantRepository.class.getClassLoader(),
                new Class<?>[]{AssistantRepository.class},
                (proxy, method, args) -> {
                    if (values.containsKey(method.getName())) {
                        return values.get(method.getName());
                    }
                    throw new AssertionError("Unexpected repository call: " + method.getName());
                });
    }

    private static AssistantService serviceProxy(Map<String, Object> values) {
        return (AssistantService) Proxy.newProxyInstance(
                AssistantService.class.getClassLoader(),
                new Class<?>[]{AssistantService.class},
                (proxy, method, args) -> {
                    if (values.containsKey(method.getName())) {
                        return values.get(method.getName());
                    }
                    throw new AssertionError("Unexpected service call: " + method.getName());
                });
    }

    private static HocKy semester(String id, LocalDate start, LocalDate end) {
        HocKy semester = new HocKy();
        semester.setMaHocKy(id);
        semester.setTenHocKy(TenHocKy.HOCKY1);
        semester.setThoiGianBD(start);
        semester.setThoiGianKT(end);
        return semester;
    }

    private static LopHocPhan courseClass(String id, int current, int maximum) {
        LopHocPhan courseClass = new LopHocPhan();
        courseClass.setMaLHP(id);
        courseClass.setSiSoHienTai(current);
        courseClass.setSiSoToiDa(maximum);
        return courseClass;
    }

    private static LichHoc schedule(String id, String room) {
        LichHoc schedule = new LichHoc();
        schedule.setMaLichHoc(id);
        schedule.setPhongHoc(room);
        return schedule;
    }
}
