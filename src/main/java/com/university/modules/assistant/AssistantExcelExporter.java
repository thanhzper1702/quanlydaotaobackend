package com.university.modules.assistant;

import com.university.core.util.ExcelUtil;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Creates the XLSX response for API 32 without pagination. */
public final class AssistantExcelExporter {

    public static final String XLSX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final List<String> HEADERS = List.of(
            "STT", "Mã sinh viên", "Họ tên", "Ảnh thẻ/URL", "Ngày đăng ký", "Trạng thái"
    );
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AssistantResponses.FilePayload exportStudents(
            String classId, List<AssistantResponses.StudentDTO> students) {
        List<AssistantResponses.StudentDTO> safeStudents = students == null ? List.of() : students;
        try {
            byte[] content = ExcelUtil.createWorkbook(
                    "Danh sach sinh vien", HEADERS, toRows(safeStudents));
            return new AssistantResponses.FilePayload(
                    content, buildFileName(classId), XLSX_CONTENT_TYPE);
        } catch (IOException exception) {
            throw new IllegalStateException("Không thể tạo file Excel danh sách sinh viên", exception);
        }
    }

    public static String buildFileName(String classId) {
        String safeId = classId == null ? "unknown" : classId.trim()
                .replaceAll("[^A-Za-z0-9._-]", "_")
                .replaceAll("_+", "_");
        safeId = safeId.replaceAll("^\\.+", "");
        if (safeId.isBlank()) {
            safeId = "unknown";
        }
        if (safeId.length() > 50) {
            safeId = safeId.substring(0, 50);
        }
        return "students-" + safeId + ".xlsx";
    }

    private static List<List<String>> toRows(List<AssistantResponses.StudentDTO> students) {
        List<List<String>> rows = new ArrayList<>(students.size());
        for (int index = 0; index < students.size(); index++) {
            AssistantResponses.StudentDTO student = students.get(index);
            rows.add(List.of(
                    Integer.toString(index + 1),
                    valueOf(student == null ? null : student.getMaSV()),
                    valueOf(student == null ? null : student.getHoTen()),
                    valueOf(student == null ? null : student.getAnhThe()),
                    student == null || student.getNgayDangKy() == null
                            ? "" : DATE_TIME_FORMATTER.format(student.getNgayDangKy()),
                    valueOf(student == null ? null : student.getTrangThai())));
        }
        return rows;
    }

    private static String valueOf(String value) {
        return value == null ? "" : value;
    }
}
