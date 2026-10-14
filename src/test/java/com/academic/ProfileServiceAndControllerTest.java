package com.academic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.core.model.*;
import com.university.core.util.PasswordUtil;
import com.university.core.common.ApiResponse;
import com.university.modules.profile.*;
import com.university.modules.profile.ProfileRequestDTO.ChangePasswordRequestDTO;
import com.university.modules.profile.ProfileResponseDTO.UserProfileResponseDTO;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileServiceAndControllerTest {

    private FakeProfileRepository fakeRepository;
    private ProfileServiceImpl profileService;
    private TestProfileController controller;
    private ObjectMapper objectMapper;

    static class TestProfileController extends ProfileController {
        private String mockCccd;
        private String mockUserCode;
        private String mockRole;

        public TestProfileController(ProfileService profileService) {
            super(profileService);
        }

        public void setMockContext(String cccd, String userCode, String role) {
            this.mockCccd = cccd;
            this.mockUserCode = userCode;
            this.mockRole = role;
        }

        @Override
        protected String getAuthenticatedUserCccd() {
            return mockCccd;
        }

        @Override
        protected String getAuthenticatedUserCode() {
            return mockUserCode;
        }

        @Override
        protected String getAuthenticatedRole() {
            return mockRole;
        }
    }

    static class FakeProfileRepository implements ProfileRepository {
        Map<String, NguoiDung> users = new HashMap<>();
        Map<String, SinhVien> studentsByCccd = new HashMap<>();
        Map<String, SinhVien> studentsByMaSV = new HashMap<>();
        Map<String, GiangVien> lecturersByCccd = new HashMap<>();
        Map<String, GiangVien> lecturersByMaNV = new HashMap<>();
        Map<String, TroLyDaoTao> assistantsByCccd = new HashMap<>();
        Map<String, TroLyDaoTao> assistantsByMaNV = new HashMap<>();
        Map<String, TruongPhongDaoTao> headOfficesByCccd = new HashMap<>();
        Map<String, TruongPhongDaoTao> headOfficesByMaNV = new HashMap<>();
        Map<String, NhanVien> staffByCccd = new HashMap<>();
        Map<String, NhanVien> staffByMaNV = new HashMap<>();
        Map<String, List<HoSoHocPhan>> studentGrades = new HashMap<>();

        @Override
        public Optional<NguoiDung> findUserByCccd(String cccd) {
            return Optional.ofNullable(users.get(cccd));
        }

        @Override
        public Optional<NguoiDung> findUserByEmail(String email) {
            return users.values().stream()
                    .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        }

        @Override
        public Optional<SinhVien> findStudentByCccd(String cccd) {
            return Optional.ofNullable(studentsByCccd.get(cccd));
        }

        @Override
        public Optional<SinhVien> findStudentByMaSV(String maSV) {
            return Optional.ofNullable(studentsByMaSV.get(maSV));
        }

        @Override
        public Optional<GiangVien> findLecturerByCccd(String cccd) {
            return Optional.ofNullable(lecturersByCccd.get(cccd));
        }

        @Override
        public Optional<GiangVien> findLecturerByMaNV(String maNV) {
            return Optional.ofNullable(lecturersByMaNV.get(maNV));
        }

        @Override
        public Optional<TroLyDaoTao> findAssistantByCccd(String cccd) {
            return Optional.ofNullable(assistantsByCccd.get(cccd));
        }

        @Override
        public Optional<TroLyDaoTao> findAssistantByMaNV(String maNV) {
            return Optional.ofNullable(assistantsByMaNV.get(maNV));
        }

        @Override
        public Optional<TruongPhongDaoTao> findHeadOfficeByCccd(String cccd) {
            return Optional.ofNullable(headOfficesByCccd.get(cccd));
        }

        @Override
        public Optional<TruongPhongDaoTao> findHeadOfficeByMaNV(String maNV) {
            return Optional.ofNullable(headOfficesByMaNV.get(maNV));
        }

        @Override
        public Optional<NhanVien> findStaffByCccd(String cccd) {
            return Optional.ofNullable(staffByCccd.get(cccd));
        }

        @Override
        public Optional<NhanVien> findStaffByMaNV(String maNV) {
            return Optional.ofNullable(staffByMaNV.get(maNV));
        }

        @Override
        public List<HoSoHocPhan> findStudentGradeRecords(String maSV) {
            return studentGrades.getOrDefault(maSV, Collections.emptyList());
        }

        @Override
        public boolean updatePassword(String cccd, String newHashedPassword) {
            NguoiDung u = users.get(cccd);
            if (u != null) {
                u.setMatKhauHashed(newHashedPassword);
                u.setRefreshToken(null);
                return true;
            }
            return false;
        }

        @Override
        public boolean updateContactInfo(String cccd, String soDienThoai, String diaChi) {
            NguoiDung u = users.get(cccd);
            if (u != null) {
                u.setSoDienThoai(soDienThoai);
                u.setDiaChi(diaChi);
                return true;
            }
            return false;
        }

        @Override
        public Optional<NguoiDung> findById(String id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public List<NguoiDung> findAll() {
            return new ArrayList<>(users.values());
        }

        @Override
        public NguoiDung save(NguoiDung entity) {
            users.put(entity.getCccd(), entity);
            return entity;
        }

        @Override
        public NguoiDung update(NguoiDung entity) {
            users.put(entity.getCccd(), entity);
            return entity;
        }

        @Override
        public void delete(NguoiDung entity) {
            users.remove(entity.getCccd());
        }

        @Override
        public void deleteById(String id) {
            users.remove(id);
        }

        @Override
        public long count() {
            return users.size();
        }
    }

    @BeforeEach
    void setUp() {
        fakeRepository = new FakeProfileRepository();
        profileService = new ProfileServiceImpl(fakeRepository);
        controller = new TestProfileController(profileService);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 1. Sinh viên mẫu
        NguoiDung svUser = new NguoiDung();
        svUser.setCccd("001203000001");
        svUser.setHoTen("Nguyễn Văn An");
        svUser.setEmail("sinhvien01@ptit.edu.vn");
        svUser.setNgaySinh(LocalDate.of(2003, 5, 15));
        svUser.setGioiTinh("Nam");
        svUser.setSoDienThoai("0912345678");
        svUser.setDiaChi("Hà Nội");
        svUser.setAnhThe("https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg");
        svUser.setRefreshToken("active_refresh_token_abc");
        svUser.setMatKhauHashed(PasswordUtil.hash("123456"));
        fakeRepository.users.put(svUser.getCccd(), svUser);

        SinhVien sv = new SinhVien();
        sv.setMaSV("B21DCCN001");
        sv.setNguoiDung(svUser);
        sv.setNienKhoa("2021-2025");
        sv.setKhoa("Công nghệ thông tin 1");
        sv.setChuyenNganh("Kỹ nghệ phần mềm");
        fakeRepository.studentsByCccd.put(svUser.getCccd(), sv);
        fakeRepository.studentsByMaSV.put(sv.getMaSV(), sv);

        // Hồ sơ điểm của sinh viên
        HocPhan hp1 = new HocPhan();
        hp1.setMaHP("INT1405");
        hp1.setTenHP("Kỹ nghệ phần mềm");
        hp1.setSoTinChi(3);

        LopHocPhan lhp1 = new LopHocPhan();
        lhp1.setMaLHP("INT1405_01");
        lhp1.setMonHoc(hp1);

        HoSoHocPhan hs1 = new HoSoHocPhan();
        hs1.setSinhVien(sv);
        hs1.setLopHocPhan(lhp1);
        hs1.setDiemTongKet(BigDecimal.valueOf(8.5)); // GPA 4.0

        HocPhan hp2 = new HocPhan();
        hp2.setMaHP("INT1306");
        hp2.setTenHP("Cấu trúc dữ liệu và giải thuật");
        hp2.setSoTinChi(4);

        LopHocPhan lhp2 = new LopHocPhan();
        lhp2.setMaLHP("INT1306_01");
        lhp2.setMonHoc(hp2);

        HoSoHocPhan hs2 = new HoSoHocPhan();
        hs2.setSinhVien(sv);
        hs2.setLopHocPhan(lhp2);
        hs2.setDiemTongKet(BigDecimal.valueOf(7.0)); // GPA 3.0

        fakeRepository.studentGrades.put(sv.getMaSV(), Arrays.asList(hs1, hs2));

        // 2. Giảng viên mẫu
        NguoiDung gvUser = new NguoiDung();
        gvUser.setCccd("001203000002");
        gvUser.setHoTen("TS. Trần Thị Bình");
        gvUser.setEmail("gv.binh@ptit.edu.vn");
        gvUser.setNgaySinh(LocalDate.of(1985, 8, 20));
        gvUser.setGioiTinh("Nữ");
        gvUser.setSoDienThoai("0987654321");
        gvUser.setAnhThe("https://ptit.edu.vn/storage/avatars/GV012.jpg");
        gvUser.setMatKhauHashed(PasswordUtil.hash("123456"));
        fakeRepository.users.put(gvUser.getCccd(), gvUser);

        GiangVien gv = new GiangVien();
        gv.setMaNV("GV012");
        gv.setNguoiDung(gvUser);
        gv.setHocVi("Tiến sĩ");
        gv.setKhoa("Công nghệ thông tin 1");
        gv.setNgayBatDau(LocalDate.of(2015, 9, 1));
        fakeRepository.lecturersByCccd.put(gvUser.getCccd(), gv);
        fakeRepository.lecturersByMaNV.put(gv.getMaNV(), gv);
        fakeRepository.staffByCccd.put(gvUser.getCccd(), gv);
        fakeRepository.staffByMaNV.put(gv.getMaNV(), gv);

        // 3. Trợ lý đào tạo mẫu
        NguoiDung tlUser = new NguoiDung();
        tlUser.setCccd("001203000003");
        tlUser.setHoTen("ThS. Lê Hoàng Long");
        tlUser.setEmail("troly.long@ptit.edu.vn");
        tlUser.setNgaySinh(LocalDate.of(1990, 11, 10));
        tlUser.setGioiTinh("Nam");
        tlUser.setSoDienThoai("0977112233");
        tlUser.setAnhThe("https://ptit.edu.vn/storage/avatars/TL001.jpg");
        tlUser.setMatKhauHashed(PasswordUtil.hash("123456"));
        fakeRepository.users.put(tlUser.getCccd(), tlUser);

        TroLyDaoTao tl = new TroLyDaoTao();
        tl.setMaNV("TL001");
        tl.setNguoiDung(tlUser);
        tl.setNgayBatDau(LocalDate.of(2020, 3, 15));
        fakeRepository.assistantsByCccd.put(tlUser.getCccd(), tl);
        fakeRepository.assistantsByMaNV.put(tl.getMaNV(), tl);
        fakeRepository.staffByCccd.put(tlUser.getCccd(), tl);
        fakeRepository.staffByMaNV.put(tl.getMaNV(), tl);

        // 4. Trưởng phòng đào tạo mẫu
        NguoiDung tpUser = new NguoiDung();
        tpUser.setCccd("001203000004");
        tpUser.setHoTen("PGS.TS Phạm Văn Đức");
        tpUser.setEmail("truongphong.duc@ptit.edu.vn");
        tpUser.setNgaySinh(LocalDate.of(1975, 4, 12));
        tpUser.setGioiTinh("Nam");
        tpUser.setSoDienThoai("0903456789");
        tpUser.setAnhThe("https://ptit.edu.vn/storage/avatars/TP001.jpg");
        tpUser.setMatKhauHashed(PasswordUtil.hash("123456"));
        fakeRepository.users.put(tpUser.getCccd(), tpUser);

        TruongPhongDaoTao tp = new TruongPhongDaoTao();
        tp.setMaNV("TP001");
        tp.setNguoiDung(tpUser);
        tp.setNhiemKy("2021-2026");
        fakeRepository.headOfficesByCccd.put(tpUser.getCccd(), tp);
        fakeRepository.headOfficesByMaNV.put(tp.getMaNV(), tp);
        fakeRepository.staffByCccd.put(tpUser.getCccd(), tp);
        fakeRepository.staffByMaNV.put(tp.getMaNV(), tp);
    }

    @Test
    @DisplayName("Test 1: Lấy Profile Sinh viên thành công kèm GPA và Tín chỉ tích lũy (Route 05)")
    void testGetStudentProfileSuccess() {
        controller.setMockContext("001203000001", "B21DCCN001", "SINHVIEN");
        Response response = controller.getProfile();

        assertEquals(200, response.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(200, body.getStatus());
        assertEquals("Thông tin người dùng chi tiết", body.getMessage());

        UserProfileResponseDTO profile = (UserProfileResponseDTO) body.getData();
        assertEquals("B21DCCN001", profile.getMaDinhDanh());
        assertEquals("Nguyễn Văn An", profile.getHoTen());
        assertEquals("SINHVIEN", profile.getRole());
        assertEquals("2003-05-15", profile.getNgaySinh());
        assertNotNull(profile.getThongTinHocVu());
        assertEquals(7, profile.getThongTinHocVu().getSoTinChiTichLuy()); // 3 + 4 = 7
        // GPA = (4.0*3 + 3.0*4) / 7 = 24.0 / 7 = 3.43
        assertEquals(3.43, profile.getThongTinHocVu().getGpaHienTai());
        assertNull(profile.getThongTinCanBo());
    }

    @Test
    @DisplayName("Test 2: Lấy Profile Giảng viên thành công (Route 05)")
    void testGetLecturerProfileSuccess() {
        controller.setMockContext("001203000002", "GV012", "GIANGVIEN");
        Response response = controller.getProfile();

        assertEquals(200, response.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(200, body.getStatus());

        UserProfileResponseDTO profile = (UserProfileResponseDTO) body.getData();
        assertEquals("GV012", profile.getMaDinhDanh());
        assertEquals("TS. Trần Thị Bình", profile.getHoTen());
        assertEquals("GIANGVIEN", profile.getRole());
        assertNull(profile.getThongTinHocVu());
        assertNotNull(profile.getThongTinCanBo());
        assertEquals("Tiến sĩ", profile.getThongTinCanBo().getHocVi());
        assertEquals("Công nghệ thông tin 1", profile.getThongTinCanBo().getKhoa());
    }

    @Test
    @DisplayName("Test 3: Lấy Profile Trợ lý đào tạo thành công (Route 05)")
    void testGetAssistantProfileSuccess() {
        controller.setMockContext("001203000003", "TL001", "TROLY");
        Response response = controller.getProfile();

        assertEquals(200, response.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(200, body.getStatus());

        UserProfileResponseDTO profile = (UserProfileResponseDTO) body.getData();
        assertEquals("TL001", profile.getMaDinhDanh());
        assertEquals("ThS. Lê Hoàng Long", profile.getHoTen());
        assertEquals("TROLY", profile.getRole());
        assertNull(profile.getThongTinHocVu());
        assertNotNull(profile.getThongTinCanBo());
        assertEquals("2020-03-15", profile.getThongTinCanBo().getNgayBatDau());
    }

    @Test
    @DisplayName("Test 4: Lấy Profile Trưởng phòng đào tạo thành công (Route 05)")
    void testGetHeadOfficeProfileSuccess() {
        controller.setMockContext("001203000004", "TP001", "TRUONGPHONG");
        Response response = controller.getProfile();

        assertEquals(200, response.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(200, body.getStatus());

        UserProfileResponseDTO profile = (UserProfileResponseDTO) body.getData();
        assertEquals("TP001", profile.getMaDinhDanh());
        assertEquals("PGS.TS Phạm Văn Đức", profile.getHoTen());
        assertEquals("TRUONGPHONG", profile.getRole());
        assertNull(profile.getThongTinHocVu());
        assertNotNull(profile.getThongTinCanBo());
        assertEquals("2021-2026", profile.getThongTinCanBo().getNhiemKy());
    }

    @Test
    @DisplayName("Test 5: Đổi mật khẩu thành công với mật khẩu cũ đúng (Route 06)")
    void testChangePasswordSuccess() {
        controller.setMockContext("001203000001", "B21DCCN001", "SINHVIEN");
        ChangePasswordRequestDTO req = new ChangePasswordRequestDTO("123456", "NewPass@456");

        Response response = controller.changePassword(req);

        assertEquals(200, response.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(200, body.getStatus());
        assertEquals("Đổi mật khẩu thành công", body.getMessage());
        assertNull(body.getData());

        // Kiểm tra mật khẩu trong database đã được hash mới
        NguoiDung u = fakeRepository.users.get("001203000001");
        assertTrue(PasswordUtil.verify("NewPass@456", u.getMatKhauHashed()));
        assertFalse(PasswordUtil.verify("123456", u.getMatKhauHashed()));
        assertNull(u.getRefreshToken(), "Refresh token phải bị thu hồi (null) khi đổi mật khẩu");
        assertNotNull(response.getCookies().get("refreshToken"));
        assertEquals(0, response.getCookies().get("refreshToken").getMaxAge());
    }

    @Test
    @DisplayName("Test 6: Đổi mật khẩu thất bại khi mật khẩu cũ sai (HTTP 400) (Route 06)")
    void testChangePasswordWrongOldPassword() {
        controller.setMockContext("001203000001", "B21DCCN001", "SINHVIEN");
        ChangePasswordRequestDTO req = new ChangePasswordRequestDTO("SaiMatKhau", "NewPass@456");

        Response response = controller.changePassword(req);

        assertEquals(400, response.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(400, body.getStatus());
        assertEquals("Mật khẩu cũ không đúng", body.getMessage());
        assertNull(body.getData());
    }

    @Test
    @DisplayName("Test 7: Đổi mật khẩu thất bại khi để trống trường thông tin (HTTP 400)")
    void testChangePasswordEmptyFields() {
        controller.setMockContext("001203000001", "B21DCCN001", "SINHVIEN");

        Response r1 = controller.changePassword(new ChangePasswordRequestDTO("", "NewPass@456"));
        assertEquals(400, r1.getStatus());

        Response r2 = controller.changePassword(new ChangePasswordRequestDTO("123456", ""));
        assertEquals(400, r2.getStatus());

        Response r3 = controller.changePassword(null);
        assertEquals(400, r3.getStatus());
    }

    @Test
    @DisplayName("Test 8: Đổi mật khẩu thất bại khi mật khẩu mới quá ngắn < 6 ký tự (HTTP 400)")
    void testChangePasswordTooShort() {
        controller.setMockContext("001203000001", "B21DCCN001", "SINHVIEN");
        ChangePasswordRequestDTO req = new ChangePasswordRequestDTO("123456", "123");

        Response response = controller.changePassword(req);

        assertEquals(400, response.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(400, body.getStatus());
        assertEquals("Mật khẩu mới phải có ít nhất 6 ký tự", body.getMessage());
    }

    @Test
    @DisplayName("Test 9: Đổi mật khẩu thất bại khi mật khẩu mới trùng mật khẩu cũ (HTTP 400)")
    void testChangePasswordSameAsOld() {
        controller.setMockContext("001203000001", "B21DCCN001", "SINHVIEN");
        ChangePasswordRequestDTO req = new ChangePasswordRequestDTO("123456", "123456");

        Response response = controller.changePassword(req);

        assertEquals(400, response.getStatus());
        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(400, body.getStatus());
        assertEquals("Mật khẩu mới không được trùng với mật khẩu cũ", body.getMessage());
    }

    @Test
    @DisplayName("Test 10: Người dùng chưa xác thực truy cập API trả về 401 Unauthorized")
    void testUnauthenticatedAccess() {
        controller.setMockContext(null, null, null);

        Response rProfile = controller.getProfile();
        assertEquals(401, rProfile.getStatus());

        Response rPass = controller.changePassword(new ChangePasswordRequestDTO("123456", "NewPass@123"));
        assertEquals(401, rPass.getStatus());
    }

    @Test
    @DisplayName("Test 11: Kiểm thử cấu trúc JSON serialization có trường 'success' chuẩn core ApiResponse")
    void testJsonPayloadMatchesApiRouteContract() throws Exception {
        controller.setMockContext("001203000001", "B21DCCN001", "SINHVIEN");
        Response response = controller.getProfile();
        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();

        String json = objectMapper.writeValueAsString(body);
        System.out.println("Student Profile JSON (With success field): " + json);

        assertTrue(json.contains("\"status\":200"));
        assertTrue(json.contains("\"success\":true"), "JSON output must contain 'success' field!");
        assertTrue(json.contains("\"maDinhDanh\":\"B21DCCN001\""));
        assertTrue(json.contains("\"hoTen\":\"Nguyễn Văn An\""));
        assertTrue(json.contains("\"role\":\"SINHVIEN\""));
        assertTrue(json.contains("\"thongTinHocVu\":{"));
        assertTrue(json.contains("\"soTinChiTichLuy\":7"));
        assertTrue(json.contains("\"gpaHienTai\":3.43"));
        assertFalse(json.contains("\"thongTinCanBo\""));

        // Kiểm tra JSON khi đổi mật khẩu thành công có 'success': true
        Response respPass = controller.changePassword(new ChangePasswordRequestDTO("123456", "NewPass@456"));
        String jsonPass = objectMapper.writeValueAsString(respPass.getEntity());
        System.out.println("Change Password JSON (With success field): " + jsonPass);

        assertTrue(jsonPass.contains("\"status\":200"));
        assertTrue(jsonPass.contains("\"success\":true"), "JSON output must contain 'success' field!");
        assertTrue(jsonPass.contains("\"data\":null"));

        // Kiểm tra JSON khi đổi mật khẩu lỗi 400 có 'success': false
        Response respPassErr = controller.changePassword(new ChangePasswordRequestDTO("WrongPass", "NewPass@456"));
        String jsonPassErr = objectMapper.writeValueAsString(respPassErr.getEntity());
        System.out.println("Change Password 400 JSON (With success field): " + jsonPassErr);

        assertTrue(jsonPassErr.contains("\"status\":400"));
        assertTrue(jsonPassErr.contains("\"success\":false"), "JSON output must contain 'success' field!");
        assertTrue(jsonPassErr.contains("\"data\":null"));
    }
}
