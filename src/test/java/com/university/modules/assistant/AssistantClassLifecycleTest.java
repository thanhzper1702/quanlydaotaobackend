package com.university.modules.assistant;

import com.university.core.common.ApiResponse;
import com.university.core.filter.AppSecurityContext;
import com.university.core.filter.AppUserPrincipal;
import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.TroLyDaoTao;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistantClassLifecycleTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-08T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void createValidatesReferencesGeneratesFifteenSchedulesAndPersistsAtomically() {
        HocKy semester = semester(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 14));
        HocPhan course = new HocPhan();
        course.setMaHP("INT1405");
        TroLyDaoTao assistant = new TroLyDaoTao();
        assistant.setMaNV("TL-01");
        Map<String, Object> values = new HashMap<>();
        values.put("findSemesterById", Optional.of(semester));
        values.put("findCourseById", Optional.of(course));
        values.put("findAssistantById", Optional.of(assistant));
        values.put("findClassById", Optional.empty());
        RecordingRepository recording = new RecordingRepository(values);
        AssistantServiceImpl service = service(recording.repository());

        AssistantResponses.Result<AssistantResponses.CreateClassData> result = service.createClass(
                " HK-01 ", " TL-01 ",
                new AssistantRequests.CreateClassRequest(
                        " INT1405_01 ", "Lập trình", " INT1405 ", 2, 1, 3, 40));

        assertEquals(201, result.getStatusCode());
        assertEquals("INT1405_01", result.getData().getMaLHP());
        assertEquals(15, result.getData().getSoBuoiHocDaSinh());
        assertEquals("createClassWithSchedules", recording.lastWrite);
        assertEquals(15, recording.schedules.size());
        assertEquals(LocalDate.of(2026, 9, 7), recording.schedules.get(0).getNgayHoc());
        assertEquals(LocalDate.of(2026, 12, 14), recording.schedules.get(14).getNgayHoc());
        assertEquals(1, recording.schedules.stream().map(LichHoc::getPhongHoc).distinct().count());
        assertTrue(recording.schedules.stream().allMatch(schedule -> schedule.getMaLichHoc().length() <= 30));
        assertSame(assistant, recording.courseClass.getTroLyDaoTao());
        assertSame(course, recording.courseClass.getMonHoc());
    }

    @Test
    void createFallsBackToAnotherRoomAndRejectsWhenEveryRoomConflicts() {
        HocKy semester = semester(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 14));
        HocPhan course = new HocPhan();
        course.setMaHP("HP-01");
        TroLyDaoTao assistant = new TroLyDaoTao();
        assistant.setMaNV("TL-01");
        Map<String, Object> references = new HashMap<>();
        references.put("findSemesterById", Optional.of(semester));
        references.put("findCourseById", Optional.of(course));
        references.put("findAssistantById", Optional.of(assistant));
        references.put("findClassById", Optional.empty());
        references.put("conflictingRooms", Set.of("A1-102"));
        RecordingRepository fallback = new RecordingRepository(references);

        AssistantResponses.Result<AssistantResponses.CreateClassData> created = service(fallback.repository())
                .createClass("HK-01", "TL-01", new AssistantRequests.CreateClassRequest(
                        "LHP-ROOM", "Lớp phòng", "HP-01", 2, 1, 3, 40));

        assertEquals(201, created.getStatusCode());
        assertTrue(fallback.schedules.stream().allMatch(schedule -> "A1-101".equals(schedule.getPhongHoc())));

        references.put("conflictingRooms", Set.of("A1-101", "A1-102"));
        RecordingRepository unavailable = new RecordingRepository(references);
        AssistantResponses.Result<AssistantResponses.CreateClassData> rejected = service(unavailable.repository())
                .createClass("HK-01", "TL-01", new AssistantRequests.CreateClassRequest(
                        "LHP-FULL", "Lớp hết phòng", "HP-01", 2, 1, 3, 40));

        assertEquals(409, rejected.getStatusCode());
        assertNull(unavailable.lastWrite);
    }

    @Test
    void createRejectsUnauthenticatedMissingReferencesAndDuplicateClass() {
        AssistantRequests.CreateClassRequest request = new AssistantRequests.CreateClassRequest(
                "LHP-01", "Lớp 1", "HP-01", 2, 1, 3, 40);
        AssistantServiceImpl emptyService = service(new RecordingRepository(Map.of()).repository());
        assertEquals(401, emptyService.createClass("HK-01", " ", request).getStatusCode());
        assertEquals(404, emptyService.createClass("HK-01", "TL-01", request).getStatusCode());

        HocKy semester = semester(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 14));
        HocPhan course = new HocPhan();
        TroLyDaoTao assistant = new TroLyDaoTao();
        Map<String, Object> values = new HashMap<>();
        values.put("findSemesterById", Optional.of(semester));
        values.put("findCourseById", Optional.of(course));
        values.put("findAssistantById", Optional.of(assistant));
        values.put("findClassById", Optional.of(courseClass(semester)));
        assertEquals(409, service(new RecordingRepository(values).repository())
                .createClass("HK-01", "TL-01", request).getStatusCode());
    }

    @Test
    void updateUsesSimpleSaveUnlessScheduleConfigurationChanges() {
        HocKy semester = semester(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 14));
        LopHocPhan existing = courseClass(semester);
        RecordingRepository simple = new RecordingRepository(
                Map.of("findClassById", Optional.of(existing)));

        AssistantResponses.Result<Void> renamed = service(simple.repository()).updateClass(
                " LHP-01 ", new AssistantRequests.UpdateClassRequest("Tên mới", 45, null, null, null));

        assertEquals(200, renamed.getStatusCode());
        assertEquals("saveClass", simple.lastWrite);
        assertEquals("Tên mới", existing.getTenLHP());

        LopHocPhan rescheduledClass = courseClass(semester);
        RecordingRepository rescheduled = new RecordingRepository(
                Map.of("findClassById", Optional.of(rescheduledClass)));
        AssistantResponses.Result<Void> moved = service(rescheduled.repository()).updateClass(
                "LHP-01", new AssistantRequests.UpdateClassRequest(null, null, 4, 5, 2));

        assertEquals(200, moved.getStatusCode());
        assertEquals("updateClassWithSchedules", rescheduled.lastWrite);
        assertFalse(rescheduled.schedules.isEmpty());
        assertEquals(4, rescheduled.courseClass.getThu());
        assertTrue(rescheduled.schedules.stream().allMatch(
                schedule -> !schedule.getNgayHoc().isBefore(semester.getThoiGianBD())
                        && !schedule.getNgayHoc().isAfter(semester.getThoiGianKT())));
    }

    @Test
    void updateValidatesMergedCapacityAndDeleteEnforcesNoStudents() {
        HocKy semester = semester(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 14));
        LopHocPhan existing = courseClass(semester);
        existing.setSiSoHienTai(20);
        RecordingRepository invalidUpdate = new RecordingRepository(
                Map.of("findClassById", Optional.of(existing)));
        assertEquals(400, service(invalidUpdate.repository()).updateClass(
                "LHP-01", new AssistantRequests.UpdateClassRequest(null, 19, null, null, null))
                .getStatusCode());

        RecordingRepository occupied = new RecordingRepository(Map.of(
                "findClassById", Optional.of(existing), "countStudentsByClass", 1L));
        assertEquals(409, service(occupied.repository()).deleteClass("LHP-01").getStatusCode());

        RecordingRepository deletable = new RecordingRepository(Map.of(
                "findClassById", Optional.of(existing), "countStudentsByClass", 0L,
                "deleteClassWithSchedulesIfNoStudents", true));
        assertEquals(200, service(deletable.repository()).deleteClass(" LHP-01 ").getStatusCode());
        assertEquals("deleteClassWithSchedulesIfNoStudents", deletable.lastWrite);

        RecordingRepository missing = new RecordingRepository(
                Map.of("findClassById", Optional.empty()));
        assertEquals(404, service(missing.repository()).deleteClass("LHP-01").getStatusCode());
    }

    @Test
    void updatePersistsRequestedRoomForEveryClassSchedule() {
        HocKy semester = semester(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 14));
        LopHocPhan existing = courseClass(semester);
        LichHoc first = new LichHoc();
        first.setMaLichHoc("LH-01");
        first.setNgayHoc(LocalDate.of(2026, 9, 7));
        first.setPhongHoc("A1-101");
        LichHoc second = new LichHoc();
        second.setMaLichHoc("LH-02");
        second.setNgayHoc(LocalDate.of(2026, 9, 14));
        second.setPhongHoc("A1-101");
        RecordingRepository recording = new RecordingRepository(Map.of(
                "findClassById", Optional.of(existing), "findSchedulesByClass", List.of(first, second)));
        AssistantRequests.UpdateClassRequest request = new AssistantRequests.UpdateClassRequest();
        request.setPhongHoc("B2-302");

        AssistantResponses.Result<Void> result = service(recording.repository()).updateClass("LHP-01", request);

        assertEquals(200, result.getStatusCode());
        assertEquals("updateClassWithSchedules", recording.lastWrite);
        assertTrue(recording.schedules.stream().allMatch(schedule -> "B2-302".equals(schedule.getPhongHoc())));
    }

    @Test
    void updateReturnsAvailableRoomsWhenRequestedRoomConflicts() {
        HocKy semester = semester(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 14));
        LopHocPhan existing = courseClass(semester);
        LichHoc schedule = new LichHoc();
        schedule.setMaLichHoc("LH-01");
        schedule.setNgayHoc(LocalDate.of(2026, 9, 7));
        schedule.setPhongHoc("A1-101");
        RecordingRepository recording = new RecordingRepository(Map.of(
                "findClassById", Optional.of(existing), "findSchedulesByClass", List.of(schedule),
                "conflictingRooms", Set.of("B2-302"),
                "updateClassWithSchedulesException", new IllegalArgumentException("Room conflict")));
        AssistantRequests.UpdateClassRequest request = new AssistantRequests.UpdateClassRequest();
        request.setPhongHoc("B2-302");

        AssistantResponses.Result<Void> result = service(recording.repository()).updateClass("LHP-01", request);

        assertEquals(409, result.getStatusCode());
        assertTrue(result.getMessage().contains("B2-302 bị trùng lịch"));
        assertTrue(result.getErrors().contains("A1-101"));
        assertFalse(result.getErrors().contains("B2-302"));
    }

    @Test
    void updateRejectsManualLockInsteadOfSilentlyIgnoringIt() {
        HocKy semester = semester(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 14));
        LopHocPhan existing = courseClass(semester);
        RecordingRepository recording = new RecordingRepository(
                Map.of("findClassById", Optional.of(existing)));
        AssistantRequests.UpdateClassRequest request = new AssistantRequests.UpdateClassRequest();
        request.setTrangThai("DA_KHOA");

        AssistantResponses.Result<Void> result = service(recording.repository()).updateClass("LHP-01", request);

        assertEquals(409, result.getStatusCode());
        assertTrue(result.getMessage().contains("Không thể khóa lớp khi học kỳ đang diễn ra"));
        assertNull(recording.lastWrite);
    }

    @Test
    void controllerPassesAuthenticatedAssistantAndReturnsCreatedEnvelope() {
        AssistantResponses.Result<AssistantResponses.CreateClassData> created =
                AssistantResponses.Result.success(201, "created",
                        new AssistantResponses.CreateClassData("LHP-01", 15));
        Object[] captured = new Object[3];
        AssistantService service = (AssistantService) Proxy.newProxyInstance(
                AssistantService.class.getClassLoader(), new Class<?>[]{AssistantService.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("createClass")) {
                        System.arraycopy(args, 0, captured, 0, args.length);
                        return created;
                    }
                    throw new AssertionError("Unexpected service call: " + method.getName());
                });
        TestController controller = new TestController(service);
        controller.authenticate("TL-01");

        Response response = controller.createClass("HK-01", new AssistantRequests.CreateClassRequest());

        assertEquals(201, response.getStatus());
        assertEquals("TL-01", captured[1]);
        ApiResponse<?> body = assertInstanceOf(ApiResponse.class, response.getEntity());
        assertTrue(body.isSuccess());
    }

    private static AssistantServiceImpl service(AssistantRepository repository) {
        ScheduleGenerator generator = new ScheduleGenerator(
                new MockRoomProvider(List.of("A1-101", "A1-102"), bound -> 1));
        return new AssistantServiceImpl(repository, new AssistantExcelExporter(), generator, FIXED_CLOCK);
    }

    private static HocKy semester(LocalDate start, LocalDate end) {
        HocKy semester = new HocKy();
        semester.setMaHocKy("HK-01");
        semester.setThoiGianBD(start);
        semester.setThoiGianKT(end);
        return semester;
    }

    private static LopHocPhan courseClass(HocKy semester) {
        LopHocPhan courseClass = new LopHocPhan();
        courseClass.setMaLHP("LHP-01");
        courseClass.setTenLHP("Lớp 1");
        courseClass.setHocKy(semester);
        courseClass.setThu(2);
        courseClass.setTietBatDau(1);
        courseClass.setSoTiet(3);
        courseClass.setSiSoHienTai(0);
        courseClass.setSiSoToiDa(40);
        return courseClass;
    }

    private static final class RecordingRepository {
        private final Map<String, Object> values;
        private String lastWrite;
        private LopHocPhan courseClass;
        private List<LichHoc> schedules = List.of();

        private RecordingRepository(Map<String, Object> values) {
            this.values = values;
        }

        private AssistantRepository repository() {
            return (AssistantRepository) Proxy.newProxyInstance(
                    AssistantRepository.class.getClassLoader(), new Class<?>[]{AssistantRepository.class},
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if (name.equals("createClassWithSchedules")
                                || name.equals("updateClassWithSchedules")) {
                            if (values.containsKey(name + "Exception")) {
                                throw (IllegalArgumentException) values.get(name + "Exception");
                            }
                            lastWrite = name;
                            courseClass = (LopHocPhan) args[0];
                            schedules = List.copyOf((List<LichHoc>) args[1]);
                            return courseClass;
                        }
                        if (name.equals("saveClass")) {
                            lastWrite = name;
                            courseClass = (LopHocPhan) args[0];
                            return courseClass;
                        }
                        if (name.equals("deleteClassWithSchedulesIfNoStudents")) {
                            lastWrite = name;
                        }
                        if (name.equals("hasRoomConflictExcludingClass")) {
                            @SuppressWarnings("unchecked")
                            Set<String> conflictingRooms = (Set<String>) values.getOrDefault(
                                    "conflictingRooms", Set.of());
                            return conflictingRooms.contains((String) args[1]);
                        }
                        if (values.containsKey(name)) {
                            return values.get(name);
                        }
                        if (name.equals("findSemesterById") || name.equals("findCourseById")
                                || name.equals("findAssistantById") || name.equals("findClassById")) {
                            return Optional.empty();
                        }
                        throw new AssertionError("Unexpected repository call: " + name);
                    });
        }
    }

    private static final class TestController extends AssistantController {
        private TestController(AssistantService service) {
            super(service);
        }

        private void authenticate(String userCode) {
            securityContext = new AppSecurityContext(
                    new AppUserPrincipal("012345678901", userCode, "TROLY", "tl@example.com"), false);
        }
    }
}
