package com.academic;

import com.university.core.common.ApiResponse;
import com.university.modules.auth.AuthController;
import com.university.modules.auth.AuthResult;
import com.university.modules.auth.AuthService;
import com.university.modules.auth.requestDTO.AdminLoginRequestDTO;
import com.university.modules.auth.requestDTO.LoginRequestDTO;
import com.university.modules.auth.responseDTO.AdminLoginResponseDTO;
import com.university.modules.auth.responseDTO.LoginResponseDTO;
import com.university.modules.auth.responseDTO.LogoutResponseDTO;
import com.university.modules.auth.responseDTO.RefreshTokenResponseDTO;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthControllerTest {

    private AuthController controller;
    private MockAuthService mockAuthService;

    @BeforeEach
    public void setup() {
        mockAuthService = new MockAuthService();
        controller = new AuthController(mockAuthService);
    }

    @Test
    public void testLoginSuccessReturns200AndSetCookie() {
        LoginRequestDTO req = new LoginRequestDTO("student@ptit.edu.vn", "Password123");
        Response response = controller.login(req);

        assertEquals(200, response.getStatus());

        // Check response body
        Object entity = response.getEntity();
        assertTrue(entity instanceof ApiResponse);
        ApiResponse<?> apiResponse = (ApiResponse<?>) entity;
        assertEquals(200, apiResponse.getStatus());
        assertEquals("Đăng nhập thành công", apiResponse.getMessage());
        assertNotNull(apiResponse.getData());

        // Check HttpOnly Cookie
        NewCookie cookie = response.getCookies().get("refreshToken");
        assertNotNull(cookie, "Set-Cookie refreshToken must be present");
        assertTrue(cookie.isHttpOnly(), "Cookie must be HttpOnly");
        assertEquals("mock-refresh-token", cookie.getValue());
        assertTrue(cookie.getMaxAge() > 0);
    }

    @Test
    public void testLoginFailureReturns401() {
        mockAuthService.loginResult = AuthResult.error(401, "Sai thông tin đăng nhập");
        LoginRequestDTO req = new LoginRequestDTO("wrong@ptit.edu.vn", "Wrong");
        Response response = controller.login(req);

        assertEquals(401, response.getStatus());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getEntity();
        assertEquals(401, apiResponse.getStatus());
        assertNull(apiResponse.getData());
        assertNull(response.getCookies().get("refreshToken"));
    }

    @Test
    public void testAdminLoginSuccessReturns200AndSetCookie() {
        AdminLoginRequestDTO req = new AdminLoginRequestDTO("admin@ptit.edu.vn", "Password123");
        Response response = controller.adminLogin(req);

        assertEquals(200, response.getStatus());
        NewCookie cookie = response.getCookies().get("refreshToken");
        assertNotNull(cookie);
        assertTrue(cookie.isHttpOnly());
        assertEquals("mock-admin-refresh-token", cookie.getValue());
    }

    @Test
    public void testLogoutClearsCookieAndReturns200() {
        Cookie incomingCookie = new Cookie.Builder("refreshToken")
                .value("old-token")
                .build();

        Response response = controller.logout("Bearer some-token", incomingCookie);

        assertEquals(200, response.getStatus());
        NewCookie clearCookie = response.getCookies().get("refreshToken");
        assertNotNull(clearCookie);
        assertEquals(0, clearCookie.getMaxAge(), "Max-Age must be 0 to clear cookie");
        assertEquals("", clearCookie.getValue());
        assertTrue(clearCookie.isHttpOnly());
    }

    @Test
    public void testRefreshTokenReturnsNewTokenAndRotatesCookie() {
        Cookie incomingCookie = new Cookie.Builder("refreshToken")
                .value("valid-refresh-token")
                .build();

        Response response = controller.refreshToken(incomingCookie);

        assertEquals(200, response.getStatus());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getEntity();
        assertEquals(200, apiResponse.getStatus());
        assertNotNull(apiResponse.getData());

        NewCookie newCookie = response.getCookies().get("refreshToken");
        assertNotNull(newCookie);
        assertEquals("new-rotated-refresh-token", newCookie.getValue());
        assertTrue(newCookie.isHttpOnly());
    }

    static class MockAuthService implements AuthService {
        AuthResult<LoginResponseDTO> loginResult = AuthResult.success(
                new LoginResponseDTO("mock-access-token", new LoginResponseDTO.StudentUserDTO("SV01", "An", "SINHVIEN", null)),
                "mock-refresh-token",
                "Đăng nhập thành công"
        );

        AuthResult<AdminLoginResponseDTO> adminLoginResult = AuthResult.success(
                new AdminLoginResponseDTO("mock-admin-token", new AdminLoginResponseDTO.AdminUserDTO("GV01", "Bình", "GIANGVIEN", null)),
                "mock-admin-refresh-token",
                "Đăng nhập cán bộ thành công"
        );

        AuthResult<LogoutResponseDTO> logoutResult = AuthResult.success(null, null, "Đăng xuất thành công");

        AuthResult<RefreshTokenResponseDTO> refreshResult = AuthResult.success(
                new RefreshTokenResponseDTO("new-access-token"),
                "new-rotated-refresh-token",
                "Cấp token mới thành công"
        );

        @Override
        public AuthResult<LoginResponseDTO> login(LoginRequestDTO request) {
            return loginResult;
        }

        @Override
        public AuthResult<AdminLoginResponseDTO> adminLogin(AdminLoginRequestDTO request) {
            return adminLoginResult;
        }

        @Override
        public AuthResult<LogoutResponseDTO> logout(String authHeader, String cookieRefreshToken) {
            return logoutResult;
        }

        @Override
        public AuthResult<LogoutResponseDTO> logout(String token) {
            return logoutResult;
        }

        @Override
        public AuthResult<RefreshTokenResponseDTO> refreshToken(String refreshToken) {
            return refreshResult;
        }
    }
}
