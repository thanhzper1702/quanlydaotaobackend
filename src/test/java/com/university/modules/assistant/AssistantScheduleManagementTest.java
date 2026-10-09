package com.university.modules.assistant;

import com.university.core.common.ApiResponse;
import com.university.core.model.HocKy;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistantScheduleManagementTest {
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-08T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void updateScheduleValidatesOwnershipSemesterAndRoomConflictBeforeWriting() {
        LopHocPhan owner = courseClass("LHP-01");
        LichHoc schedule = schedule("LH-01", owner);
        RecordingRepository success = new RecordingRepository(Map.of(
                "findClassById", Optional.of(owner), "findScheduleById", Optional.of(schedule),
                "hasRoomConflict", false));

        AssistantResponses.Result<Void> updated = service(success).updateSchedule(" LHP-01 ",
                new AssistantRequests.UpdateScheduleRequest(" LH-01 ", LocalDate.of(2026, 9, 8), " P102 "));

        assertEquals(200, updated.getStatusCode());
        assertEquals("saveSchedule", success.lastWrite);
        assertEquals("P102", schedule.getPhongHoc());
        assertEquals(List.of(LocalDate.of(2026, 9, 8), "P102", 3, 2, "LH-01"), success.conflictArguments);

        LichHoc foreign = schedule("LH-02", courseClass("LHP-02"));
        RecordingRepository wrongOwner = new RecordingRepository(Map.of(
                "findClassById", Optional.of(owner), "findScheduleById", Optional.of(foreign)));
        assertEquals(404, service(wrongOwner).updateSchedule("LHP-01",
                new AssistantRequests.UpdateScheduleRequest("LH-02", LocalDate.of(2026, 9, 8), "P102")).getStatusCode());
        assertNull(wrongOwner.lastWrite);

        RecordingRepository outsideSemester = new RecordingRepository(Map.of(
                "findClassById", Optional.of(owner), "findScheduleById", Optional.of(schedule)));
        assertEquals(400, service(outsideSemester).updateSchedule("LHP-01",
                new AssistantRequests.UpdateScheduleRequest("LH-01", LocalDate.of(2027, 1, 1), "P102")).getStatusCode());

        RecordingRepository conflict = new RecordingRepository(Map.of(
                "findClassById", Optional.of(owner), "findScheduleById", Optional.of(schedule),
                "hasRoomConflict", true));
        assertEquals(409, service(conflict).updateSchedule("LHP-01",
                new AssistantRequests.UpdateScheduleRequest("LH-01", LocalDate.of(2026, 9, 8), "P102")).getStatusCode());
        assertNull(conflict.lastWrite);
    }

    @Test
    void deleteScheduleUsesScheduleIdAndControllerReturnsEnvelope() {
        LopHocPhan firstClass = courseClass("LHP-01");
        LichHoc firstSchedule = schedule("LH-01", firstClass);
        RecordingRepository recording = new RecordingRepository(Map.of("findScheduleById", Optional.of(firstSchedule)));

        AssistantResponses.Result<Void> deleted = service(recording).deleteSchedule(" LH-01 ");
        assertEquals(200, deleted.getStatusCode());
        assertEquals("deleteScheduleById", recording.lastWrite);
        assertEquals("LH-01", recording.deletedScheduleId);

        Response updateResponse = new AssistantController(service(new RecordingRepository(Map.of(
                "findClassById", Optional.of(firstClass), "findScheduleById", Optional.of(firstSchedule),
                "hasRoomConflict", false)))).updateSchedule("LHP-01",
                new AssistantRequests.UpdateScheduleRequest("LH-01", LocalDate.of(2026, 9, 8), "P102"));
        assertEquals(200, updateResponse.getStatus());
        assertTrue(updateResponse.getEntity() instanceof ApiResponse<?>);
    }

    private static AssistantServiceImpl service(RecordingRepository recording) {
        return new AssistantServiceImpl(recording.repository(), new AssistantExcelExporter(), FIXED_CLOCK);
    }

    private static LopHocPhan courseClass(String id) {
        HocKy semester = new HocKy();
        semester.setMaHocKy("HK-01");
        semester.setThoiGianBD(LocalDate.of(2026, 9, 1));
        semester.setThoiGianKT(LocalDate.of(2026, 12, 14));
        LopHocPhan courseClass = new LopHocPhan();
        courseClass.setMaLHP(id);
        courseClass.setHocKy(semester);
        courseClass.setTietBatDau(3);
        courseClass.setSoTiet(2);
        return courseClass;
    }

    private static LichHoc schedule(String id, LopHocPhan courseClass) {
        LichHoc schedule = new LichHoc();
        schedule.setMaLichHoc(id);
        schedule.setLopHocPhan(courseClass);
        schedule.setNgayHoc(LocalDate.of(2026, 9, 7));
        schedule.setPhongHoc("P101");
        return schedule;
    }

    private static final class RecordingRepository {
        private final Map<String, Object> values;
        private String lastWrite;
        private String deletedScheduleId;
        private List<Object> conflictArguments;

        private RecordingRepository(Map<String, Object> values) { this.values = new HashMap<>(values); }

        private AssistantRepository repository() {
            return (AssistantRepository) Proxy.newProxyInstance(AssistantRepository.class.getClassLoader(),
                    new Class<?>[]{AssistantRepository.class}, (proxy, method, args) -> {
                        String name = method.getName();
                        if (name.equals("hasRoomConflict")) conflictArguments = List.of(args);
                        if (name.equals("saveSchedule")) { lastWrite = name; return args[0]; }
                        if (name.equals("deleteScheduleById")) { lastWrite = name; deletedScheduleId = (String) args[0]; return null; }
                        if (values.containsKey(name)) return values.get(name);
                        if (name.equals("findClassById") || name.equals("findScheduleById")) return Optional.empty();
                        throw new AssertionError("Unexpected repository call: " + name);
                    });
        }
    }
}
