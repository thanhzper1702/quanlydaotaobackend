package com.university.core.common;

import com.university.core.filter.AppSecurityContext;
import com.university.core.filter.AppUserPrincipal;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

public abstract class BaseController {

    @Context
    protected SecurityContext securityContext;

    protected AppUserPrincipal getPrincipal() {
        if (securityContext instanceof AppSecurityContext) {
            return ((AppSecurityContext) securityContext).getAppUserPrincipal();
        }
        if (securityContext != null && securityContext.getUserPrincipal() instanceof AppUserPrincipal) {
            return (AppUserPrincipal) securityContext.getUserPrincipal();
        }
        return null;
    }

    protected String getAuthenticatedUserCccd() {
        AppUserPrincipal principal = getPrincipal();
        return principal != null ? principal.getCccd() : null;
    }

    protected String getAuthenticatedUserCode() {
        AppUserPrincipal principal = getPrincipal();
        return principal != null ? principal.getUserCode() : null;
    }

    protected String getAuthenticatedRole() {
        AppUserPrincipal principal = getPrincipal();
        return principal != null ? principal.getRole() : null;
    }

    protected String getAuthenticatedEmail() {
        AppUserPrincipal principal = getPrincipal();
        return principal != null ? principal.getEmail() : null;
    }
}
