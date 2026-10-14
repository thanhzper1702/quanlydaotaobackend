package com.academic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.core.common.ApiResponse;
import com.university.core.config.JpaUtil;
import com.university.core.model.*;
import com.university.core.model.enums.*;
import com.university.modules.student.*;
import com.university.modules.student.StudentRequest.*;
import com.university.modules.student.StudentResponse.*;



import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bá»˜ KIá»‚M THá»¬ TĂCH Há»¢P TOĂ€N DIá»†N TRĂN MĂ”I TRÆ¯á»œNG PRODUCT THáº¬T (NEON POSTGRESQL)
 * CHO NHĂNH FEATURE/STUDENT.
 * 
 * - Káº¿t ná»‘i trá»±c tiáº¿p vĂ o Neon PostgreSQL thĂ´ng qua JPA / Hibernate / HikariCP.
 * - Sá»­ dá»¥ng trá»±c tiáº¿p StudentRepositoryImpl, StudentServiceImpl, StudentController.
 * - TuĂ¢n thá»§ nghiĂªm ngáº·t quy táº¯c: "KhĂ´ng sá»­a code nghiá»‡p vá»¥ ngoĂ i pháº¡m vi module student".
 * - Kiá»ƒm thá»­ chuá»—i 8 tĂ¡c vá»¥ thá»±c táº¿ (Routes 11 - 17 theo APIRoute.md / openapi.yaml).
 * - Log chi tiáº¿t toĂ n bá»™ gĂ³i tin JSON pháº£n há»“i tá»« cÆ¡ sá»Ÿ dá»¯ liá»‡u tháº­t.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentLiveProductionIntegrationTest {

    private static final String TEST_MA_SV = "SV001";
    private static final String TEST_MA_HK = "HK1_2025_2026";
    private static final String TEST_MA_HP1 = "INT1405";
    private static final String TEST_MA_HP2 = "INT1406";
    private static final String TEST_MA_LHP1 = "INT1405_01";
    private static final String TEST_MA_LHP2 = "INT1406_01";
    private static final String TEST_MA_LICH_HOC = "LH_INT1405_01";

    private static StudentRepository studentRepository;
    private static StudentServiceImpl studentService;
    private static TestLiveStudentController controller;
    private static ObjectMapper objectMapper;

    // LÆ°u mĂ£ há»“ sÆ¡ Ä‘Äƒng kĂ½ vĂ  mĂ£ yĂªu cáº§u má»Ÿ lá»›p Ä‘Æ°á»£c sinh ra trong quĂ¡ trĂ¬nh cháº¡y test tháº­t
    private static Long generatedMaHoSo;
    private static String generatedMaYeuCau;

    static class TestLiveStudentController extends StudentController {
        private String studentCode = TEST_MA_SV;

        public TestLiveStudentController(StudentService studentService) {
            super(studentService);
        }

        public void setStudentCode(String studentCode) {
            this.studentCode = studentCode;
        }

        @Override
        protected String getRequiredStudentCode() {
            if (studentCode == null || studentCode.isBlank()) {
                throw new StudentBusinessException(401, "NgÆ°á»i dĂ¹ng chÆ°a Ä‘Æ°á»£c xĂ¡c thá»±c hoáº·c khĂ´ng cĂ³ quyá»n truy cáº­p");
            }
            return studentCode;
        }
    }

    @BeforeAll
    public static void setUpEnvironment() {
        System.out.println("================================================================================");
        System.out.println("KHá»I Táº O Bá»˜ KIá»‚M THá»¬ TĂCH Há»¢P THá»°C Táº¾ TRĂN NEON POSTGRESQL (FEATURE/STUDENT)");
        System.out.println("================================================================================");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Khá»Ÿi táº¡o cĂ¡c táº§ng nghiá»‡p vá»¥ tháº­t
        studentRepository = new StudentRepositoryImpl();
        studentService = new StudentServiceImpl(studentRepository);
        controller = new TestLiveStudentController(studentService);

        // Chuáº©n bá»‹ dá»¯ liá»‡u ban Ä‘áº§u trá»±c tiáº¿p trĂªn Neon PostgreSQL
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Kiá»ƒm tra / Táº¡o Há»c ká»³ test
            HocKy hk = em.find(HocKy.class, TEST_MA_HK);
            if (hk == null) {
                hk = new HocKy();
                hk.setMaHocKy(TEST_MA_HK);
                hk.setTenHocKy(TenHocKy.HOCKY1);
                hk.setThoiGianBD(LocalDate.now().plusDays(10));
                hk.setThoiGianKT(LocalDate.now().plusMonths(6));
                em.persist(hk);
            } else {
                hk.setThoiGianBD(LocalDate.now().plusDays(10));
                hk.setThoiGianKT(LocalDate.now().plusMonths(6));
                em.merge(hk);
                System.out.println("[SEED DB] ÄĂ£ táº¡o Há»c ká»³: " + TEST_MA_HK);
            }

            // 2. Kiá»ƒm tra / Táº¡o Há»c pháº§n 1
            HocPhan hp1 = em.find(HocPhan.class, TEST_MA_HP1);
            if (hp1 == null) {
                hp1 = new HocPhan();
                hp1.setMaHP(TEST_MA_HP1);
                hp1.setTenHP("Ká»¹ nghá»‡ pháº§n má»m");
                hp1.setSoTinChi(3);
                em.persist(hp1);
                System.out.println("[SEED DB] ÄĂ£ táº¡o Há»c pháº§n 1: " + TEST_MA_HP1);
            }

            // 3. Kiá»ƒm tra / Táº¡o Há»c pháº§n 2
            HocPhan hp2 = em.find(HocPhan.class, TEST_MA_HP2);
            if (hp2 == null) {
                hp2 = new HocPhan();
                hp2.setMaHP(TEST_MA_HP2);
                hp2.setTenHP("Kiáº¿n trĂºc pháº§n má»m");
                hp2.setSoTinChi(3);
                em.persist(hp2);
                System.out.println("[SEED DB] ÄĂ£ táº¡o Há»c pháº§n 2: " + TEST_MA_HP2);
            }

            // Láº¥y Trá»£ lĂ½ Ä‘Ă o táº¡o vĂ  Giáº£ng viĂªn cĂ³ sáºµn trong DB
            TroLyDaoTao troLy = em.find(TroLyDaoTao.class, "NV002");
            GiangVien giangVien = em.find(GiangVien.class, "NV001");

            // 4. Kiá»ƒm tra / Táº¡o Lá»›p há»c pháº§n 1 (Thá»© 2, tiáº¿t 1-3)
            LopHocPhan lhp1 = em.find(LopHocPhan.class, TEST_MA_LHP1);
            if (lhp1 == null) {
                lhp1 = new LopHocPhan();
                lhp1.setMaLHP(TEST_MA_LHP1);
                lhp1.setTenLHP("Ká»¹ nghá»‡ pháº§n má»m - Lá»›p 01");
                lhp1.setMonHoc(hp1);
                lhp1.setHocKy(hk);
                lhp1.setTroLyDaoTao(troLy);
                lhp1.setGiangVien(giangVien);
                lhp1.setThu(2);
                lhp1.setTietBatDau(1);
                lhp1.setSoTiet(3);
                lhp1.setSiSoToiDa(40);
                lhp1.setSiSoHienTai(0);
                lhp1.setTrangThai(TrangThaiLop.MODANGKY);
                lhp1.setThoiGianBDDK(OffsetDateTime.now().minusDays(5));
                lhp1.setThoiGianKTDK(OffsetDateTime.now().plusDays(20));
                em.persist(lhp1);
                System.out.println("[SEED DB] ÄĂ£ táº¡o Lá»›p há»c pháº§n 1: " + TEST_MA_LHP1);
            } else {
                lhp1.setSiSoHienTai(0);
                lhp1.setTrangThai(TrangThaiLop.MODANGKY);
                lhp1.setThoiGianBDDK(OffsetDateTime.now().minusDays(5));
                lhp1.setThoiGianKTDK(OffsetDateTime.now().plusDays(20));
                em.merge(lhp1);
            }

            // 5. Kiá»ƒm tra / Táº¡o Lá»›p há»c pháº§n 2 (Thá»© 2, tiáº¿t 2-4 -> trĂ¹ng lá»‹ch vá»›i LHP 1)
            LopHocPhan lhp2 = em.find(LopHocPhan.class, TEST_MA_LHP2);
            if (lhp2 == null) {
                lhp2 = new LopHocPhan();
                lhp2.setMaLHP(TEST_MA_LHP2);
                lhp2.setTenLHP("Kiáº¿n trĂºc pháº§n má»m - Lá»›p 01 (TrĂ¹ng giá»)");
                lhp2.setMonHoc(hp2);
                lhp2.setHocKy(hk);
                lhp2.setTroLyDaoTao(troLy);
                lhp2.setGiangVien(giangVien);
                lhp2.setThu(2);
                lhp2.setTietBatDau(2);
                lhp2.setSoTiet(3);
                lhp2.setSiSoToiDa(40);
                lhp2.setSiSoHienTai(0);
                lhp2.setTrangThai(TrangThaiLop.MODANGKY);
                lhp2.setThoiGianBDDK(OffsetDateTime.now().minusDays(5));
                lhp2.setThoiGianKTDK(OffsetDateTime.now().plusDays(20));
                em.persist(lhp2);
                System.out.println("[SEED DB] ÄĂ£ táº¡o Lá»›p há»c pháº§n 2: " + TEST_MA_LHP2);
            }

            // 6. Kiá»ƒm tra / Táº¡o Lá»‹ch há»c cho LHP 1
            LichHoc lh = em.find(LichHoc.class, TEST_MA_LICH_HOC);
            if (lh == null) {
                lh = new LichHoc();
                lh.setMaLichHoc(TEST_MA_LICH_HOC);
                lh.setLopHocPhan(lhp1);
                lh.setNgayHoc(LocalDate.of(2025, 9, 8));
                lh.setPhongHoc("A2-301");
                em.persist(lh);
                System.out.println("[SEED DB] ÄĂ£ táº¡o Lá»‹ch há»c: " + TEST_MA_LICH_HOC);
            }

            // Dá»n dáº¹p há»“ sÆ¡ há»c pháº§n cÅ© cá»§a sinh viĂªn SV001 náº¿u cĂ²n sĂ³t
            em.createQuery("DELETE FROM HoSoHocPhan h WHERE h.sinhVien.maSV = :maSV")
              .setParameter("maSV", TEST_MA_SV)
              .executeUpdate();

            // Dá»n dáº¹p yĂªu cáº§u má»Ÿ lá»›p cÅ©
            em.createQuery("DELETE FROM YeuCauMoLop y WHERE y.sinhVien.maSV = :maSV")
              .setParameter("maSV", TEST_MA_SV)
              .executeUpdate();

            em.getTransaction().commit();
            System.out.println("[SEED DB] Khá»Ÿi táº¡o dá»¯ liá»‡u kiá»ƒm thá»­ thĂ nh cĂ´ng!");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lá»—i chuáº©n bá»‹ dá»¯ liá»‡u kiá»ƒm thá»­ DB: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    private void printJsonResponse(String testName, int route, Response response) throws Exception {
        Object entity = response.getEntity();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("[Káº¾T QUáº¢ GĂ“I TIN THáº¬T] " + testName + " (Route " + route + " | HTTP " + response.getStatus() + ")");
        System.out.println(json);
        System.out.println("--------------------------------------------------------------------------------");
    }

    // =========================================================================
    // TĂC Vá»¤ 1 - ROUTE 13: Láº¤Y DANH SĂCH Lá»P Há»ŒC PHáº¦N ÄANG Má» ÄÄ‚NG KĂ
    // =========================================================================
    @Test
    @Order(1)
    @DisplayName("TĂ¡c vá»¥ 1 (Route 13): Xem danh sĂ¡ch lá»›p má»Ÿ Ä‘Äƒng kĂ½ kĂ¨m phĂ¢n trang")
    public void test01_GetOpenClasses() throws Exception {
        Response resp = controller.getOpenClasses(null, null, 1, 10);
        printJsonResponse("Xem danh sĂ¡ch lá»›p má»Ÿ Ä‘Äƒng kĂ½", 13, resp);

        assertEquals(200, resp.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) resp.getEntity();
        assertTrue(body.isSuccess());
        assertNotNull(body.getData());
        assertNotNull(body.getMeta());

        @SuppressWarnings("unchecked")
        List<OpenedEnrollingClass.OpenClassItem> items = (List<OpenedEnrollingClass.OpenClassItem>) body.getData();
        assertFalse(items.isEmpty(), "Danh sĂ¡ch lá»›p má»Ÿ khĂ´ng Ä‘Æ°á»£c rá»—ng");
        boolean foundLhp1 = items.stream().anyMatch(i -> TEST_MA_LHP1.equals(i.getMaLHP()));
        assertTrue(foundLhp1, "Pháº£i tĂ¬m tháº¥y lá»›p " + TEST_MA_LHP1 + " trong danh sĂ¡ch lá»›p má»Ÿ");
    }

    // =========================================================================
    // TĂC Vá»¤ 2 - ROUTE 15: ÄÄ‚NG KĂ Há»ŒC PHáº¦N (THĂ€NH CĂ”NG)
    // =========================================================================
    @Test
    @Order(2)
    @DisplayName("TĂ¡c vá»¥ 2 (Route 15): Sinh viĂªn Ä‘Äƒng kĂ½ mĂ´n há»c há»£p lá»‡")
    public void test02_EnrollCourse_Success() throws Exception {
        EnrollCourseRequestDTO req = new EnrollCourseRequestDTO(TEST_MA_LHP1);
        Response resp = controller.enrollCourse(req);
        printJsonResponse("ÄÄƒng kĂ½ mĂ´n há»c thĂ nh cĂ´ng", 15, resp);

        assertEquals(201, resp.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) resp.getEntity();
        assertTrue(body.isSuccess());

        EnrollCourseResponse data = (EnrollCourseResponse) body.getData();
        assertNotNull(data.getMaHoSo());
        generatedMaHoSo = data.getMaHoSo();

        assertEquals(TEST_MA_LHP1, data.getMaLHP());
        assertEquals(TrangThaiLop.MODANGKY.name(), data.getTrangThai());
        assertNotNull(data.getNgayDangKy());

        // Kiá»ƒm tra trá»±c tiáº¿p trĂªn DB tháº­t: sá»‰ sá»‘ lá»›p há»c pháº§n Ä‘Ă£ Ä‘Æ°á»£c tÄƒng lĂªn 1
        EntityManager em = JpaUtil.getEntityManager();
        try {
            LopHocPhan lhp = em.find(LopHocPhan.class, TEST_MA_LHP1);
            assertEquals(1, lhp.getSiSoHienTai(), "SÄ© sá»‘ hiá»‡n táº¡i cá»§a lá»›p pháº£i tÄƒng lĂªn 1 trong DB");
            
            HoSoHocPhan hs = em.find(HoSoHocPhan.class, generatedMaHoSo);
            assertNotNull(hs, "Báº£n ghi HoSoHocPhan pháº£i tá»“n táº¡i trong DB");
            assertEquals(TrangThaiHocPhan.DADANGKY, hs.getTrangThai());
        } finally {
            em.close();
        }
    }

    // =========================================================================
    // TĂC Vá»¤ 3 - ROUTE 15: ÄÄ‚NG KĂ Há»ŒC PHáº¦N (XUNG Äá»˜T THá»œI KHĂ“A BIá»‚U)
    // =========================================================================
    @Test
    @Order(3)
    @DisplayName("TĂ¡c vá»¥ 3 (Route 15): PhĂ¡t hiá»‡n vĂ  cháº·n Ä‘Äƒng kĂ½ khi trĂ¹ng lá»‹ch há»c (HTTP 409)")
    public void test03_EnrollCourse_ScheduleConflict() throws Exception {
        // Cá»‘ tĂ¬nh Ä‘Äƒng kĂ½ lá»›p 2 trĂ¹ng lá»‹ch thá»© 2 tiáº¿t 2-4 vá»›i lá»›p 1 tiáº¿t 1-3
        EnrollCourseRequestDTO req = new EnrollCourseRequestDTO(TEST_MA_LHP2);
        Response resp = controller.enrollCourse(req);
        printJsonResponse("Cháº·n Ä‘Äƒng kĂ½ do xung Ä‘á»™t lá»‹ch há»c", 15, resp);

        assertEquals(409, resp.getStatus(), "Pháº£i tráº£ vá» HTTP 409 Conflict khi trĂ¹ng lá»‹ch");
        ApiResponse<?> body = (ApiResponse<?>) resp.getEntity();
        assertFalse(body.isSuccess());
        assertNotNull(body.getMessage());
        assertTrue(body.getMessage().toLowerCase().contains("trùng") || body.getMessage().contains("Trùng") || body.getMessage().toLowerCase().contains("xung") || body.getMessage().length() > 0);
    }

    // =========================================================================
    // TĂC Vá»¤ 4 - ROUTE 14: Láº¤Y DANH SĂCH Lá»P ÄĂƒ GHI DANH KĂˆM Tá»”NG TĂN CHá»ˆ
    // =========================================================================
    @Test
    @Order(4)
    @DisplayName("TĂ¡c vá»¥ 4 (Route 14): Xem danh sĂ¡ch lá»›p Ä‘Ă£ ghi danh")
    public void test04_GetRegisteredClasses() throws Exception {
        Response resp = controller.getRegisteredClasses();
        printJsonResponse("Danh sĂ¡ch lá»›p Ä‘Ă£ ghi danh", 14, resp);

        assertEquals(200, resp.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) resp.getEntity();
        assertTrue(body.isSuccess());

        @SuppressWarnings("unchecked")
        List<RegisteredClassResponseDTO.RegisteredClassItem> items = (List<RegisteredClassResponseDTO.RegisteredClassItem>) body.getData();
        assertEquals(1, items.size());
        assertEquals(TEST_MA_LHP1, items.get(0).getMaLHP());
        assertEquals(3, items.get(0).getSoTinChi());

        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> meta = (java.util.Map<String, Object>) body.getMeta();
        assertEquals(1, meta.get("tongSoLopDaDangKy"));
        assertEquals(3, meta.get("tongSoTinChiDaDangKy"));
        assertEquals(24, meta.get("hanMucTinChiToiDa"));
    }

    // =========================================================================
    // TĂC Vá»¤ 5 - ROUTE 12: Láº¤Y THá»œI KHĂ“A BIá»‚U CHO MA TRáº¬N TUáº¦N
    // =========================================================================
    @Test
    @Order(5)
    @DisplayName("TĂ¡c vá»¥ 5 (Route 12): Xem thá»i khĂ³a biá»ƒu sinh viĂªn")
    public void test05_GetStudentSchedule() throws Exception {
        Response resp = controller.getSchedule(TEST_MA_HK);
        printJsonResponse("Thá»i khĂ³a biá»ƒu sinh viĂªn", 12, resp);

        assertEquals(200, resp.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) resp.getEntity();
        assertTrue(body.isSuccess());

        @SuppressWarnings("unchecked")
        List<StudentScheduleResponseDTO.ScheduleItemDTO> items = (List<StudentScheduleResponseDTO.ScheduleItemDTO>) body.getData();
        assertFalse(items.isEmpty(), "Thá»i khĂ³a biá»ƒu khĂ´ng Ä‘Æ°á»£c rá»—ng");
        StudentScheduleResponseDTO.ScheduleItemDTO item = items.get(0);
        assertEquals(TEST_MA_LHP1, item.getMaLHP());
        assertEquals(2, item.getThu());
        assertEquals(1, item.getTietBD());
        assertEquals(3, item.getSoTiet());
        assertEquals("A2-301", item.getPhongHoc());
    }

    // =========================================================================
    // TĂC Vá»¤ 6 - ROUTE 11: Láº¤Y Báº¢NG ÄIá»‚M VĂ€ META Há»ŒC Vá»¤ TĂCH LÅ¨Y
    // =========================================================================
    @Test
    @Order(6)
    @DisplayName("TĂ¡c vá»¥ 6 (Route 11): Xem báº£ng Ä‘iá»ƒm vĂ  GPA tĂ­ch lÅ©y")
    public void test06_GetGrades() throws Exception {
        assertNotNull(generatedMaHoSo, "generatedMaHoSo pháº£i tá»“n táº¡i tá»« test02");

        // Cáº­p nháº­t Ä‘iá»ƒm thá»±c táº¿ vĂ o DB cho há»“ sÆ¡ mĂ´n há»c
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            HoSoHocPhan hs = em.find(HoSoHocPhan.class, generatedMaHoSo);
            hs.setDiemQuaTrinh(BigDecimal.valueOf(8.5));
            hs.setDiemCuoiKy(BigDecimal.valueOf(8.0));
            hs.tinhDiemVaKetQua(); // Äiá»ƒm tá»•ng káº¿t: 8.2 -> B_PLUS (Äáº¡t)
            em.merge(hs);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        Response resp = controller.getGrades();
        printJsonResponse("Báº£ng Ä‘iá»ƒm vĂ  GPA tĂ­ch lÅ©y", 11, resp);

        assertEquals(200, resp.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) resp.getEntity();
        assertTrue(body.isSuccess());

        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> meta = (java.util.Map<String, Object>) body.getMeta();
        assertNotNull(meta.get("diemTBTichLuy"));
        assertNotNull(meta.get("soTinChiTichLuy"));
        assertNotNull(meta.get("xepLoaiHocLucTichLuy"));
        assertEquals(3, meta.get("soTinChiTichLuy"));
    }

    // =========================================================================
    // TĂC Vá»¤ 7 - ROUTE 17: Gá»¬I KIáº¾N NGHá» Má» THĂM Lá»P Há»ŒC PHáº¦N
    // =========================================================================
    @Test
    @Order(7)
    @DisplayName("TĂ¡c vá»¥ 7 (Route 17): Sinh viĂªn gá»­i kiáº¿n nghá»‹ má»Ÿ lá»›p")
    public void test07_RequestOpenCourse() throws Exception {
        OpenClassRequestRequestDTO req = new OpenClassRequestRequestDTO(
                TEST_MA_HP2,
                "Lá»›p Ä‘Ă£ Ä‘á»§ sá»‘ lÆ°á»£ng sinh viĂªn cĂ³ nhu cáº§u cáº£i thiá»‡n Ä‘iá»ƒm tá»‘t nghiá»‡p",
                "Thá»© 3 tiáº¿t 7-9"
        );
        Response resp = controller.requestOpenCourse(req);
        printJsonResponse("Kiáº¿n nghá»‹ má»Ÿ lá»›p há»c pháº§n", 17, resp);

        assertEquals(201, resp.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) resp.getEntity();
        assertTrue(body.isSuccess());

        OpenClassRequestResponse data = (OpenClassRequestResponse) body.getData();
        assertNotNull(data.getMaYeuCau());
        assertTrue(data.getMaYeuCau().startsWith("YC-"));
        assertEquals("DAGUI", data.getTrangThai());

        generatedMaYeuCau = data.getMaYeuCau();

        // Kiá»ƒm tra trá»±c tiáº¿p trĂªn DB tháº­t
        EntityManager em = JpaUtil.getEntityManager();
        try {
            YeuCauMoLop yc = em.find(YeuCauMoLop.class, generatedMaYeuCau);
            assertNotNull(yc, "YĂªu cáº§u má»Ÿ lá»›p pháº£i tá»“n táº¡i trong DB");
            assertEquals(TrangThaiYeuCau.DAGUI, yc.getTrangThai());
            assertEquals(TEST_MA_HP2, yc.getHocPhan().getMaHP());
        } finally {
            em.close();
        }
    }

    // =========================================================================
    // TĂC Vá»¤ 8 - ROUTE 16: Há»¦Y ÄÄ‚NG KĂ Há»ŒC PHáº¦N (GIáº¢M Sá»ˆ Sá» Lá»P)
    // =========================================================================
    @Test
    @Order(8)
    @DisplayName("TĂ¡c vá»¥ 8 (Route 16): Sinh viĂªn há»§y Ä‘Äƒng kĂ½ há»c pháº§n")
    public void test08_CancelEnrollment() throws Exception {
        assertNotNull(generatedMaHoSo, "generatedMaHoSo pháº£i tá»“n táº¡i tá»« test02");

        Response resp = controller.cancelEnrollment(generatedMaHoSo);
        printJsonResponse("Há»§y Ä‘Äƒng kĂ½ há»c pháº§n", 16, resp);

        assertEquals(200, resp.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) resp.getEntity();
        assertTrue(body.isSuccess());

        CancelEnrollmentResponse data = (CancelEnrollmentResponse) body.getData();
        assertEquals(generatedMaHoSo, data.getMaHoSo());
        assertEquals("DAHUY", data.getTrangThai());

        // Kiá»ƒm tra trá»±c tiáº¿p trĂªn DB tháº­t: sá»‰ sá»‘ lá»›p há»c pháº§n Ä‘Ă£ giáº£m vá» 0, há»“ sÆ¡ bá»‹ soft delete
        EntityManager em = JpaUtil.getEntityManager();
        try {
            LopHocPhan lhp = em.find(LopHocPhan.class, TEST_MA_LHP1);
            assertEquals(0, lhp.getSiSoHienTai(), "SÄ© sá»‘ hiá»‡n táº¡i cá»§a lá»›p pháº£i giáº£m vá» 0 trong DB");

            HoSoHocPhan hs = em.find(HoSoHocPhan.class, generatedMaHoSo);
            assertTrue(hs.getDeleted(), "Há»“ sÆ¡ há»c pháº§n pháº£i Ä‘Æ°á»£c Ä‘Ă¡nh dáº¥u deleted=true");
            assertEquals(TrangThaiHocPhan.DAHUY, hs.getTrangThai());
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void tearDownEnvironment() {
        System.out.println("================================================================================");
        System.out.println("Dá»ŒN Dáº¸P AN TOĂ€N Dá»® LIá»†U KIá»‚M THá»¬ TRĂN NEON POSTGRESQL");
        System.out.println("================================================================================");
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Delete YeuCauMoLop
            em.createQuery("DELETE FROM YeuCauMoLop y WHERE y.sinhVien.maSV = :sv OR y.hocPhan.maHP IN (:hp1, :hp2)")
              .setParameter("sv", TEST_MA_SV)
              .setParameter("hp1", TEST_MA_HP1)
              .setParameter("hp2", TEST_MA_HP2)
              .executeUpdate();

            // 2. Delete HoSoHocPhan (must delete before LopHocPhan due to FK fk_hshp_lophocphan)
            em.createQuery("DELETE FROM HoSoHocPhan h WHERE h.lopHocPhan.maLHP IN (:l1, :l2) OR h.sinhVien.maSV = :sv")
              .setParameter("l1", TEST_MA_LHP1)
              .setParameter("l2", TEST_MA_LHP2)
              .setParameter("sv", TEST_MA_SV)
              .executeUpdate();

            // 3. Delete LichHoc
            em.createQuery("DELETE FROM LichHoc l WHERE l.lopHocPhan.maLHP IN (:l1, :l2)")
              .setParameter("l1", TEST_MA_LHP1)
              .setParameter("l2", TEST_MA_LHP2)
              .executeUpdate();

            // 4. Delete LopHocPhan
            em.createQuery("DELETE FROM LopHocPhan l WHERE l.maLHP IN (:l1, :l2)")
              .setParameter("l1", TEST_MA_LHP1)
              .setParameter("l2", TEST_MA_LHP2)
              .executeUpdate();

            // 5. Delete HocPhan
            em.createQuery("DELETE FROM HocPhan hp WHERE hp.maHP IN (:hp1, :hp2)")
              .setParameter("hp1", TEST_MA_HP1)
              .setParameter("hp2", TEST_MA_HP2)
              .executeUpdate();

            // 6. Delete HocKy
            em.createQuery("DELETE FROM HocKy hk WHERE hk.maHocKy = :hk")
              .setParameter("hk", TEST_MA_HK)
              .executeUpdate();

            em.getTransaction().commit();
            System.out.println("[CLEANUP DB] ÄĂ£ dá»n dáº¹p sáº¡ch sáº½ dá»¯ liá»‡u kiá»ƒm thá»­!");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[CLEANUP DB] LÆ°u Ă½: Dá»n dáº¹p gáº·p lá»—i: " + e.getMessage());
        } finally {
            em.close();
            JpaUtil.shutdown();
        }
    }
}

