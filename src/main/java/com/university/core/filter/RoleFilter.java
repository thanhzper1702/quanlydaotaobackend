package com.university.core.filter;

import com.university.core.common.ApiResponse;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class RoleFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return;
        }

        String path = requestContext.getUriInfo().getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (path.startsWith("auth")) {
            return;
        }

        SecurityContext securityContext = requestContext.getSecurityContext();
        AppUserPrincipal principal = null;
        if (securityContext instanceof AppSecurityContext) {
            principal = ((AppSecurityContext) securityContext).getAppUserPrincipal();
        }

        String role = (principal != null) ? principal.getRole() : null;

        // Role verification for module routes
        if (path.startsWith("student") && !"SINHVIEN".equals(role)) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(ApiResponse.error(Response.Status.FORBIDDEN.getStatusCode(), "Access denied: Requires SINHVIEN role"))
                            .build()
            );
            return;
        }

        if (path.startsWith("lecturer") && !"GIANGVIEN".equals(role)) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(ApiResponse.error(Response.Status.FORBIDDEN.getStatusCode(), "Access denied: Requires GIANGVIEN role"))
                            .build()
            );
            return;
        }

        if (path.startsWith("assistant") && !"TROLY".equals(role) && !"TRUONGPHONG".equals(role)) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(ApiResponse.error(Response.Status.FORBIDDEN.getStatusCode(), "Access denied: Requires TROLY role"))
                            .build()
            );
            return;
        }

        boolean isLecturerLookupForAssistant = "GET".equalsIgnoreCase(method)
                && "head-office/lecturers".equals(path);
        if (isLecturerLookupForAssistant && !"TROLY".equals(role) && !"TRUONGPHONG".equals(role)) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(ApiResponse.error(Response.Status.FORBIDDEN.getStatusCode(), "Access denied: Requires TROLY or TRUONGPHONG role"))
                            .build()
            );
            return;
        }

        if (path.startsWith("head-office") && !isLecturerLookupForAssistant && !"TRUONGPHONG".equals(role)) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(ApiResponse.error(Response.Status.FORBIDDEN.getStatusCode(), "Access denied: Requires TRUONGPHONG role"))
                            .build()
            );
            return;
        }

        if (path.startsWith("notifications") && ("POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method))) {
            if (!"TROLY".equals(role) && !"TRUONGPHONG".equals(role)) {
                requestContext.abortWith(
                        Response.status(Response.Status.FORBIDDEN)
                                .type(MediaType.APPLICATION_JSON)
                                .entity(ApiResponse.error(Response.Status.FORBIDDEN.getStatusCode(), "Access denied: Requires TROLY or TRUONGPHONG role"))
                                .build()
                );
            }
        }
    }
}
