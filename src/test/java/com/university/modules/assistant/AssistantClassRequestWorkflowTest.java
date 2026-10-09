package com.university.modules.assistant;

import com.university.core.common.ApiResponse;
import com.university.core.model.YeuCauMoLop;
import com.university.core.model.enums.TrangThaiYeuCau;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AssistantClassRequestWorkflowTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-08T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void acceptsAllFourLockedTransitionsAndPersistsOnlyTheNewStatus() {
        assertSuccessfulTransition(TrangThaiYeuCau.DAGUI, "DATIEPNHAN",
                TrangThaiYeuCau.DATIEPNHAN, "Đã tiếp nhận yêu cầu mở lớp");
        assertSuccessfulTransition(TrangThaiYeuCau.DATIEPNHAN, "DADUYET",
                TrangThaiYeuCau.DADUYET, "Đã duyệt yêu cầu mở lớp");
        assertSuccessfulTransition(TrangThaiYeuCau.DAGUI, "DATUCHOI",
                TrangThaiYeuCau.DATUCHOI, "Đã từ chối yêu cầu mở lớp");
        assertSuccessfulTransition(TrangThaiYeuCau.DATIEPNHAN, "DATUCHOI",
                TrangThaiYeuCau.DATUCHOI, "Đã từ chối yêu cầu mở lớp");
    }

    @Test
    void rejectsInvalidTargetAndMissingRequestWithoutWriting() {
        RecordingRepository invalidTarget = new RecordingRepository(request(TrangThaiYeuCau.DAGUI));
        AssistantResponses.Result<Void> invalid = service(invalidTarget.repository()).updateClassRequestStatus(
                "YC-01", new AssistantRequests.UpdateClassRequestStatusRequest("CHODUYET"));
        assertEquals(400, invalid.getStatusCode());
        assertNull(invalidTarget.saved);

        RecordingRepository missing = new RecordingRepository(null);
        AssistantResponses.Result<Void> notFound = service(missing.repository()).updateClassRequestStatus(
                "YC-404", new AssistantRequests.UpdateClassRequestStatusRequest("DATIEPNHAN"));
        assertEquals(404, notFound.getStatusCode());
        assertNull(missing.saved);
    }

    @Test
    void rejectsSkipRepeatAndTerminalTransitionsWithoutMutatingOrWriting() {
        assertRejectedTransition(TrangThaiYeuCau.DAGUI, "DADUYET");
        assertRejectedTransition(TrangThaiYeuCau.DATIEPNHAN, "DATIEPNHAN");
        for (String target : new String[]{"DATIEPNHAN", "DADUYET", "DATUCHOI"}) {
            assertRejectedTransition(TrangThaiYeuCau.DADUYET, target);
            assertRejectedTransition(TrangThaiYeuCau.DATUCHOI, target);
        }
    }

    @Test
    void controllerReturnsSingleApiResponseEnvelope() {
        AssistantResponses.Result<Void> result = AssistantResponses.Result.success(200, "ok");
        AssistantService service = (AssistantService) Proxy.newProxyInstance(
                AssistantService.class.getClassLoader(), new Class<?>[]{AssistantService.class},
                (proxy, method, args) -> method.getName().equals("updateClassRequestStatus")
                        ? result : throwUnexpected(method.getName()));

        Response response = new AssistantController(service).updateClassRequestStatus(
                "YC-01", new AssistantRequests.UpdateClassRequestStatusRequest("DATIEPNHAN"));
        assertEquals(200, response.getStatus());
        assertInstanceOf(ApiResponse.class, response.getEntity());
    }

    private static Object throwUnexpected(String methodName) {
        throw new AssertionError("Unexpected service call: " + methodName);
    }

    private static void assertSuccessfulTransition(TrangThaiYeuCau current, String target,
                                                   TrangThaiYeuCau expected, String message) {
        YeuCauMoLop classRequest = request(current);
        RecordingRepository recording = new RecordingRepository(classRequest);

        AssistantResponses.Result<Void> result = service(recording.repository()).updateClassRequestStatus(
                " YC-01 ", new AssistantRequests.UpdateClassRequestStatusRequest(target));

        assertEquals(200, result.getStatusCode());
        assertEquals(message, result.getMessage());
        assertEquals(expected, classRequest.getTrangThai());
        assertEquals(classRequest, recording.saved);
        assertEquals(LocalDateTime.ofInstant(FIXED_CLOCK.instant(), ZoneOffset.UTC), classRequest.getUpdatedAt());
    }

    private static void assertRejectedTransition(TrangThaiYeuCau current, String target) {
        YeuCauMoLop classRequest = request(current);
        RecordingRepository recording = new RecordingRepository(classRequest);

        AssistantResponses.Result<Void> result = service(recording.repository()).updateClassRequestStatus(
                "YC-01", new AssistantRequests.UpdateClassRequestStatusRequest(target));

        assertEquals(409, result.getStatusCode());
        assertEquals(current, classRequest.getTrangThai());
        assertNull(recording.saved);
    }

    private static AssistantServiceImpl service(AssistantRepository repository) {
        return new AssistantServiceImpl(repository, new AssistantExcelExporter(), FIXED_CLOCK);
    }

    private static YeuCauMoLop request(TrangThaiYeuCau status) {
        YeuCauMoLop classRequest = new YeuCauMoLop();
        classRequest.setMaYeuCau("YC-01");
        classRequest.setTrangThai(status);
        return classRequest;
    }

    private static final class RecordingRepository {
        private final YeuCauMoLop classRequest;
        private YeuCauMoLop saved;

        private RecordingRepository(YeuCauMoLop classRequest) {
            this.classRequest = classRequest;
        }

        private AssistantRepository repository() {
            return (AssistantRepository) Proxy.newProxyInstance(
                    AssistantRepository.class.getClassLoader(), new Class<?>[]{AssistantRepository.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "findClassRequestById" -> Optional.ofNullable(classRequest);
                        case "saveClassRequest" -> {
                            saved = (YeuCauMoLop) args[0];
                            yield saved;
                        }
                        default -> throw new AssertionError("Unexpected repository call: " + method.getName());
                    });
        }
    }
}
