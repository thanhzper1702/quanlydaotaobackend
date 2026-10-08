package com.academic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.core.common.ApiResponse;
import com.university.core.model.*;
import com.university.core.model.enums.*;
import com.university.modules.student.*;
import com.university.modules.student.StudentRequest.*;
import com.university.modules.student.StudentResponse.*;



import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceAndControllerTest {

    private FakeStudentRepository fakeRepository;
    private StudentServiceImpl studentService;
    private TestStudentController controller;
    private ObjectMapper objectMapper;

    // Subclass of StudentController to override getAuthenticatedUserCode() for testing
    static class TestStudentController extends StudentController {
        private String testStudentCode = "B21DCCN001";

        public TestStudentController(StudentService studentService) {
            super(studentService);
        }

        public void setTestStudentCode(String testStudentCode) {
            this.testStudentCode = testStudentCode;
        }

        @Override
        protected String getRequiredStudentCode() {
            if (testStudentCode == null || testStudentCode.isBlank()) {
                throw new StudentBusinessException(401, "NgÆ°á»i dĂ¹ng chÆ°a Ä‘Æ°á»£c xĂ¡c thá»±c hoáº·c khĂ´ng cĂ³ quyá»n truy cáº­p");
            }
            return testStudentCode;
        }
    }

    // In-memory fake repository implementing StudentRepository
    static class FakeStudentRepository implements StudentRepository {
        Map<String, SinhVien> students = new HashMap<>();
        Map<String, HocPhan> courses = new HashMap<>();
        Map<String, LopHocPhan> classes = new HashMap<>();
        Map<Long, HoSoHocPhan> enrollments = new HashMap<>();
        Map<String, YeuCauMoLop> requests = new HashMap<>();
        Map<String, List<LichHoc>> schedules = new HashMap<>();
        long nextHoSoId = 10000L;

        @Override
        public Optional<SinhVien> findByMaSV(String maSV) {
            return Optional.ofNullable(students.get(maSV));
        }

        @Override
        public Optional<HocPhan> findHocPhanByMaHP(String maHP) {
            return Optional.ofNullable(courses.get(maHP));
        }

        @Override
        public String findPhongHocByMaLHP(String maLHP) {
            return "A2-301";
        }

        @Override
        public List<LopHocPhan> findOpenClasses(String maHocKy, String keyword, int page, int limit) {
            List<LopHocPhan> list = new ArrayList<>();
            for (LopHocPhan lhp : classes.values()) {
                if (lhp.getTrangThai() == TrangThaiLop.MODANGKY && !lhp.isDeleted()) {
                    if (keyword == null || lhp.getTenLHP().toLowerCase().contains(keyword.toLowerCase())
                            || lhp.getMaLHP().toLowerCase().contains(keyword.toLowerCase())) {
                        list.add(lhp);
                    }
                }
            }
            return list;
        }

        @Override
        public long countOpenClasses(String maHocKy, String keyword) {
            return findOpenClasses(maHocKy, keyword, 1, 100).size();
        }

        @Override
        public Optional<LopHocPhan> findClassByMaLHP(String maLHP) {
            return Optional.ofNullable(classes.get(maLHP));
        }

        @Override
        public LopHocPhan updateClass(LopHocPhan lhp) {
            classes.put(lhp.getMaLHP(), lhp);
            return lhp;
        }

        @Override
        public List<HoSoHocPhan> findRegisteredClasses(String maSV) {
            List<HoSoHocPhan> list = new ArrayList<>();
            for (HoSoHocPhan hs : enrollments.values()) {
                if (hs.getSinhVien() != null && maSV.equals(hs.getSinhVien().getMaSV())
                        && !hs.isDeleted() && !hs.isDaHuy()) {
                    list.add(hs);
                }
            }
            return list;
        }

        @Override
        public List<HoSoHocPhan> findRegisteredClassesByHocKy(String maSV, String maHocKy) {
            return findRegisteredClasses(maSV);
        }

        @Override
        public Optional<HoSoHocPhan> findEnrollment(String maSV, String maLHP) {
            for (HoSoHocPhan hs : enrollments.values()) {
                if (hs.getSinhVien() != null && maSV.equals(hs.getSinhVien().getMaSV())
                        && hs.getLopHocPhan() != null && maLHP.equals(hs.getLopHocPhan().getMaLHP())) {
                    return Optional.of(hs);
                }
            }
            return Optional.empty();
        }

        @Override
        public Optional<HoSoHocPhan> findEnrollmentById(Long maHoSo) {
            return Optional.ofNullable(enrollments.get(maHoSo));
        }

        @Override
        public HoSoHocPhan saveEnrollment(HoSoHocPhan hoSo) {
            if (hoSo.getMaHoSo() == null) {
                hoSo.setMaHoSo(nextHoSoId++);
            }
            enrollments.put(hoSo.getMaHoSo(), hoSo);
            return hoSo;
        }

        @Override
        public void deleteEnrollment(HoSoHocPhan hoSo) {
            if (hoSo != null && hoSo.getMaHoSo() != null) {
                HoSoHocPhan existing = enrollments.get(hoSo.getMaHoSo());
                if (existing != null) {
                    existing.setDeleted(true);
                    existing.setTrangThai(TrangThaiHocPhan.DAHUY);
                }
            }
        }

        @Override
        public List<HoSoHocPhan> findStudentGrades(String maSV) {
            List<HoSoHocPhan> list = new ArrayList<>();
            for (HoSoHocPhan hs : enrollments.values()) {
                if (hs.getSinhVien() != null && maSV.equals(hs.getSinhVien().getMaSV()) && !hs.isDeleted()) {
                    list.add(hs);
                }
            }
            return list;
        }

        @Override
        public List<LichHoc> findStudentSchedule(String maSV, String maHocKy) {
            return schedules.getOrDefault(maSV, Collections.emptyList());
        }

        @Override
        public List<LopHocPhan> findStudentEnrolledClasses(String maSV, String maHocKy) {
            List<LopHocPhan> list = new ArrayList<>();
            for (HoSoHocPhan hs : findRegisteredClasses(maSV)) {
                if (hs.getLopHocPhan() != null) {
                    list.add(hs.getLopHocPhan());
                }
            }
            return list;
        }

        @Override
        public YeuCauMoLop saveClassRequest(YeuCauMoLop request) {
            requests.put(request.getMaYeuCau(), request);
            return request;
        }

        @Override
        public List<YeuCauMoLop> findClassRequestsByStudent(String maSV) {
            return new ArrayList<>(requests.values());
        }

        @Override public Optional<SinhVien> findById(String id) { return findByMaSV(id); }
        @Override public List<SinhVien> findAll() { return new ArrayList<>(students.values()); }
        @Override public SinhVien save(SinhVien entity) { students.put(entity.getMaSV(), entity); return entity; }
        @Override public SinhVien update(SinhVien entity) { students.put(entity.getMaSV(), entity); return entity; }
        @Override public void delete(SinhVien entity) { students.remove(entity.getMaSV()); }
        @Override public void deleteById(String id) { students.remove(id); }
        @Override public long count() { return students.size(); }
    }

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        fakeRepository = new FakeStudentRepository();
        studentService = new StudentServiceImpl(fakeRepository);
        controller = new TestStudentController(studentService);

        // Seed data
        NguoiDung svUser = new NguoiDung();
        svUser.setCccd("001099000001");
        svUser.setHoTen("Nguyá»…n VÄƒn An");

        SinhVien sv = new SinhVien();
        sv.setMaSV("B21DCCN001");
        sv.setNguoiDung(svUser);
        fakeRepository.students.put(sv.getMaSV(), sv);

        HocKy hk = new HocKy();
        hk.setMaHocKy("2025_2026_HK1");
        hk.setTenHocKy(TenHocKy.HOCKY1);
        hk.setThoiGianBD(LocalDate.of(2025, 9, 1));
        hk.setThoiGianKT(LocalDate.of(2026, 1, 15));

        GiangVien gv = new GiangVien();
        gv.setMaNV("GV001");
        NguoiDung gvUser = new NguoiDung();
        gvUser.setHoTen("TS. Tráº§n Thá»‹ BĂ¬nh");
        gv.setNguoiDung(gvUser);

        HocPhan hp = new HocPhan();
        hp.setMaHP("INT1405");
        hp.setTenHP("Ká»¹ nghá»‡ pháº§n má»m");
        hp.setSoTinChi(3);
        fakeRepository.courses.put(hp.getMaHP(), hp);

        HocPhan hp2 = new HocPhan();
        hp2.setMaHP("INT1416");
        hp2.setTenHP("Kiáº¿n trĂºc pháº§n má»m");
        hp2.setSoTinChi(3);
        fakeRepository.courses.put(hp2.getMaHP(), hp2);

        // Open Class 1
        LopHocPhan lhp1 = new LopHocPhan();
        lhp1.setMaLHP("INT1405_01");
        lhp1.setTenLHP("Ká»¹ nghá»‡ pháº§n má»m - Lá»›p 01");
        lhp1.setMonHoc(hp);
        lhp1.setHocKy(hk);
        lhp1.setGiangVien(gv);
        lhp1.setThu(2);
        lhp1.setTietBatDau(1);
        lhp1.setSoTiet(3);
        lhp1.setSiSoHienTai(55);
        lhp1.setSiSoToiDa(60);
        lhp1.setTrangThai(TrangThaiLop.MODANGKY);
        lhp1.setThoiGianBDDK(OffsetDateTime.now().minusDays(2));
        lhp1.setThoiGianKTDK(OffsetDateTime.now().plusDays(5));
        fakeRepository.classes.put(lhp1.getMaLHP(), lhp1);

        // Open Class 2 (Conflicting time with Class 1: Thu 2, Tiet 2-4)
        LopHocPhan lhp2 = new LopHocPhan();
        lhp2.setMaLHP("INT1416_02");
        lhp2.setTenLHP("Kiáº¿n trĂºc pháº§n má»m - Lá»›p 02");
        lhp2.setMonHoc(hp2);
        lhp2.setHocKy(hk);
        lhp2.setGiangVien(gv);
        lhp2.setThu(2);
        lhp2.setTietBatDau(2);
        lhp2.setSoTiet(3);
        lhp2.setSiSoHienTai(58);
        lhp2.setSiSoToiDa(60);
        lhp2.setTrangThai(TrangThaiLop.MODANGKY);
        lhp2.setThoiGianBDDK(OffsetDateTime.now().minusDays(2));
        lhp2.setThoiGianKTDK(OffsetDateTime.now().plusDays(5));
        fakeRepository.classes.put(lhp2.getMaLHP(), lhp2);
    }

    @Test
    @DisplayName("Route 13: GET /student/registration/open-classes Output JSON verification")
    void testGetOpenClasses() throws Exception {
        Response response = controller.getOpenClasses(null, null, 1, 10);
        assertEquals(200, response.getStatus());

        String json = objectMapper.writeValueAsString(response.getEntity());
        System.out.println("Route 13 Controller JSON: " + json);

        assertTrue(json.contains("\"status\":200"));
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("\"maLHP\":\"INT1405_01\""));
        assertTrue(json.contains("\"INT1405\""));
        assertTrue(json.contains("\"page\":1"));
        assertTrue(json.contains("\"totalItems\":2"));
    }

    @Test
    @DisplayName("Route 15: POST /student/registration/enroll Output JSON verification & Conflict Detection")
    void testEnrollCourseSuccessAndConflict() throws Exception {
        // Step 1: Enroll in INT1405_01 -> 201 Created
        EnrollCourseRequestDTO req = new EnrollCourseRequestDTO("INT1405_01");
        Response res1 = controller.enrollCourse(req);
        assertEquals(201, res1.getStatus());

        String json1 = objectMapper.writeValueAsString(res1.getEntity());
        System.out.println("Route 15 Success JSON: " + json1);
        assertTrue(json1.contains("\"status\":201"));
        assertTrue(json1.contains("\"success\":true"));
        assertTrue(json1.contains("\"maLHP\":\"INT1405_01\""));

        // Step 2: Attempt to enroll in INT1416_02 (conflicting time on Thu 2) -> 409 Conflict
        EnrollCourseRequestDTO conflictReq = new EnrollCourseRequestDTO("INT1416_02");
        Response resConflict = controller.enrollCourse(conflictReq);
        assertEquals(409, resConflict.getStatus());

        String jsonConflict = objectMapper.writeValueAsString(resConflict.getEntity());
        System.out.println("Route 15 Conflict JSON: " + jsonConflict);
        assertTrue(jsonConflict.contains("\"success\":false"));
        assertTrue(jsonConflict.contains("\"success\":false"));
    }

    @Test
    @DisplayName("Route 14: GET /student/registration/registered-classes Output JSON verification")
    void testGetRegisteredClasses() throws Exception {
        // Enroll student in class first
        EnrollCourseRequestDTO req = new EnrollCourseRequestDTO("INT1405_01");
        controller.enrollCourse(req);

        Response response = controller.getRegisteredClasses();
        assertEquals(200, response.getStatus());

        String json = objectMapper.writeValueAsString(response.getEntity());
        System.out.println("Route 14 Controller JSON: " + json);

        assertTrue(json.contains("\"status\":200"));
        assertTrue(json.contains("\"tongSoLopDaDangKy\":1"));
        assertTrue(json.contains("\"tongSoTinChiDaDangKy\":3"));
        assertTrue(json.contains("\"hanMucTinChiToiDa\":24"));
    }

    @Test
    @DisplayName("Route 16: DELETE /student/registration/cancel/{maHoSo} Output JSON verification")
    void testCancelEnrollment() throws Exception {
        // Enroll first
        EnrollCourseRequestDTO req = new EnrollCourseRequestDTO("INT1405_01");
        Response resEnroll = controller.enrollCourse(req);
        ApiResponse<?> enrollResp = (ApiResponse<?>) resEnroll.getEntity();
        EnrollCourseResponse enrollData = (EnrollCourseResponse) enrollResp.getData();
        Long maHoSo = enrollData.getMaHoSo();

        // Cancel
        Response resCancel = controller.cancelEnrollment(maHoSo);
        assertEquals(200, resCancel.getStatus());

        String json = objectMapper.writeValueAsString(resCancel.getEntity());
        System.out.println("Route 16 Controller JSON: " + json);

        assertTrue(json.contains("\"status\":200"));
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("\"maHoSo\":" + maHoSo));
        assertTrue(json.contains("\"trangThai\":\"DAHUY\""));
    }

    @Test
    @DisplayName("Route 17: POST /student/registration/open-request Output JSON verification")
    void testRequestOpenCourse() throws Exception {
        OpenClassRequestRequestDTO req = new OpenClassRequestRequestDTO(
                "INT1405",
                "Cáº§n hoĂ n thĂ nh tĂ­n chá»‰ báº¯t buá»™c Ä‘á»ƒ ká»‹p tiáº¿n Ä‘á»™ tá»‘t nghiá»‡p",
                "Thá»© 4 (Tiáº¿t 1-3) hoáº·c Thá»© 6 (Tiáº¿t 7-9)"
        );

        Response response = controller.requestOpenCourse(req);
        assertEquals(201, response.getStatus());

        String json = objectMapper.writeValueAsString(response.getEntity());
        System.out.println("Route 17 Controller JSON: " + json);

        assertTrue(json.contains("\"status\":201"));
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("\"trangThai\":\"DAGUI\""));
        assertTrue(json.contains("YC-"));
    }

    @Test
    @DisplayName("Route 12: GET /student/schedule Output JSON verification")
    void testGetSchedule() throws Exception {
        // Enroll first
        EnrollCourseRequestDTO req = new EnrollCourseRequestDTO("INT1405_01");
        controller.enrollCourse(req);

        Response response = controller.getSchedule("2025_2026_HK1");
        assertEquals(200, response.getStatus());

        String json = objectMapper.writeValueAsString(response.getEntity());
        System.out.println("Route 12 Controller JSON: " + json);

        assertTrue(json.contains("\"status\":200"));
        assertTrue(json.contains("\"maLHP\":\"INT1405_01\""));
        assertTrue(json.contains("\"tietBD\":1"));
        assertTrue(json.contains("\"maHocKyHienTai\":\"2025_2026_HK1\""));
    }

    @Test
    @DisplayName("Route 11: GET /student/grades Output JSON verification")
    void testGetGrades() throws Exception {
        // Setup a graded course
        HocKy hk = fakeRepository.classes.get("INT1405_01").getHocKy();
        LopHocPhan lhp = fakeRepository.classes.get("INT1405_01");
        SinhVien sv = fakeRepository.students.get("B21DCCN001");

        HoSoHocPhan hs = new HoSoHocPhan();
        hs.setSinhVien(sv);
        hs.setLopHocPhan(lhp);
        hs.setDiemQuaTrinh(new BigDecimal("8.5"));
        hs.setDiemCuoiKy(new BigDecimal("8.0"));
        hs.setDiemTongKet(new BigDecimal("8.2"));
        hs.setKetQua(KetQua.B_PLUS);
        hs.setTrangThai(TrangThaiHocPhan.DANGHOC);
        fakeRepository.saveEnrollment(hs);

        Response response = controller.getGrades();
        assertEquals(200, response.getStatus());

        String json = objectMapper.writeValueAsString(response.getEntity());
        System.out.println("Route 11 Controller JSON: " + json);

        assertTrue(json.contains("\"status\":200"));
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("\"maHP\":\"INT1405\""));
        assertTrue(json.contains("\"diemTBHK\":3.5"));
        assertTrue(json.contains("\"diemTBTichLuy\":3.5"));
        assertTrue(json.contains("\"soTinChiTichLuy\":3"));
        assertTrue(json.contains("\"diemTBTichLuy\":3.5"));
    }
}

