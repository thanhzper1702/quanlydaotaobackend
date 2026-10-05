package com.university.core.filter;

import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

public class AppSecurityContext implements SecurityContext {

    private final AppUserPrincipal principal;
    private final boolean isSecure;

    public AppSecurityContext(AppUserPrincipal principal, boolean isSecure) {
        this.principal = principal;
        this.isSecure = isSecure;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    public AppUserPrincipal getAppUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return principal != null && principal.getRole() != null && principal.getRole().equalsIgnoreCase(role);
    }

    @Override
    public boolean isSecure() {
        return isSecure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}
