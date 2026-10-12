package com.university.modules.assistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.core.common.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistantDtoContractTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void deserializesAllAssistantRequestContracts() throws Exception {
        AssistantRequests.CreateClassRequest create = objectMapper.readValue("""
                {"maLHP":"INT1405_01","tenLHP":"Kỹ nghệ phần mềm - Lớp 01",
                 "maHP":"INT1405","thu":2,"tietBD":1,"soTiet":3,"siSoToiDa":60}
                """, AssistantRequests.CreateClassRequest.class);
        assertEquals("INT1405_01", create.getMaLHP());
        assertEquals(1, create.getTietBD());

        AssistantRequests.UpdateClassRequest update = objectMapper.readValue("""
                {"tenLHP":"Lớp 01","siSoToiDa":60,"thu":2,"tietBD":1,"soTiet":3,"phongHoc":"B2-302"}
                """, AssistantRequests.UpdateClassRequest.class);
        assertEquals(60, update.getSiSoToiDa());
        assertEquals("B2-302", update.getPhongHoc());

        AssistantRequests.AssignLecturerRequest assign = objectMapper.readValue(
                "{\"maGV\":\"GV012\"}", AssistantRequests.AssignLecturerRequest.class);
        assertEquals("GV012", assign.getMaGV());

        AssistantRequests.OpenRegistrationPeriodRequest registration = objectMapper.readValue("""
                {"dsMaLHP":["INT1405_01","INT1408_02"],
                 "thoiGianBD":"2025-09-01","thoiGianKT":"2026-01-15"}
                """, AssistantRequests.OpenRegistrationPeriodRequest.class);
        assertEquals(2, registration.getDsMaLHP().size());
        assertEquals("2025-09-01", registration.getThoiGianBD());

        AssistantRequests.UpdateScheduleRequest schedule = objectMapper.readValue("""
                {"maLichHoc":"LH_20250915_T13","ngayHoc":"2025-09-15","phongHoc":"A2-301"}
                """, AssistantRequests.UpdateScheduleRequest.class);
        assertEquals(LocalDate.of(2025, 9, 15), schedule.getNgayHoc());

        AssistantRequests.UpdateClassRequestStatusRequest status = objectMapper.readValue(
                "{\"trangThai\":\"DADUYET\"}",
                AssistantRequests.UpdateClassRequestStatusRequest.class);
        assertEquals("DADUYET", status.getTrangThai());
    }

    @Test
    void usesTietBDOnlyAtRequestBoundary() throws Exception {
        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(
                new AssistantRequests.CreateClassRequest(
                        "INT1405_01", "Lớp 01", "INT1405", 2, 1, 3, 60)));

        assertEquals(1, json.get("tietBD").asInt());
        assertFalse(json.has("tietBatDau"));
    }

    @Test
    void controllerBoundaryUsesSharedApiResponseWithoutNestedEnvelope() throws Exception {
        AssistantResponses.Result<List<AssistantResponses.SemesterDTO>> result =
                AssistantResponses.Result.success(
                        200,
                        "Danh sách học kỳ kèm cờ isEditable",
                        List.of(new AssistantResponses.SemesterDTO(
                                "2025_2026_HK1",
                                "Học kỳ 1 (2025 - 2026)",
                                LocalDate.of(2025, 9, 1),
                                LocalDate.of(2026, 1, 15),
                                true)),
                        new AssistantResponses.SemesterMeta(1, "2025_2026_HK1"));

        ApiResponse<List<AssistantResponses.SemesterDTO>> response = ApiResponse.success(
                result.getStatusCode(), result.getMessage(), result.getData(), result.getMeta());
        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));

        assertEquals(200, json.get("status").asInt());
        assertEquals("2025_2026_HK1", json.get("data").get(0).get("maHocKy").asText());
        assertEquals("2025-09-01", json.get("data").get(0).get("thoiGianBD").asText());
        assertTrue(json.get("data").get(0).get("isEditable").asBoolean());
        assertFalse(json.get("data").get(0).has("editable"));
        assertEquals(1, json.get("meta").get("tongSoHocKy").asInt());
        assertFalse(json.get("data").get(0).has("data"));
        assertFalse(json.get("data").get(0).has("status"));
    }

    @Test
    void sharedApiResponseSerializesCreateAndNullActionData() throws Exception {
        AssistantResponses.Result<AssistantResponses.CreateClassData> createdResult =
                AssistantResponses.Result.success(
                        201,
                        "Tạo lớp thành công",
                        new AssistantResponses.CreateClassData("INT1405_01", 15));
        ApiResponse<AssistantResponses.CreateClassData> created = ApiResponse.success(
                createdResult.getStatusCode(),
                createdResult.getMessage(),
                createdResult.getData(),
                createdResult.getMeta());

        JsonNode createdJson = objectMapper.readTree(objectMapper.writeValueAsString(created));
        assertEquals("INT1405_01", createdJson.get("data").get("maLHP").asText());
        assertEquals(15, createdJson.get("data").get("soBuoiHocDaSinh").asInt());

        AssistantResponses.Result<Void> deletedResult =
                AssistantResponses.Result.success(200, "Xóa lịch học thành công");
        ApiResponse<Void> deleted = ApiResponse.success(
                deletedResult.getStatusCode(), deletedResult.getMessage(), deletedResult.getData());

        JsonNode deletedJson = objectMapper.readTree(objectMapper.writeValueAsString(deleted));
        assertTrue(deletedJson.has("data"));
        assertTrue(deletedJson.get("data").isNull());
        assertNull(deletedResult.getData());
    }

    @Test
    void keepsBinaryExportInInternalResultInsteadOfJsonEnvelope() {
        byte[] content = {1, 2, 3};
        AssistantResponses.FilePayload file = new AssistantResponses.FilePayload(
                content,
                "students-INT1405_01.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        AssistantResponses.Result<List<AssistantResponses.StudentDTO>> result =
                AssistantResponses.Result.file(200, "Xuất danh sách sinh viên", file);

        assertTrue(result.isFileResponse());
        assertNull(result.getData());
        assertEquals("students-INT1405_01.xlsx", result.getFile().getFileName());
        assertArrayEquals(content, result.getFile().getContent());

        content[0] = 9;
        assertArrayEquals(new byte[]{1, 2, 3}, result.getFile().getContent());
    }

    @Test
    void carriesErrorsInternallyForApiResponseConversion() {
        AssistantResponses.Result<Void> result = AssistantResponses.Result.error(
                400, "Dữ liệu không hợp lệ", List.of("page phải lớn hơn hoặc bằng 1"));
        ApiResponse<Void> response = ApiResponse.error(
                result.getStatusCode(), result.getMessage(), result.getErrors());

        assertFalse(result.isSuccess());
        assertEquals(400, response.getStatus());
        assertEquals(result.getErrors(), response.getErrors());
    }
}
