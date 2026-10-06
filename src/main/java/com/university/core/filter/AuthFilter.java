package com.university.core.filter;

import com.university.core.common.ApiResponse;
import com.university.core.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    public static final String ATTR_USER_CCCD = "authenticatedUserCccd";
    public static final String ATTR_USER_CODE = "authenticatedUserCode";
    public static final String ATTR_USER_ROLE = "authenticatedRole";
    public static final String ATTR_USER_EMAIL = "authenticatedEmail";

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "auth/login",
            "auth/admin/login",
            "auth/refresh-token",
            "auth/logout"
    );

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

        // Allow public authentication endpoints
        final String normalizedPath = path;
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(normalizedPath::startsWith);
        if (isPublic) {
            // Optional authentication for public endpoints (like logout)
            String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                Claims claims = JwtUtil.parseToken(token);
                if (claims != null && !"REFRESH".equalsIgnoreCase(claims.get("type", String.class))) {
                    String cccd = claims.getSubject();
                    String userCode = claims.get("userCode", String.class);
                    String role = claims.get("role", String.class);
                    String email = claims.get("email", String.class);

                    AppUserPrincipal principal = new AppUserPrincipal(cccd, userCode, role, email);
                    requestContext.setSecurityContext(new AppSecurityContext(principal, requestContext.getSecurityContext().isSecure()));
                }
            }
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(ApiResponse.error(Response.Status.UNAUTHORIZED.getStatusCode(), "Missing or invalid Authorization header"))
                            .build()
            );
            return;
        }

        String token = authHeader.substring(7).trim();
        Claims claims = JwtUtil.parseToken(token);
        if (claims == null) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(ApiResponse.error(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid or expired JWT token"))
                            .build()
            );
            return;
        }

        if ("REFRESH".equalsIgnoreCase(claims.get("type", String.class))) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(ApiResponse.error(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid access token"))
                            .build()
            );
            return;
        }

        String cccd = claims.getSubject();
        String userCode = claims.get("userCode", String.class);
        String role = claims.get("role", String.class);
        String email = claims.get("email", String.class);

        // Bind security context and request context properties
        AppUserPrincipal principal = new AppUserPrincipal(cccd, userCode, role, email);
        requestContext.setSecurityContext(new AppSecurityContext(principal, requestContext.getSecurityContext().isSecure()));

        requestContext.setProperty(ATTR_USER_CCCD, cccd);
        requestContext.setProperty(ATTR_USER_CODE, userCode);
        requestContext.setProperty(ATTR_USER_ROLE, role);
        requestContext.setProperty(ATTR_USER_EMAIL, email);
    }
}
