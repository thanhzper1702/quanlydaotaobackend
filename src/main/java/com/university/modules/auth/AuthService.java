package com.university.modules.auth;

import com.university.modules.auth.requestDTO.AdminLoginRequestDTO;
import com.university.modules.auth.requestDTO.LoginRequestDTO;
import com.university.modules.auth.responseDTO.AdminLoginResponseDTO;
import com.university.modules.auth.responseDTO.LoginResponseDTO;
import com.university.modules.auth.responseDTO.LogoutResponseDTO;
import com.university.modules.auth.responseDTO.RefreshTokenResponseDTO;

public interface AuthService {

    AuthResult<LoginResponseDTO> login(LoginRequestDTO request);

    AuthResult<AdminLoginResponseDTO> adminLogin(AdminLoginRequestDTO request);

    AuthResult<LogoutResponseDTO> logout(String authHeader, String cookieRefreshToken);

    AuthResult<LogoutResponseDTO> logout(String token);

    AuthResult<RefreshTokenResponseDTO> refreshToken(String refreshToken);
}
