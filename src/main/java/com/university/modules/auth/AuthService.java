package com.university.modules.auth;
import com.university.modules.auth.requestDTO.*;
import com.university.modules.auth.responseDTO.*;


public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    AdminLoginResponseDTO adminLogin(AdminLoginRequestDTO request);

    LogoutResponseDTO logout(String token);

    RefreshTokenResponseDTO refreshToken(String refreshToken);
}
