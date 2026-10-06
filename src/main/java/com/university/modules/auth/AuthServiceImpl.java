package com.university.modules.auth;

import com.university.core.model.NguoiDung;
import com.university.core.model.NhanVien;
import com.university.core.model.SinhVien;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.TruongPhongDaoTao;
import com.university.core.model.GiangVien;
import com.university.core.util.JwtUtil;
import com.university.core.util.PasswordUtil;
import com.university.modules.auth.requestDTO.AdminLoginRequestDTO;
import com.university.modules.auth.requestDTO.LoginRequestDTO;
import com.university.modules.auth.responseDTO.AdminLoginResponseDTO;
import com.university.modules.auth.responseDTO.LoginResponseDTO;
import com.university.modules.auth.responseDTO.LogoutResponseDTO;
import com.university.modules.auth.responseDTO.RefreshTokenResponseDTO;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthRepository authRepository;

    public AuthServiceImpl() {
        this.authRepository = new AuthRepositoryImpl();
    }

    public AuthServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public AuthResult<LoginResponseDTO> login(LoginRequestDTO request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return AuthResult.error(400, "Vui lòng nhập đầy đủ email và mật khẩu");
        }

        String email = request.getEmail().trim().toLowerCase();
        Optional<NguoiDung> userOpt = authRepository.findByEmail(email);
        if (userOpt.isEmpty() || userOpt.get().isDeleted()) {
            return AuthResult.error(401, "Sai thông tin đăng nhập");
        }

        NguoiDung user = userOpt.get();
        if (!PasswordUtil.verify(request.getPassword(), user.getMatKhauHashed())) {
            return AuthResult.error(401, "Sai thông tin đăng nhập");
        }

        // Verify Student role
        Optional<SinhVien> studentOpt = authRepository.findStudentByCccd(user.getCccd());
        if (studentOpt.isEmpty()) {
            // Check if user is staff to provide clear error message
            if (authRepository.findStaffByCccd(user.getCccd()).isPresent()) {
                return AuthResult.error(403, "Tài khoản cán bộ vui lòng đăng nhập qua cổng quản trị (/admin/login)");
            }
            return AuthResult.error(403, "Tài khoản không có hồ sơ sinh viên hợp lệ");
        }

        SinhVien student = studentOpt.get();
        String maSV = student.getMaSV();
        String role = "SINHVIEN";
        String hoTen = user.getHoTen();
        String anhThe = user.getAnhThe();

        String accessToken = JwtUtil.generateAccessToken(user.getCccd(), maSV, role, user.getEmail());
        String refreshToken = JwtUtil.generateRefreshToken(user.getCccd());

        authRepository.updateRefreshToken(user.getCccd(), refreshToken);

        LoginResponseDTO responseDTO = new LoginResponseDTO(
                accessToken,
                new LoginResponseDTO.StudentUserDTO(maSV, hoTen, role, anhThe)
        );

        return AuthResult.success(responseDTO, refreshToken, "Đăng nhập thành công");
    }

    @Override
    public AuthResult<AdminLoginResponseDTO> adminLogin(AdminLoginRequestDTO request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return AuthResult.error(400, "Vui lòng nhập đầy đủ email và mật khẩu");
        }

        String email = request.getEmail().trim().toLowerCase();
        Optional<NguoiDung> userOpt = authRepository.findByEmail(email);
        if (userOpt.isEmpty() || userOpt.get().isDeleted()) {
            return AuthResult.error(401, "Sai thông tin đăng nhập");
        }

        NguoiDung user = userOpt.get();
        if (!PasswordUtil.verify(request.getPassword(), user.getMatKhauHashed())) {
            return AuthResult.error(401, "Sai thông tin đăng nhập");
        }

        // Refuse students on admin login portal
        if (authRepository.findStudentByCccd(user.getCccd()).isPresent()) {
            return AuthResult.error(403, "Tài khoản sinh viên không có quyền truy cập cổng quản trị");
        }

        Optional<NhanVien> staffOpt = authRepository.findStaffByCccd(user.getCccd());
        if (staffOpt.isEmpty()) {
            return AuthResult.error(403, "Tài khoản không có quyền truy cập cổng quản trị");
        }

        NhanVien staff = staffOpt.get();
        String role = resolveStaffRole(user.getCccd(), staff);
        String maNV = staff.getMaNV();
        String hoTen = user.getHoTen();
        String anhThe = user.getAnhThe();

        String accessToken = JwtUtil.generateAccessToken(user.getCccd(), maNV, role, user.getEmail());
        String refreshToken = JwtUtil.generateRefreshToken(user.getCccd());

        authRepository.updateRefreshToken(user.getCccd(), refreshToken);

        AdminLoginResponseDTO responseDTO = new AdminLoginResponseDTO(
                accessToken,
                new AdminLoginResponseDTO.AdminUserDTO(maNV, hoTen, role, anhThe)
        );

        return AuthResult.success(responseDTO, refreshToken, "Đăng nhập cán bộ thành công");
    }

    @Override
    public AuthResult<LogoutResponseDTO> logout(String authHeader, String cookieRefreshToken) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                Claims claims = JwtUtil.parseToken(token);
                if (claims != null && claims.getSubject() != null) {
                    authRepository.clearRefreshToken(claims.getSubject());
                }
            }

            if (cookieRefreshToken != null && !cookieRefreshToken.isBlank()) {
                Claims refClaims = JwtUtil.parseToken(cookieRefreshToken);
                if (refClaims != null && refClaims.getSubject() != null) {
                    authRepository.clearRefreshToken(refClaims.getSubject());
                } else {
                    authRepository.findByRefreshToken(cookieRefreshToken)
                            .ifPresent(u -> authRepository.clearRefreshToken(u.getCccd()));
                }
            }
        } catch (Exception e) {
            log.warn("Error during logout token revocation: {}", e.getMessage());
        }

        return AuthResult.success(null, null, "Đăng xuất thành công");
    }

    @Override
    public AuthResult<LogoutResponseDTO> logout(String token) {
        return logout(token, null);
    }

    @Override
    public AuthResult<RefreshTokenResponseDTO> refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return AuthResult.error(401, "Refresh token không tồn tại");
        }

        Claims claims = JwtUtil.parseToken(refreshToken);
        if (claims == null) {
            return AuthResult.error(401, "Refresh token không hợp lệ hoặc đã hết hạn");
        }

        String tokenType = claims.get("type", String.class);
        if (!"REFRESH".equalsIgnoreCase(tokenType)) {
            return AuthResult.error(401, "Token không phải là refresh token hợp lệ");
        }

        String cccd = claims.getSubject();
        if (cccd == null || cccd.isBlank()) {
            return AuthResult.error(401, "Refresh token không hợp lệ");
        }

        Optional<NguoiDung> userOpt = authRepository.findByCccd(cccd);
        if (userOpt.isEmpty() || userOpt.get().isDeleted()) {
            return AuthResult.error(401, "Người dùng không tồn tại hoặc đã bị khóa");
        }

        NguoiDung user = userOpt.get();
        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            return AuthResult.error(401, "Refresh token không hợp lệ hoặc đã bị thu hồi");
        }

        // Determine user role and code
        String role;
        String userCode;

        Optional<SinhVien> studentOpt = authRepository.findStudentByCccd(cccd);
        if (studentOpt.isPresent()) {
            role = "SINHVIEN";
            userCode = studentOpt.get().getMaSV();
        } else {
            Optional<NhanVien> staffOpt = authRepository.findStaffByCccd(cccd);
            if (staffOpt.isPresent()) {
                NhanVien staff = staffOpt.get();
                role = resolveStaffRole(cccd, staff);
                userCode = staff.getMaNV();
            } else {
                return AuthResult.error(401, "Không tìm thấy hồ sơ người dùng hợp lệ");
            }
        }

        String newAccessToken = JwtUtil.generateAccessToken(cccd, userCode, role, user.getEmail());
        String newRefreshToken = JwtUtil.generateRefreshToken(cccd);

        authRepository.updateRefreshToken(cccd, newRefreshToken);

        RefreshTokenResponseDTO responseDTO = new RefreshTokenResponseDTO(newAccessToken);
        return AuthResult.success(responseDTO, newRefreshToken, "Cấp token mới thành công");
    }

    private String resolveStaffRole(String cccd, NhanVien staff) {
        if (authRepository.findHeadOfficeByCccd(cccd).isPresent() || staff instanceof TruongPhongDaoTao) {
            return "TRUONGPHONG";
        }
        if (authRepository.findAssistantByCccd(cccd).isPresent() || staff instanceof TroLyDaoTao) {
            return "TROLY";
        }
        if (authRepository.findLecturerByCccd(cccd).isPresent() || staff instanceof GiangVien) {
            return "GIANGVIEN";
        }
        return "GIANGVIEN";
    }
}
