package com.university.modules.auth;

import com.university.core.common.ApiResponse;
import com.university.core.common.BaseController;
import com.university.core.config.EnvironmentLoader;
import com.university.core.util.JwtUtil;
import com.university.modules.auth.requestDTO.AdminLoginRequestDTO;
import com.university.modules.auth.requestDTO.LoginRequestDTO;
import com.university.modules.auth.responseDTO.AdminLoginResponseDTO;
import com.university.modules.auth.responseDTO.LoginResponseDTO;
import com.university.modules.auth.responseDTO.LogoutResponseDTO;
import com.university.modules.auth.responseDTO.RefreshTokenResponseDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController extends BaseController {

    private final AuthService authService;
    private final boolean cookieSecure;

    public AuthController() {
        this.authService = new AuthServiceImpl();
        this.cookieSecure = EnvironmentLoader.getBoolean("COOKIE_SECURE", false);
    }

    public AuthController(AuthService authService) {
        this.authService = authService;
        this.cookieSecure = EnvironmentLoader.getBoolean("COOKIE_SECURE", false);
    }

    @POST
    @Path("/login")
    public Response login(LoginRequestDTO request) {
        AuthResult<LoginResponseDTO> result = authService.login(request);
        if (!result.isSuccess()) {
            return Response.status(result.getStatusCode())
                    .entity(ApiResponse.error(result.getStatusCode(), result.getMessage()))
                    .build();
        }

        NewCookie refreshCookie = createRefreshTokenCookie(result.getRefreshToken());
        return Response.ok(ApiResponse.success(result.getStatusCode(), result.getMessage(), result.getData()))
                .cookie(refreshCookie)
                .build();
    }

    @POST
    @Path("/admin/login")
    public Response adminLogin(AdminLoginRequestDTO request) {
        AuthResult<AdminLoginResponseDTO> result = authService.adminLogin(request);
        if (!result.isSuccess()) {
            return Response.status(result.getStatusCode())
                    .entity(ApiResponse.error(result.getStatusCode(), result.getMessage()))
                    .build();
        }

        NewCookie refreshCookie = createRefreshTokenCookie(result.getRefreshToken());
        return Response.ok(ApiResponse.success(result.getStatusCode(), result.getMessage(), result.getData()))
                .cookie(refreshCookie)
                .build();
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader,
            @CookieParam("refreshToken") Cookie cookie) {
        String cookieToken = (cookie != null) ? cookie.getValue() : null;
        AuthResult<LogoutResponseDTO> result = authService.logout(authHeader, cookieToken);

        NewCookie clearCookie = createClearTokenCookie();
        return Response.ok(ApiResponse.success(200, result.getMessage(), null))
                .cookie(clearCookie)
                .build();
    }

    @POST
    @Path("/refresh-token")
    public Response refreshToken(@CookieParam("refreshToken") Cookie cookie) {
        String token = (cookie != null) ? cookie.getValue() : null;
        AuthResult<RefreshTokenResponseDTO> result = authService.refreshToken(token);
        if (!result.isSuccess()) {
            return Response.status(result.getStatusCode())
                    .entity(ApiResponse.error(result.getStatusCode(), result.getMessage()))
                    .build();
        }

        NewCookie refreshCookie = createRefreshTokenCookie(result.getRefreshToken());
        return Response.ok(ApiResponse.success(200, result.getMessage(), result.getData()))
                .cookie(refreshCookie)
                .build();
    }

    private NewCookie createRefreshTokenCookie(String refreshToken) {
        int maxAge = (int) (JwtUtil.getRefreshTokenExpiration() / 1000);
        return new NewCookie.Builder("refreshToken")
                .value(refreshToken != null ? refreshToken : "")
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(NewCookie.SameSite.LAX)
                .build();
    }

    private NewCookie createClearTokenCookie() {
        return new NewCookie.Builder("refreshToken")
                .value("")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(NewCookie.SameSite.LAX)
                .build();
    }
}
