package com.university.modules.profile;
import com.university.modules.profile.requestDTO.*;
import com.university.modules.profile.responseDTO.*;


import com.university.core.common.BaseController;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/profile")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfileController extends BaseController {

    private final ProfileService profileService;

    public ProfileController() {
        this.profileService = new ProfileServiceImpl();
    }

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GET
    @Path("/me")
    public Response getProfile() {
        return null;
    }

    @PUT
    @Path("/change-password")
    public Response changePassword(ChangePasswordRequestDTO request) {
        return null;
    }
}
