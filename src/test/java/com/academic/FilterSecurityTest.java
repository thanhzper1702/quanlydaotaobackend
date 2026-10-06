package com.academic;

import com.university.core.common.ApiResponse;
import com.university.core.filter.AppSecurityContext;
import com.university.core.filter.AuthFilter;
import com.university.core.filter.RoleFilter;
import com.university.core.util.JwtUtil;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FilterSecurityTest {

    private AuthFilter authFilter;
    private RoleFilter roleFilter;

    @BeforeEach
    public void setup() {
        authFilter = new AuthFilter();
        roleFilter = new RoleFilter();
    }

    @Test
    public void testPublicAuthPathsBypassAuthFilter() throws IOException {
        MockRequestContext context = new MockRequestContext("POST", "auth/login");
        authFilter.filter(context);
        assertNull(context.abortedResponse, "Public auth/login should not be aborted");

        context = new MockRequestContext("POST", "auth/admin/login");
        authFilter.filter(context);
        assertNull(context.abortedResponse);

        context = new MockRequestContext("POST", "auth/refresh-token");
        authFilter.filter(context);
        assertNull(context.abortedResponse);

        context = new MockRequestContext("POST", "auth/logout");
        authFilter.filter(context);
        assertNull(context.abortedResponse);
    }

    @Test
    public void testMissingAuthorizationHeaderReturns401() throws IOException {
        MockRequestContext context = new MockRequestContext("GET", "student/grades");
        authFilter.filter(context);

        assertNotNull(context.abortedResponse);
        assertEquals(401, context.abortedResponse.getStatus());
        ApiResponse<?> res = (ApiResponse<?>) context.abortedResponse.getEntity();
        assertEquals(401, res.getStatus());
    }

    @Test
    public void testInvalidTokenReturns401() throws IOException {
        MockRequestContext context = new MockRequestContext("GET", "student/grades");
        context.headers.put(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here");
        authFilter.filter(context);

        assertNotNull(context.abortedResponse);
        assertEquals(401, context.abortedResponse.getStatus());
    }

    @Test
    public void testRefreshTokenAsAccessTokenRejectedWith401() throws IOException {
        String refreshToken = JwtUtil.generateRefreshToken("001099000001");
        MockRequestContext context = new MockRequestContext("GET", "student/grades");
        context.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken);
        authFilter.filter(context);

        assertNotNull(context.abortedResponse);
        assertEquals(401, context.abortedResponse.getStatus());
        ApiResponse<?> res = (ApiResponse<?>) context.abortedResponse.getEntity();
        assertTrue(res.getMessage().contains("Invalid access token"));
    }

    @Test
    public void testStudentRoleAccessControl() throws IOException {
        String studentToken = JwtUtil.generateAccessToken("001099000001", "B21DCCN001", "SINHVIEN", "student@ptit.edu.vn");

        // 1. Access student route -> allowed
        MockRequestContext ctxStudent = new MockRequestContext("GET", "student/grades");
        ctxStudent.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken);
        authFilter.filter(ctxStudent);
        assertNull(ctxStudent.abortedResponse);
        roleFilter.filter(ctxStudent);
        assertNull(ctxStudent.abortedResponse, "Student should access student/grades");

        // 2. Access lecturer route -> 403 Forbidden
        MockRequestContext ctxLecturer = new MockRequestContext("GET", "lecturer/grades");
        ctxLecturer.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken);
        authFilter.filter(ctxLecturer);
        roleFilter.filter(ctxLecturer);
        assertNotNull(ctxLecturer.abortedResponse);
        assertEquals(403, ctxLecturer.abortedResponse.getStatus());

        // 3. Access assistant route -> 403 Forbidden
        MockRequestContext ctxAssistant = new MockRequestContext("GET", "assistant/semesters");
        ctxAssistant.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken);
        authFilter.filter(ctxAssistant);
        roleFilter.filter(ctxAssistant);
        assertNotNull(ctxAssistant.abortedResponse);
        assertEquals(403, ctxAssistant.abortedResponse.getStatus());

        // 4. Access head-office route -> 403 Forbidden
        MockRequestContext ctxHeadOffice = new MockRequestContext("GET", "head-office/courses");
        ctxHeadOffice.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken);
        authFilter.filter(ctxHeadOffice);
        roleFilter.filter(ctxHeadOffice);
        assertNotNull(ctxHeadOffice.abortedResponse);
        assertEquals(403, ctxHeadOffice.abortedResponse.getStatus());

        // 5. Access POST notifications -> 403 Forbidden
        MockRequestContext ctxNotifPost = new MockRequestContext("POST", "notifications");
        ctxNotifPost.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken);
        authFilter.filter(ctxNotifPost);
        roleFilter.filter(ctxNotifPost);
        assertNotNull(ctxNotifPost.abortedResponse);
        assertEquals(403, ctxNotifPost.abortedResponse.getStatus());

        // 6. Access GET notifications -> allowed for all authenticated users
        MockRequestContext ctxNotifGet = new MockRequestContext("GET", "notifications");
        ctxNotifGet.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken);
        authFilter.filter(ctxNotifGet);
        roleFilter.filter(ctxNotifGet);
        assertNull(ctxNotifGet.abortedResponse, "Authenticated student can view notifications");
    }

    @Test
    public void testStaffRolesAccessControl() throws IOException {
        String lecturerToken = JwtUtil.generateAccessToken("001088000002", "GV012", "GIANGVIEN", "gv@ptit.edu.vn");
        String assistantToken = JwtUtil.generateAccessToken("001088000003", "TL005", "TROLY", "tl@ptit.edu.vn");
        String headOfficeToken = JwtUtil.generateAccessToken("001088000004", "TP001", "TRUONGPHONG", "tp@ptit.edu.vn");

        // Lecturer on lecturer route -> OK
        MockRequestContext ctxGv = new MockRequestContext("GET", "lecturer/grades");
        ctxGv.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + lecturerToken);
        authFilter.filter(ctxGv);
        roleFilter.filter(ctxGv);
        assertNull(ctxGv.abortedResponse);

        // Assistant on assistant route -> OK
        MockRequestContext ctxTl = new MockRequestContext("GET", "assistant/semesters");
        ctxTl.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + assistantToken);
        authFilter.filter(ctxTl);
        roleFilter.filter(ctxTl);
        assertNull(ctxTl.abortedResponse);

        // Assistant creating notification -> OK
        MockRequestContext ctxTlNotif = new MockRequestContext("POST", "notifications");
        ctxTlNotif.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + assistantToken);
        authFilter.filter(ctxTlNotif);
        roleFilter.filter(ctxTlNotif);
        assertNull(ctxTlNotif.abortedResponse);

        // Head office on head-office route -> OK
        MockRequestContext ctxTp = new MockRequestContext("GET", "head-office/courses");
        ctxTp.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + headOfficeToken);
        authFilter.filter(ctxTp);
        roleFilter.filter(ctxTp);
        assertNull(ctxTp.abortedResponse);

        // Head office creating notification -> OK
        MockRequestContext ctxTpNotif = new MockRequestContext("POST", "notifications");
        ctxTpNotif.headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + headOfficeToken);
        authFilter.filter(ctxTpNotif);
        roleFilter.filter(ctxTpNotif);
        assertNull(ctxTpNotif.abortedResponse);
    }

    static class MockRequestContext implements ContainerRequestContext {
        String method;
        String path;
        Map<String, String> headers = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        SecurityContext securityContext;
        Response abortedResponse;

        public MockRequestContext(String method, String path) {
            this.method = method;
            this.path = path;
            this.securityContext = new SecurityContext() {
                @Override
                public java.security.Principal getUserPrincipal() { return null; }
                @Override
                public boolean isUserInRole(String role) { return false; }
                @Override
                public boolean isSecure() { return false; }
                @Override
                public String getAuthenticationScheme() { return null; }
            };
        }

        @Override
        public String getMethod() { return method; }

        @Override
        public void setMethod(String method) { this.method = method; }

        @Override
        public UriInfo getUriInfo() {
            return new MockUriInfo(path);
        }

        @Override
        public String getHeaderString(String name) { return headers.get(name); }

        @Override
        public SecurityContext getSecurityContext() { return securityContext; }

        @Override
        public void setSecurityContext(SecurityContext context) { this.securityContext = context; }

        @Override
        public void abortWith(Response response) { this.abortedResponse = response; }

        @Override
        public Object getProperty(String name) { return properties.get(name); }

        @Override
        public void setProperty(String name, Object object) { properties.put(name, object); }

        @Override
        public void removeProperty(String name) { properties.remove(name); }

        @Override
        public int getLength() { return 0; }

        @Override
        public java.util.Collection<String> getPropertyNames() { return properties.keySet(); }
        @Override
        public jakarta.ws.rs.core.Request getRequest() { return null; }
        @Override
        public void setRequestUri(URI requestUri) {}
        @Override
        public void setRequestUri(URI baseUri, URI requestUri) {}
        @Override
        public jakarta.ws.rs.core.MultivaluedMap<String, String> getHeaders() { return null; }
        @Override
        public java.util.Date getDate() { return null; }
        @Override
        public jakarta.ws.rs.core.MediaType getMediaType() { return null; }
        @Override
        public java.util.List<jakarta.ws.rs.core.MediaType> getAcceptableMediaTypes() { return null; }
        @Override
        public java.util.Locale getLanguage() { return null; }

        @Override
        public java.util.List<java.util.Locale> getAcceptableLanguages() { return null; }
        @Override
        public Map<String, jakarta.ws.rs.core.Cookie> getCookies() { return null; }
        @Override
        public boolean hasEntity() { return false; }
        @Override
        public java.io.InputStream getEntityStream() { return null; }
        @Override
        public void setEntityStream(java.io.InputStream input) {}
    }

    static class MockUriInfo implements UriInfo {
        String path;

        public MockUriInfo(String path) { this.path = path; }
        @Override public String getPath() { return path; }
        @Override public String getPath(boolean decode) { return path; }
        @Override public java.util.List<jakarta.ws.rs.core.PathSegment> getPathSegments() { return null; }
        @Override public java.util.List<jakarta.ws.rs.core.PathSegment> getPathSegments(boolean decode) { return null; }
        @Override public URI getRequestUri() { return URI.create("http://localhost:8080/api/" + path); }
        @Override public jakarta.ws.rs.core.UriBuilder getRequestUriBuilder() { return null; }
        @Override public URI getAbsolutePath() { return null; }
        @Override public jakarta.ws.rs.core.UriBuilder getAbsolutePathBuilder() { return null; }
        @Override public URI getBaseUri() { return URI.create("http://localhost:8080/api/"); }
        @Override public jakarta.ws.rs.core.UriBuilder getBaseUriBuilder() { return null; }
        @Override public jakarta.ws.rs.core.MultivaluedMap<String, String> getPathParameters() { return null; }
        @Override public jakarta.ws.rs.core.MultivaluedMap<String, String> getPathParameters(boolean decode) { return null; }
        @Override public jakarta.ws.rs.core.MultivaluedMap<String, String> getQueryParameters() { return null; }
        @Override public jakarta.ws.rs.core.MultivaluedMap<String, String> getQueryParameters(boolean decode) { return null; }
        @Override public java.util.List<String> getMatchedURIs() { return null; }
        @Override public java.util.List<String> getMatchedURIs(boolean decode) { return null; }
        @Override public java.util.List<Object> getMatchedResources() { return null; }
        @Override public URI resolve(URI uri) { return null; }
        @Override public URI relativize(URI uri) { return null; }
    }
}
