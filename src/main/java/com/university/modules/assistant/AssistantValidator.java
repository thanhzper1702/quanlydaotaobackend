package com.university.modules.assistant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Shared, side-effect-free validation rules for assistant endpoints. */
public final class AssistantValidator {

    public static final int MAX_PAGE_SIZE = 100;
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final Set<String> REQUEST_STATUSES =
            Set.of("DAGUI", "DATIEPNHAN", "DADUYET", "DATUCHOI");
    private static final Set<String> REQUEST_STATUS_TARGETS =
            Set.of("DATIEPNHAN", "DADUYET", "DATUCHOI");

    private AssistantValidator() {
    }

    public static List<String> validateRequiredId(String fieldName, String value) {
        List<String> errors = new ArrayList<>();
        requireText(errors, fieldName, value);
        return errors;
    }

    public static List<String> validatePagination(int page, int limit) {
        List<String> errors = new ArrayList<>();
        if (page < 1) {
            errors.add("page phải lớn hơn hoặc bằng 1");
        }
        if (limit < 1 || limit > MAX_PAGE_SIZE) {
            errors.add("limit phải nằm trong khoảng 1.." + MAX_PAGE_SIZE);
        }
        return errors;
    }

    public static List<String> validateCreateClass(AssistantRequests.CreateClassRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body không được để trống");
            return errors;
        }

