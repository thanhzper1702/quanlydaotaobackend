package com.university.modules.auth;
import com.university.modules.auth.requestDTO.*;
import com.university.modules.auth.responseDTO.*;


public class AuthServiceImpl implements AuthService {

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        return null;
    }

    @Override
    public AdminLoginResponseDTO adminLogin(AdminLoginRequestDTO request) {
        return null;
    }

    @Override
    public LogoutResponseDTO logout(String token) {
        return null;
    }

    @Override
    public RefreshTokenResponseDTO refreshToken(String refreshToken) {
        return null;
    }
}
