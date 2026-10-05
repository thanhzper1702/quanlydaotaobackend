package com.university.modules.assistant;
import com.university.modules.assistant.requestDTO.*;
import com.university.modules.assistant.responseDTO.*;


import com.university.core.common.BaseController;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/assistant")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssistantController extends BaseController {

    private final AssistantService assistantService;

    public AssistantController() {
        this.assistantService = new AssistantServiceImpl();
    }

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GET
    @Path("/semesters")
    public Response getSemesters() {
        return null;
    }

    @GET
    @Path("/semesters/{maHocKy}/classes")
    public Response getClassesBySemester(@PathParam("maHocKy") String maHocKy,
                                        @QueryParam("page") @DefaultValue("1") int page,
                                        @QueryParam("limit") @DefaultValue("10") int limit) {
        return null;
    }

    @POST
    @Path("/semesters/{maHocKy}/classes")
    public Response createClass(@PathParam("maHocKy") String maHocKy, CreateClassRequestDTO request) {
        return null;
    }

    @PUT
    @Path("/classes/{maLHP}")
    public Response updateClass(@PathParam("maLHP") String maLHP, UpdateClassRequestDTO request) {
        return null;
    }

    @DELETE
    @Path("/classes/{maLHP}")
    public Response deleteClass(@PathParam("maLHP") String maLHP) {
        return null;
    }

    @PATCH
    @Path("/classes/{maLHP}/assign-lecturer")
    public Response assignLecturer(@PathParam("maLHP") String maLHP, AssignLecturerRequestDTO request) {
        return null;
    }

    @POST
    @Path("/classes/open-registration-period")
    public Response openRegistrationPeriod(OpenRegistrationPeriodRequestDTO request) {
        return null;
    }

    @GET
    @Path("/classes/{maLHP}/students")
    public Response getClassStudents(@PathParam("maLHP") String maLHP,
                                     @QueryParam("export") @DefaultValue("false") boolean export) {
        return null;
    }

    @GET
    @Path("/schedules/{maLHP}")
    public Response getClassSchedules(@PathParam("maLHP") String maLHP) {
        return null;
    }

    @PUT
    @Path("/schedules/{maLHP}")
    public Response updateSchedule(@PathParam("maLHP") String maLHP, UpdateScheduleRequestDTO request) {
        return null;
    }

    @DELETE
    @Path("/schedules/{maLHP}")
    public Response deleteSchedule(@PathParam("maLHP") String maLHP) {
        return null;
    }

    @GET
    @Path("/class-requests")
    public Response getClassRequests(@QueryParam("trangThai") String trangThai,
                                     @QueryParam("page") @DefaultValue("1") int page,
                                     @QueryParam("limit") @DefaultValue("10") int limit) {
        return null;
    }

    @PATCH
    @Path("/class-requests/{maYeuCau}/status")
    public Response updateClassRequestStatus(@PathParam("maYeuCau") String maYeuCau,
                                             UpdateClassRequestStatusRequestDTO request) {
        return null;
    }
}