        requireText(errors, "maLHP", request.getMaLHP());
        requireText(errors, "tenLHP", request.getTenLHP());
        requireText(errors, "maHP", request.getMaHP());
        validateScheduleFields(errors, request.getThu(), request.getTietBD(), request.getSoTiet(), true);
        validateCapacity(errors, request.getSiSoToiDa(), 0, true);
        return errors;
    }

    public static List<String> validateUpdateClass(
            AssistantRequests.UpdateClassRequest request,
            int currentEnrollment,
            Integer currentThu,
            Integer currentStartPeriod,
            Integer currentDuration) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body không được để trống");
            return errors;
        }

        if (request.getTenLHP() != null && request.getTenLHP().isBlank()) {
            errors.add("tenLHP không được để trống");
        }
        if (request.getSiSoToiDa() != null) {
            validateCapacity(errors, request.getSiSoToiDa(), currentEnrollment, false);
        }

        Integer mergedThu = request.getThu() != null ? request.getThu() : currentThu;
        Integer mergedStart = request.getTietBD() != null ? request.getTietBD() : currentStartPeriod;
        Integer mergedDuration = request.getSoTiet() != null ? request.getSoTiet() : currentDuration;
        validateScheduleFields(errors, mergedThu, mergedStart, mergedDuration, true);
        return errors;
    }

    public static List<String> validateAssignLecturer(AssistantRequests.AssignLecturerRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body không được để trống");
            return errors;
        }
        requireText(errors, "maGV", request.getMaGV());
        return errors;
    }

    public static List<String> validateOpenRegistrationPeriod(
            AssistantRequests.OpenRegistrationPeriodRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body không được để trống");
            return errors;
        }

        List<String> classIds = request.getDsMaLHP();
        if (classIds == null || classIds.isEmpty()) {
            errors.add("dsMaLHP phải có ít nhất một mã lớp học phần");
        } else {
            for (int index = 0; index < classIds.size(); index++) {
                if (isBlank(classIds.get(index))) {
                    errors.add("dsMaLHP[" + index + "] không được để trống");
                }
            }
        }

        OffsetDateTime start = parseTime(request.getThoiGianBD(), "thoiGianBD", errors);
        OffsetDateTime end = parseTime(request.getThoiGianKT(), "thoiGianKT", errors);
        if (start != null && end != null && !start.isBefore(end)) {
            errors.add("thoiGianBD phải nhỏ hơn thoiGianKT");
        }
        return errors;
    }

    public static List<String> validateUpdateSchedule(AssistantRequests.UpdateScheduleRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body không được để trống");
            return errors;
        }
        requireText(errors, "maLichHoc", request.getMaLichHoc());
        if (request.getNgayHoc() == null) {
            errors.add("ngayHoc là bắt buộc");
        }
        requireText(errors, "phongHoc", request.getPhongHoc());
        return errors;
    }

    public static List<String> validateClassRequestStatus(
            AssistantRequests.UpdateClassRequestStatusRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body không được để trống");
            return errors;
        }
        String status = normalizeStatus(request.getTrangThai());
        if (status == null) {
            errors.add("trangThai là bắt buộc");
        } else if (!REQUEST_STATUS_TARGETS.contains(status)) {
            errors.add("trangThai chỉ nhận DATIEPNHAN, DADUYET hoặc DATUCHOI");
        }
        return errors;
    }

    public static List<String> validateClassRequestFilter(String status) {
        List<String> errors = new ArrayList<>();
        if (status != null) {
            String normalized = normalizeStatus(status);
            if (normalized == null || !REQUEST_STATUSES.contains(normalized)) {
                errors.add("trangThai chỉ nhận DAGUI, DATIEPNHAN, DADUYET hoặc DATUCHOI");
            }
        }
        return errors;
    }

    public static List<String> validateDateWithinSemester(
            LocalDate date, LocalDate semesterStart, LocalDate semesterEnd) {
        List<String> errors = new ArrayList<>();
        if (date == null) {
            errors.add("ngayHoc là bắt buộc");
        } else if (semesterStart == null || semesterEnd == null
                || date.isBefore(semesterStart) || date.isAfter(semesterEnd)) {
            errors.add("ngayHoc phải nằm trong khoảng thời gian của học kỳ");
        }
        return errors;
    }

    public static List<String> distinctClassIds(List<String> classIds) {
        if (classIds == null) {
            return List.of();
        }
        LinkedHashSet<String> distinct = new LinkedHashSet<>();
        for (String classId : classIds) {
            if (!isBlank(classId)) {
                distinct.add(classId.trim());
            }
        }
        return List.copyOf(distinct);
    }

    public static OffsetDateTime parseRegistrationTime(String value) {
        List<String> errors = new ArrayList<>();
        OffsetDateTime parsed = parseTime(value, "time", errors);
        if (parsed == null) {
            throw new IllegalArgumentException(errors.isEmpty() ? "Thời gian không hợp lệ" : errors.get(0));
        }
        return parsed;
    }

    public static String normalizeStatus(String status) {
        return isBlank(status) ? null : status.trim().toUpperCase(Locale.ROOT);
    }

    private static void validateScheduleFields(
            List<String> errors, Integer day, Integer start, Integer duration, boolean required) {
        if (day == null) {
            if (required) errors.add("thu là bắt buộc");
        } else if (day < 2 || day > 8) {
            errors.add("thu phải nằm trong khoảng 2..8");
        }

        if (start == null) {
            if (required) errors.add("tietBD là bắt buộc");
        } else if (start < 1 || start > 12) {
            errors.add("tietBD phải nằm trong khoảng 1..12");
        }

        if (duration == null) {
            if (required) errors.add("soTiet là bắt buộc");
        } else if (duration <= 0) {
            errors.add("soTiet phải lớn hơn 0");
        }

        if (start != null && duration != null && start >= 1 && duration > 0
                && start + duration - 1 > 12) {
            errors.add("Tiết kết thúc không được vượt quá tiết 12");
        }
    }

    private static void validateCapacity(
            List<String> errors, Integer maximum, int currentEnrollment, boolean required) {
        if (maximum == null) {
            if (required) errors.add("siSoToiDa là bắt buộc");
        } else if (maximum <= 0) {
            errors.add("siSoToiDa phải lớn hơn 0");
        } else if (maximum < currentEnrollment) {
            errors.add("siSoToiDa không được nhỏ hơn siSoHienTai");
        }
    }

    private static OffsetDateTime parseTime(String value, String fieldName, List<String> errors) {
        if (isBlank(value)) {
            errors.add(fieldName + " là bắt buộc");
            return null;
        }
        String normalized = value.trim();
        try {
            return OffsetDateTime.parse(normalized);
        } catch (DateTimeParseException ignored) {
            // Accept contract examples without an explicit offset in the business time zone.
        }
        try {
            return LocalDateTime.parse(normalized).atZone(BUSINESS_ZONE).toOffsetDateTime();
        } catch (DateTimeParseException ignored) {
            // Accept a date-only value as start of day for backward-compatible contract examples.
        }
        try {
            return LocalDate.parse(normalized).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();
        } catch (DateTimeParseException ignored) {
            errors.add(fieldName + " không đúng định dạng ISO date/date-time");
            return null;
        }
    }

    private static void requireText(List<String> errors, String fieldName, String value) {
        if (isBlank(value)) {
            errors.add(fieldName + " là bắt buộc");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
