package com.academic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.core.common.ApiResponse;
import com.university.modules.student.StudentRequest.*;
import com.university.modules.student.StudentResponse.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class StudentJsonPayloadTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Test 1: EnrollCourse Request and Response JSON (Route 15)")
    void testEnrollCourseJson() throws Exception {
        // Request payload
        String reqJson = "{\"maLHP\":\"INT1405_01\"}";
        EnrollCourseRequestDTO req = objectMapper.readValue(reqJson, EnrollCourseRequestDTO.class);
        assertEquals("INT1405_01", req.getMaLHP());

        // Response payload
        LocalDateTime now = LocalDateTime.parse("2025-08-15T09:15:30");
        EnrollCourseResponseDTO resData = new EnrollCourseResponseDTO(10582L, "INT1405_01", "DANG_MO", now);
        ApiResponse<EnrollCourseResponseDTO> apiResp = ApiResponse.success(201, "ÄÄƒng kĂ½ thĂ nh cĂ´ng", resData);

        String jsonOut = objectMapper.writeValueAsString(apiResp);
        System.out.println("Route 15 Output JSON: " + jsonOut);

        assertTrue(jsonOut.contains("\"status\":201"));
        assertTrue(jsonOut.contains("\"message\":\"ÄÄƒng kĂ½ thĂ nh cĂ´ng\""));
        assertTrue(jsonOut.contains("\"maHoSo\":10582"));
        assertTrue(jsonOut.contains("\"maLHP\":\"INT1405_01\""));
        assertTrue(jsonOut.contains("\"trangThai\":\"DANG_MO\""));
    }

    @Test
    @DisplayName("Test 2: Cancel Enrollment Response JSON (Route 16)")
    void testCancelEnrollmentJson() throws Exception {
        CancelEnrollmentResponseDTO resData = new CancelEnrollmentResponseDTO(10582L, "DAHUY");
        ApiResponse<CancelEnrollmentResponseDTO> apiResp = ApiResponse.success(200, "Há»§y lá»›p thĂ nh cĂ´ng", resData);

        String jsonOut = objectMapper.writeValueAsString(apiResp);
        System.out.println("Route 16 Output JSON: " + jsonOut);

        assertTrue(jsonOut.contains("\"status\":200"));
        assertTrue(jsonOut.contains("\"maHoSo\":10582"));
        assertTrue(jsonOut.contains("\"trangThai\":\"DAHUY\""));
    }

    @Test
    @DisplayName("Test 3: Open Class Request (Route 17)")
    void testOpenClassRequestJson() throws Exception {
        String reqJson = "{\"maHP\":\"INT1405\",\"lyDo\":\"Cáº§n hoĂ n thĂ nh tĂ­n chá»‰ báº¯t buá»™c Ä‘á»ƒ ká»‹p tiáº¿n Ä‘á»™ tá»‘t nghiá»‡p\",\"lichHocMongMuon\":\"Thá»© 4 (Tiáº¿t 1-3) hoáº·c Thá»© 6 (Tiáº¿t 7-9)\"}";
        OpenClassRequestRequestDTO req = objectMapper.readValue(reqJson, OpenClassRequestRequestDTO.class);
        assertEquals("INT1405", req.getMaHP());
        assertEquals("Cáº§n hoĂ n thĂ nh tĂ­n chá»‰ báº¯t buá»™c Ä‘á»ƒ ká»‹p tiáº¿n Ä‘á»™ tá»‘t nghiá»‡p", req.getLyDo());
        assertEquals("Thá»© 4 (Tiáº¿t 1-3) hoáº·c Thá»© 6 (Tiáº¿t 7-9)", req.getLichHocMongMuon());

        OpenClassRequestResponseDTO resData = new OpenClassRequestResponseDTO("YC-2025-0012", "DAGUI");
        ApiResponse<OpenClassRequestResponseDTO> apiResp = ApiResponse.success(201, "Gá»­i kiáº¿n nghá»‹ thĂ nh cĂ´ng", resData);

        String jsonOut = objectMapper.writeValueAsString(apiResp);
        System.out.println("Route 17 Output JSON: " + jsonOut);

        assertTrue(jsonOut.contains("\"status\":201"));
        assertTrue(jsonOut.contains("\"maYeuCau\":\"YC-2025-0012\""));
        assertTrue(jsonOut.contains("\"trangThai\":\"DAGUI\""));
    }

    @Test
    @DisplayName("Test 4: Open Classes for Registration (Route 13)")
    void testOpenedEnrollingClassJson() throws Exception {
        OpenedEnrollingClass.OpenClassItem item = new OpenedEnrollingClass.OpenClassItem(
                "INT1405_01", "Ká»¹ nghá»‡ pháº§n má»m - Lá»›p 01", "INT1405", "Ká»¹ nghá»‡ pháº§n má»m", 3,
                "TS. Tráº§n Thá»‹ BĂ¬nh", 2, 1, 3, "A2-301", 55, 60,
                OffsetDateTime.parse("2025-08-15T08:00:00Z"), OffsetDateTime.parse("2025-08-25T17:00:00Z"), "DANG_MO"
        );

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("page", 1);
        meta.put("pageSize", 10);
        meta.put("totalItems", 45);
        meta.put("totalPages", 5);

        ApiResponse<List<OpenedEnrollingClass.OpenClassItem>> apiResp = ApiResponse.success(
                200, "Danh sĂ¡ch lá»›p má»Ÿ kĂ¨m meta phĂ¢n trang", Collections.singletonList(item), meta
        );

        String jsonOut = objectMapper.writeValueAsString(apiResp);
        System.out.println("Route 13 Output JSON: " + jsonOut);

        assertTrue(jsonOut.contains("\"maLHP\":\"INT1405_01\""));
        assertTrue(jsonOut.contains("\"soTinChi\":3"));
        assertTrue(jsonOut.contains("\"tietBatDau\":1"));
        assertTrue(jsonOut.contains("\"totalItems\":45"));
        assertTrue(jsonOut.contains("\"totalPages\":5"));
    }

    @Test
    @DisplayName("Test 5: Registered Classes (Route 14)")
    void testRegisteredClassesJson() throws Exception {
        RegisteredClassResponseDTO.RegisteredClassItem item = new RegisteredClassResponseDTO.RegisteredClassItem(
                10582L, "INT1405_01", "Ká»¹ nghá»‡ pháº§n má»m", "Ká»¹ nghá»‡ pháº§n má»m - Lá»›p 01", 3,
                2, 1, 3, "TS. Tráº§n Thá»‹ BĂ¬nh", "A2-301"
        );

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("tongSoLopDaDangKy", 6);
        meta.put("tongSoTinChiDaDangKy", 18);
        meta.put("hanMucTinChiToiDa", 24);

        ApiResponse<List<RegisteredClassResponseDTO.RegisteredClassItem>> apiResp = ApiResponse.success(
                200, "Danh sĂ¡ch lá»›p Ä‘Ă£ ghi danh kĂ¨m meta tá»•ng tĂ­n chá»‰", Collections.singletonList(item), meta
        );

        String jsonOut = objectMapper.writeValueAsString(apiResp);
        System.out.println("Route 14 Output JSON: " + jsonOut);

        assertTrue(jsonOut.contains("\"maHoSo\":10582"));
        assertTrue(jsonOut.contains("\"tongSoLopDaDangKy\":6"));
        assertTrue(jsonOut.contains("\"tongSoTinChiDaDangKy\":18"));
        assertTrue(jsonOut.contains("\"hanMucTinChiToiDa\":24"));
    }

    @Test
    @DisplayName("Test 6: Student Schedule (Route 12)")
    void testStudentScheduleJson() throws Exception {
        StudentScheduleResponseDTO.ScheduleItemDTO item = new StudentScheduleResponseDTO.ScheduleItemDTO(
                "INT1405_01", "Ká»¹ nghá»‡ pháº§n má»m", "Ká»¹ nghá»‡ pháº§n má»m - Lá»›p 01", "TS. Tráº§n Thá»‹ BĂ¬nh",
                2, 1, 3, "A2-301"
        );

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("maHocKyHienTai", "2025_2026_HK1");
        meta.put("tenHocKy", "Há»c ká»³ 1 (2025 - 2026)");

        ApiResponse<List<StudentScheduleResponseDTO.ScheduleItemDTO>> apiResp = ApiResponse.success(
                200, "Thá»i khĂ³a biá»ƒu sinh viĂªn cho ma tráº­n tuáº§n", Collections.singletonList(item), meta
        );

        String jsonOut = objectMapper.writeValueAsString(apiResp);
        System.out.println("Route 12 Output JSON: " + jsonOut);

        assertTrue(jsonOut.contains("\"tietBD\":1"));
        assertTrue(jsonOut.contains("\"maHocKyHienTai\":\"2025_2026_HK1\""));
    }

    @Test
    @DisplayName("Test 7: Student Grades (Route 11)")
    void testStudentGradesJson() throws Exception {
        StudentGradeResponseDTO.CourseGradeItemDTO course = new StudentGradeResponseDTO.CourseGradeItemDTO(
                "INT1405", "Ká»¹ nghá»‡ pháº§n má»m", 3, 8.5, 8.0, 8.2, "Äáº¡t"
        );
        StudentGradeResponseDTO.SemesterGradeDTO sem = new StudentGradeResponseDTO.SemesterGradeDTO(
                "2025_2026_HK1", "Há»c ká»³ 1 (2025 - 2026)", 3.52, 18, "Giá»i", Collections.singletonList(course)
        );

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("diemTBTichLuy", 3.45);
        meta.put("soTinChiTichLuy", 85);
        meta.put("xepLoaiHocLucTichLuy", "KhĂ¡");

        ApiResponse<List<StudentGradeResponseDTO.SemesterGradeDTO>> apiResp = ApiResponse.success(
                200, "Báº£ng Ä‘iá»ƒm sinh viĂªn kĂ¨m meta há»c vá»¥ tĂ­ch lÅ©y", Collections.singletonList(sem), meta
        );

        String jsonOut = objectMapper.writeValueAsString(apiResp);
        System.out.println("Route 11 Output JSON: " + jsonOut);

        assertTrue(jsonOut.contains("\"diemTBHK\":3.52"));
        assertTrue(jsonOut.contains("\"diemTBTichLuy\":3.45"));
        assertTrue(jsonOut.contains("\"soTinChiTichLuy\":85"));
    }
}

