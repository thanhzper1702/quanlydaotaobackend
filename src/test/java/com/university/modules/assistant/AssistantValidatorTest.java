package com.university.modules.assistant;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssistantValidatorTest {

    @Test
    void acceptsScheduleBoundariesAtPeriodsOneAndTwelve() {
        AssistantRequests.CreateClassRequest fullDay = new AssistantRequests.CreateClassRequest(
                "INT1405_01", "Kỹ nghệ phần mềm", "INT1405", 2, 1, 12, 60);
        AssistantRequests.CreateClassRequest lastPeriod = new AssistantRequests.CreateClassRequest(
                "INT1405_02", "Kỹ nghệ phần mềm", "INT1405", 8, 12, 1, 60);

        assertTrue(AssistantValidator.validateCreateClass(fullDay).isEmpty());
        assertTrue(AssistantValidator.validateCreateClass(lastPeriod).isEmpty());
    }

    @Test
    void rejectsInvalidPaginationScheduleAndCapacity() {
        assertEquals(2, AssistantValidator.validatePagination(0, 101).size());

        AssistantRequests.CreateClassRequest invalid = new AssistantRequests.CreateClassRequest(
                " ", "", null, 9, 11, 3, 0);
        List<String> errors = AssistantValidator.validateCreateClass(invalid);

        assertTrue(errors.contains("maLHP là bắt buộc"));
        assertTrue(errors.contains("thu phải nằm trong khoảng 2..8"));
        assertTrue(errors.contains("Tiết kết thúc không được vượt quá tiết 12"));
        assertTrue(errors.contains("siSoToiDa phải lớn hơn 0"));
    }

    @Test
    void validatesMergedUpdateInsteadOfOnlySuppliedFields() {
        AssistantRequests.UpdateClassRequest request = new AssistantRequests.UpdateClassRequest(
                null, 20, null, 11, null);

        List<String> errors = AssistantValidator.validateUpdateClass(request, 25, 2, 1, 3);

        assertTrue(errors.contains("siSoToiDa không được nhỏ hơn siSoHienTai"));
        assertTrue(errors.contains("Tiết kết thúc không được vượt quá tiết 12"));
    }

    @Test
    void validatesRegistrationTimesAndRemovesDuplicateClassIdsInOrder() {
        AssistantRequests.OpenRegistrationPeriodRequest request =
                new AssistantRequests.OpenRegistrationPeriodRequest(
                        List.of(" INT1405_01 ", "INT1405_01", "INT1405_02"),
                        "2026-01-01", "2026-01-02T08:30:00+07:00");

        assertTrue(AssistantValidator.validateOpenRegistrationPeriod(request).isEmpty());
        assertEquals(List.of("INT1405_01", "INT1405_02"),
                AssistantValidator.distinctClassIds(request.getDsMaLHP()));
        assertEquals(7 * 60 * 60,
                AssistantValidator.parseRegistrationTime("2026-01-01").getOffset().getTotalSeconds());
    }

    @Test
    void restrictsRequestStatusAndSemesterDate() {
        assertTrue(AssistantValidator.validateClassRequestFilter("daduyet").isEmpty());
        assertFalse(AssistantValidator.validateClassRequestFilter("CHODUYET").isEmpty());
        assertFalse(AssistantValidator.validateClassRequestFilter(" ").isEmpty());

        AssistantRequests.UpdateClassRequestStatusRequest invalid =
                new AssistantRequests.UpdateClassRequestStatusRequest("DAGUI");
        assertFalse(AssistantValidator.validateClassRequestStatus(invalid).isEmpty());
        assertThrows(IllegalArgumentException.class,
                () -> ClassRequestStateMachine.parseTarget(null));

        assertTrue(AssistantValidator.validateDateWithinSemester(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)).isEmpty());
        assertFalse(AssistantValidator.validateDateWithinSemester(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)).isEmpty());
    }
}
