package com.academic;

import com.university.core.model.GiangVien;
import com.university.core.model.NguoiDung;
import com.university.core.model.NhanVien;
import com.university.core.model.SinhVien;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.TruongPhongDaoTao;
import com.university.core.util.JwtUtil;
import com.university.core.util.PasswordUtil;
import com.university.modules.auth.AuthRepository;
import com.university.modules.auth.AuthResult;
import com.university.modules.auth.AuthServiceImpl;
import com.university.modules.auth.requestDTO.AdminLoginRequestDTO;
import com.university.modules.auth.requestDTO.LoginRequestDTO;
import com.university.modules.auth.responseDTO.AdminLoginResponseDTO;
import com.university.modules.auth.responseDTO.LoginResponseDTO;
import com.university.modules.auth.responseDTO.RefreshTokenResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private FakeAuthRepository fakeRepository;
    private AuthServiceImpl authService;

    @BeforeEach
    public void setup() {
        fakeRepository = new FakeAuthRepository();
        authService = new AuthServiceImpl(fakeRepository);

        // Seed a student user
        NguoiDung svUser = new NguoiDung();
        svUser.setCccd("001099000001");
        svUser.setEmail("sinhvien01@ptit.edu.vn");
        svUser.setHoTen("Nguyễn Văn An");
        svUser.setNgaySinh(LocalDate.of(2003, 5, 15));
        svUser.setMatKhauHashed(PasswordUtil.hash("Password@123"));
        svUser.setAnhThe("https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg");
        fakeRepository.saveUser(svUser);

        SinhVien sv = new SinhVien();
        sv.setMaSV("B21DCCN001");
        sv.setNguoiDung(svUser);
        sv.setNienKhoa("2021-2025");
        sv.setKhoa("CNTT");
        sv.setChuyenNganh("KTPM");
        fakeRepository.saveStudent(sv);

        // Seed a lecturer user
        NguoiDung gvUser = new NguoiDung();
        gvUser.setCccd("001088000002");
        gvUser.setEmail("canbo.daotao@ptit.edu.vn");
        gvUser.setHoTen("TS. Trần Thị Bình");
        gvUser.setNgaySinh(LocalDate.of(1985, 3, 20));
        gvUser.setMatKhauHashed(PasswordUtil.hash("Password@123"));
        gvUser.setAnhThe("https://ptit.edu.vn/storage/avatars/GV012.jpg");
        fakeRepository.saveUser(gvUser);

        GiangVien gv = new GiangVien();
        gv.setMaNV("GV012");
        gv.setNguoiDung(gvUser);
        gv.setKhoa("CNTT");
        gv.setHocVi("Tiến sĩ");
        fakeRepository.saveLecturer(gv);

        // Seed an assistant user
        NguoiDung tlUser = new NguoiDung();
        tlUser.setCccd("001088000003");
        tlUser.setEmail("troly.khoa@ptit.edu.vn");
        tlUser.setHoTen("ThS. Lê Hoàng Long");
        tlUser.setNgaySinh(LocalDate.of(1990, 8, 10));
        tlUser.setMatKhauHashed(PasswordUtil.hash("Password@123"));
        fakeRepository.saveUser(tlUser);

        TroLyDaoTao tl = new TroLyDaoTao();
        tl.setMaNV("TL005");
        tl.setNguoiDung(tlUser);
        tl.setNgayBatDau(LocalDate.of(2020, 1, 1));
        fakeRepository.saveAssistant(tl);

        // Seed a head office user
        NguoiDung tpUser = new NguoiDung();
        tpUser.setCccd("001088000004");
        tpUser.setEmail("truongphong@ptit.edu.vn");
        tpUser.setHoTen("PGS.TS. Phạm Minh Tuấn");
        tpUser.setNgaySinh(LocalDate.of(1975, 12, 1));
        tpUser.setMatKhauHashed(PasswordUtil.hash("Password@123"));
        fakeRepository.saveUser(tpUser);

        TruongPhongDaoTao tp = new TruongPhongDaoTao();
        tp.setMaNV("TP001");
        tp.setNguoiDung(tpUser);
        tp.setNhiemKy("2021-2026");
        fakeRepository.saveHeadOffice(tp);
    }

    @Test
    public void testStudentLoginSuccess() {
        LoginRequestDTO req = new LoginRequestDTO("sinhvien01@ptit.edu.vn", "Password@123");
        AuthResult<LoginResponseDTO> result = authService.login(req);

        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatusCode());
        assertEquals("Đăng nhập thành công", result.getMessage());
        assertNotNull(result.getData());
        assertNotNull(result.getData().getAccessToken());
        assertEquals("B21DCCN001", result.getData().getUser().getMaSV());
        assertEquals("Nguyễn Văn An", result.getData().getUser().getHoTen());
        assertEquals("SINHVIEN", result.getData().getUser().getRole());
        assertNotNull(result.getRefreshToken());

        // Refresh token must be saved to user entity
        assertEquals(result.getRefreshToken(), fakeRepository.findByCccd("001099000001").get().getRefreshToken());
    }

    @Test
    public void testStudentLoginWrongPassword() {
        LoginRequestDTO req = new LoginRequestDTO("sinhvien01@ptit.edu.vn", "WrongPassword");
        AuthResult<LoginResponseDTO> result = authService.login(req);

        assertFalse(result.isSuccess());
        assertEquals(401, result.getStatusCode());
        assertEquals("Sai thông tin đăng nhập", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    public void testStaffLoginOnStudentPortalRejected() {
        LoginRequestDTO req = new LoginRequestDTO("canbo.daotao@ptit.edu.vn", "Password@123");
        AuthResult<LoginResponseDTO> result = authService.login(req);

        assertFalse(result.isSuccess());
        assertEquals(403, result.getStatusCode());
        assertTrue(result.getMessage().contains("cổng quản trị"));
    }

    @Test
    public void testAdminLoginLecturerSuccess() {
        AdminLoginRequestDTO req = new AdminLoginRequestDTO("canbo.daotao@ptit.edu.vn", "Password@123");
        AuthResult<AdminLoginResponseDTO> result = authService.adminLogin(req);

        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatusCode());
        assertEquals("Đăng nhập cán bộ thành công", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("GV012", result.getData().getUser().getMaNV());
        assertEquals("GIANGVIEN", result.getData().getUser().getRole());
        assertNotNull(result.getRefreshToken());
    }

    @Test
    public void testAdminLoginAssistantSuccess() {
        AdminLoginRequestDTO req = new AdminLoginRequestDTO("troly.khoa@ptit.edu.vn", "Password@123");
        AuthResult<AdminLoginResponseDTO> result = authService.adminLogin(req);

        assertTrue(result.isSuccess());
        assertEquals("TL005", result.getData().getUser().getMaNV());
        assertEquals("TROLY", result.getData().getUser().getRole());
    }

    @Test
    public void testAdminLoginHeadOfficeSuccess() {
        AdminLoginRequestDTO req = new AdminLoginRequestDTO("truongphong@ptit.edu.vn", "Password@123");
        AuthResult<AdminLoginResponseDTO> result = authService.adminLogin(req);

        assertTrue(result.isSuccess());
        assertEquals("TP001", result.getData().getUser().getMaNV());
        assertEquals("TRUONGPHONG", result.getData().getUser().getRole());
    }

    @Test
    public void testStudentLoginOnAdminPortalRejected() {
        AdminLoginRequestDTO req = new AdminLoginRequestDTO("sinhvien01@ptit.edu.vn", "Password@123");
        AuthResult<AdminLoginResponseDTO> result = authService.adminLogin(req);

        assertFalse(result.isSuccess());
        assertEquals(403, result.getStatusCode());
        assertTrue(result.getMessage().contains("Tài khoản sinh viên không có quyền truy cập cổng quản trị"));
    }

    @Test
    public void testRefreshTokenSuccess() {
        // First login
        LoginRequestDTO loginReq = new LoginRequestDTO("sinhvien01@ptit.edu.vn", "Password@123");
        AuthResult<LoginResponseDTO> loginRes = authService.login(loginReq);
        String refreshToken = loginRes.getRefreshToken();

        // Refresh token
        AuthResult<RefreshTokenResponseDTO> refreshRes = authService.refreshToken(refreshToken);
        assertTrue(refreshRes.isSuccess());
        assertEquals(200, refreshRes.getStatusCode());
        assertNotNull(refreshRes.getData().getAccessToken());
        assertNotNull(refreshRes.getRefreshToken());

        // Validate new access token
        assertEquals("001099000001", JwtUtil.extractCccd(refreshRes.getData().getAccessToken()));
        assertEquals("B21DCCN001", JwtUtil.extractUserCode(refreshRes.getData().getAccessToken()));
        assertEquals("SINHVIEN", JwtUtil.extractRole(refreshRes.getData().getAccessToken()));

        // Old refresh token must be rotated
        assertNotEquals(refreshToken, fakeRepository.findByCccd("001099000001").get().getRefreshToken());
    }

    @Test
    public void testRefreshTokenWithInvalidTokenFails() {
        AuthResult<RefreshTokenResponseDTO> res = authService.refreshToken("invalid.jwt.token");
        assertFalse(res.isSuccess());
        assertEquals(401, res.getStatusCode());
    }

    @Test
    public void testLogoutClearsRefreshTokenInDb() {
        // Login
        LoginRequestDTO loginReq = new LoginRequestDTO("sinhvien01@ptit.edu.vn", "Password@123");
        AuthResult<LoginResponseDTO> loginRes = authService.login(loginReq);
        assertNotNull(fakeRepository.findByCccd("001099000001").get().getRefreshToken());

        // Logout
        authService.logout("Bearer " + loginRes.getData().getAccessToken(), loginRes.getRefreshToken());

        // Token must be null in db
        assertNull(fakeRepository.findByCccd("001099000001").get().getRefreshToken());
    }

    // In-memory fake repository for lightning-fast testing
    static class FakeAuthRepository implements AuthRepository {
        private final Map<String, NguoiDung> usersByCccd = new HashMap<>();
        private final Map<String, NguoiDung> usersByEmail = new HashMap<>();
        private final Map<String, SinhVien> studentsByCccd = new HashMap<>();
        private final Map<String, NhanVien> staffByCccd = new HashMap<>();
        private final Map<String, GiangVien> lecturersByCccd = new HashMap<>();
        private final Map<String, TroLyDaoTao> assistantsByCccd = new HashMap<>();
        private final Map<String, TruongPhongDaoTao> headOfficesByCccd = new HashMap<>();

        public void saveUser(NguoiDung user) {
            usersByCccd.put(user.getCccd(), user);
            usersByEmail.put(user.getEmail().toLowerCase(), user);
        }

        public void saveStudent(SinhVien sv) {
            studentsByCccd.put(sv.getNguoiDung().getCccd(), sv);
        }

        public void saveLecturer(GiangVien gv) {
            staffByCccd.put(gv.getNguoiDung().getCccd(), gv);
            lecturersByCccd.put(gv.getNguoiDung().getCccd(), gv);
        }

        public void saveAssistant(TroLyDaoTao tl) {
            staffByCccd.put(tl.getNguoiDung().getCccd(), tl);
            assistantsByCccd.put(tl.getNguoiDung().getCccd(), tl);
        }

        public void saveHeadOffice(TruongPhongDaoTao tp) {
            staffByCccd.put(tp.getNguoiDung().getCccd(), tp);
            headOfficesByCccd.put(tp.getNguoiDung().getCccd(), tp);
        }

        @Override
        public Optional<NguoiDung> findByEmail(String email) {
            return Optional.ofNullable(usersByEmail.get(email.toLowerCase()));
        }

        @Override
        public Optional<NguoiDung> findByCccd(String cccd) {
            return Optional.ofNullable(usersByCccd.get(cccd));
        }

        @Override
        public Optional<NguoiDung> findByRefreshToken(String refreshToken) {
            return usersByCccd.values().stream()
                    .filter(u -> refreshToken.equals(u.getRefreshToken()))
                    .findFirst();
        }

        @Override
        public boolean updateRefreshToken(String cccd, String refreshToken) {
            NguoiDung u = usersByCccd.get(cccd);
            if (u != null) {
                u.setRefreshToken(refreshToken);
                return true;
            }
            return false;
        }

        @Override
        public boolean clearRefreshToken(String cccd) {
            NguoiDung u = usersByCccd.get(cccd);
            if (u != null) {
                u.setRefreshToken(null);
                return true;
            }
            return false;
        }

        @Override
        public boolean updatePassword(String cccd, String newHashedPassword) {
            NguoiDung u = usersByCccd.get(cccd);
            if (u != null) {
                u.setMatKhauHashed(newHashedPassword);
                return true;
            }
            return false;
        }

        @Override
        public Optional<SinhVien> findStudentByCccd(String cccd) {
            return Optional.ofNullable(studentsByCccd.get(cccd));
        }

        @Override
        public Optional<NhanVien> findStaffByCccd(String cccd) {
            return Optional.ofNullable(staffByCccd.get(cccd));
        }

        @Override
        public Optional<GiangVien> findLecturerByCccd(String cccd) {
            return Optional.ofNullable(lecturersByCccd.get(cccd));
        }

        @Override
        public Optional<TroLyDaoTao> findAssistantByCccd(String cccd) {
            return Optional.ofNullable(assistantsByCccd.get(cccd));
        }

        @Override
        public Optional<TruongPhongDaoTao> findHeadOfficeByCccd(String cccd) {
            return Optional.ofNullable(headOfficesByCccd.get(cccd));
        }

        @Override
        public Optional<NguoiDung> findById(String id) {
            return findByCccd(id);
        }

        @Override
        public List<NguoiDung> findAll() {
            return new ArrayList<>(usersByCccd.values());
        }

        @Override
        public NguoiDung save(NguoiDung entity) {
            saveUser(entity);
            return entity;
        }

        @Override
        public NguoiDung update(NguoiDung entity) {
            saveUser(entity);
            return entity;
        }

        @Override
        public void delete(NguoiDung entity) {
            if (entity != null) {
                usersByCccd.remove(entity.getCccd());
                usersByEmail.remove(entity.getEmail().toLowerCase());
            }
        }

        @Override
        public void deleteById(String id) {
            NguoiDung u = usersByCccd.remove(id);
            if (u != null) {
                usersByEmail.remove(u.getEmail().toLowerCase());
            }
        }

        @Override
        public long count() {
            return usersByCccd.size();
        }
    }
}
