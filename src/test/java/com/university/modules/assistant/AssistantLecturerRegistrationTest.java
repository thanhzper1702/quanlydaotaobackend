package com.university.modules.assistant;

import com.university.core.common.ApiResponse;
import com.university.core.model.GiangVien;
import com.university.core.model.HocKy;
import com.university.core.model.LopHocPhan;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistantLecturerRegistrationTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-08T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void assignLecturerChecksConflictExcludesCurrentClassAndWritesOnlyOnSuccess() {
        LopHocPhan courseClass = courseClass("LHP-01", true);
        GiangVien lecturer = lecturer("GV-01");
        RecordingRepository success = new RecordingRepository(Map.of(
                "findClassById", Optional.of(courseClass),
                "findLecturerById", Optional.of(lecturer),
                "hasLecturerConflict", false,
                "assignLecturer", true));

        AssistantResponses.Result<Void> result = service(success).assignLecturer(
                " LHP-01 ", new AssistantRequests.AssignLecturerRequest(" GV-01 "));

        assertEquals(200, result.getStatusCode());
        assertEquals(List.of("GV-01", "HK-01", 2, 3, 2, "LHP-01"), success.conflictArguments);
        assertEquals(List.of("LHP-01", "GV-01"), success.assignmentArguments);

        RecordingRepository conflict = new RecordingRepository(Map.of(
                "findClassById", Optional.of(courseClass),
                "findLecturerById", Optional.of(lecturer),
                "hasLecturerConflict", true));
        assertEquals(409, service(conflict).assignLecturer(
                "LHP-01", new AssistantRequests.AssignLecturerRequest("GV-01")).getStatusCode());
        assertNull(conflict.assignmentArguments);
    }

    @Test
    void assignLecturerValidatesClassAndLecturerBeforeWriting() {
        RecordingRepository missingClass = new RecordingRepository(Map.of());
        assertEquals(404, service(missingClass).assignLecturer(
                "LHP-X", new AssistantRequests.AssignLecturerRequest("GV-X")).getStatusCode());

        RecordingRepository missingLecturer = new RecordingRepository(Map.of(
                "findClassById", Optional.of(courseClass("LHP-01", true))));
        assertEquals(404, service(missingLecturer).assignLecturer(
                "LHP-01", new AssistantRequests.AssignLecturerRequest("GV-X")).getStatusCode());
        assertNull(missingLecturer.assignmentArguments);
    }

    @Test
    void openRegistrationDeduplicatesThenWritesAllClassesOnce() {
        Map<String, Optional<LopHocPhan>> classes = Map.of(
                "LHP-01", Optional.of(courseClass("LHP-01", true)),
                "LHP-02", Optional.of(courseClass("LHP-02", true)));
        RecordingRepository recording = new RecordingRepository(new HashMap<>(), classes);
        AssistantRequests.OpenRegistrationPeriodRequest request =
                new AssistantRequests.OpenRegistrationPeriodRequest(
                        List.of(" LHP-01 ", "LHP-01", "LHP-02"),
                        "2026-10-10", "2026-10-20T23:59:59+07:00");

        AssistantResponses.Result<Void> result = service(recording).openRegistrationPeriod(request);

        assertEquals(200, result.getStatusCode());
        assertEquals(List.of("LHP-01", "LHP-02"), recording.registrationClassIds);
        assertEquals(OffsetDateTime.parse("2026-10-10T00:00:00+07:00"), recording.registrationStart);
        assertEquals(OffsetDateTime.parse("2026-10-20T23:59:59+07:00"), recording.registrationEnd);
    }

    @Test
    void openRegistrationValidatesEveryClassBeforeAnyWrite() {
        LopHocPhan noLecturer = courseClass("LHP-NO-GV", false);
        LopHocPhan configured = courseClass("LHP-OPEN", true);
        configured.setThoiGianBDDK(OffsetDateTime.parse("2026-09-01T00:00:00+07:00"));
        RecordingRepository recording = new RecordingRepository(new HashMap<>(), Map.of(
                "LHP-NO-GV", Optional.of(noLecturer),
                "LHP-OPEN", Optional.of(configured),
                "LHP-MISSING", Optional.empty()));
        AssistantRequests.OpenRegistrationPeriodRequest request =
                new AssistantRequests.OpenRegistrationPeriodRequest(
                        List.of("LHP-NO-GV", "LHP-OPEN", "LHP-MISSING"),
                        "2026-10-10", "2026-10-20");

        AssistantResponses.Result<Void> result = service(recording).openRegistrationPeriod(request);

        assertEquals(404, result.getStatusCode());
        assertFalse(result.getErrors().isEmpty());
        assertNull(recording.registrationClassIds);
    }

    @Test
    void controllerReturnsApiEnvelopeForBothEndpoints() {
        LopHocPhan courseClass = courseClass("LHP-01", true);
        RecordingRepository recording = new RecordingRepository(Map.of(
                "findClassById", Optional.of(courseClass),
                "findLecturerById", Optional.of(lecturer("GV-01")),
                "hasLecturerConflict", false,
                "assignLecturer", true));
        AssistantController controller = new AssistantController(service(recording));

        Response response = controller.assignLecturer(
                "LHP-01", new AssistantRequests.AssignLecturerRequest("GV-01"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof ApiResponse<?>);

        RecordingRepository registration = new RecordingRepository(new HashMap<>(), Map.of(
                "LHP-01", Optional.of(courseClass("LHP-01", true))));
        Response registrationResponse = new AssistantController(service(registration))
                .openRegistrationPeriod(new AssistantRequests.OpenRegistrationPeriodRequest(
                        List.of("LHP-01"), "2026-10-10", "2026-10-20"));
        assertEquals(200, registrationResponse.getStatus());
        assertTrue(registrationResponse.getEntity() instanceof ApiResponse<?>);
    }

    private static AssistantServiceImpl service(RecordingRepository recording) {
        return new AssistantServiceImpl(
                recording.repository(), new AssistantExcelExporter(), FIXED_CLOCK);
    }

    private static LopHocPhan courseClass(String id, boolean withLecturer) {
        HocKy semester = new HocKy();
        semester.setMaHocKy("HK-01");
        LopHocPhan courseClass = new LopHocPhan();
        courseClass.setMaLHP(id);
        courseClass.setHocKy(semester);
        courseClass.setThu(2);
        courseClass.setTietBatDau(3);
        courseClass.setSoTiet(2);
        if (withLecturer) {
            courseClass.setGiangVien(lecturer("GV-ASSIGNED"));
        }
        return courseClass;
    }

    private static GiangVien lecturer(String id) {
        GiangVien lecturer = new GiangVien();
        lecturer.setMaGV(id);
        return lecturer;
    }

    private static final class RecordingRepository {
        private final Map<String, Object> values;
        private final Map<String, Optional<LopHocPhan>> classes;
        private List<Object> conflictArguments;
        private List<String> assignmentArguments;
        private List<String> registrationClassIds;
        private OffsetDateTime registrationStart;
        private OffsetDateTime registrationEnd;

        private RecordingRepository(Map<String, Object> values) {
            this(values, Map.of());
        }

        private RecordingRepository(
                Map<String, Object> values, Map<String, Optional<LopHocPhan>> classes) {
            this.values = values;
            this.classes = classes;
        }

        private AssistantRepository repository() {
            return (AssistantRepository) Proxy.newProxyInstance(
                    AssistantRepository.class.getClassLoader(), new Class<?>[]{AssistantRepository.class},
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if (name.equals("findClassById") && !classes.isEmpty()) {
                            return classes.getOrDefault((String) args[0], Optional.empty());
                        }
                        if (name.equals("hasLecturerConflict")) {
                            conflictArguments = List.of(args);
                        }
                        if (name.equals("assignLecturer")) {
                            assignmentArguments = List.of((String) args[0], (String) args[1]);
                        }
                        if (name.equals("updateRegistrationPeriods")) {
                            registrationClassIds = List.copyOf((List<String>) args[0]);
                            registrationStart = (OffsetDateTime) args[1];
                            registrationEnd = (OffsetDateTime) args[2];
                            return null;
                        }
                        if (values.containsKey(name)) {
                            return values.get(name);
                        }
                        if (name.equals("findClassById") || name.equals("findLecturerById")) {
                            return Optional.empty();
                        }
                        throw new AssertionError("Unexpected repository call: " + name);
                    });
        }
    }
}
