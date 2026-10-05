package com.university.modules.auth;
import com.university.modules.auth.requestDTO.*;
import com.university.modules.auth.responseDTO.*;


import com.university.core.common.BaseController;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController extends BaseController {

    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthServiceImpl();
    }

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/login")
    public Response login(LoginRequestDTO request) {
        return null;
    }

    @POST
    @Path("/admin/login")
    public Response adminLogin(AdminLoginRequestDTO request) {
        return null;
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader) {
        return null;
    }

    @POST
    @Path("/refresh-token")
    public Response refreshToken(@CookieParam("refreshToken") Cookie cookie) {
        return null;
    }
}
