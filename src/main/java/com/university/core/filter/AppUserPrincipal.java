package com.university.core.filter;

import java.security.Principal;

public class AppUserPrincipal implements Principal {

    private final String cccd;
    private final String userCode;
    private final String role;
    private final String email;

    public AppUserPrincipal(String cccd, String userCode, String role, String email) {
        this.cccd = cccd;
        this.userCode = userCode;
        this.role = role;
        this.email = email;
    }

    @Override
    public String getName() {
        return cccd;
    }

    public String getCccd() {
        return cccd;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }
}
