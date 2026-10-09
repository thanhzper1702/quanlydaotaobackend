package com.university.modules.auth;

import com.university.modules.auth.AuthRequestDTO.AdminLoginRequestDTO;
import com.university.modules.auth.AuthRequestDTO.LoginRequestDTO;
import com.university.modules.auth.AuthResponseDTO.AdminLoginResponseDTO;
import com.university.modules.auth.AuthResponseDTO.LoginResponseDTO;
import com.university.modules.auth.AuthResponseDTO.LogoutResponseDTO;
import com.university.modules.auth.AuthResponseDTO.RefreshTokenResponseDTO;

public interface AuthService {

    AuthResult<LoginResponseDTO> login(LoginRequestDTO request);

    AuthResult<AdminLoginResponseDTO> adminLogin(AdminLoginRequestDTO request);

    AuthResult<LogoutResponseDTO> logout(String authHeader, String cookieRefreshToken);

    AuthResult<LogoutResponseDTO> logout(String token);

    AuthResult<RefreshTokenResponseDTO> refreshToken(String refreshToken);
}
