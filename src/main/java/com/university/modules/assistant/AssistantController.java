package com.university.modules.assistant;

import com.university.core.common.ApiResponse;
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
        return toResponse(assistantService.getSemesters());
    }

    @GET
    @Path("/semesters/{maHocKy}/classes")
    public Response getClassesBySemester(@PathParam("maHocKy") String maHocKy,
                                        @QueryParam("search") String search,
                                        @QueryParam("page") @DefaultValue("1") int page,
                                        @QueryParam("limit") @DefaultValue("10") int limit) {
        return toResponse(assistantService.getClassesBySemester(maHocKy, search, page, limit));
    }

    public Response getClassesBySemester(String maHocKy, int page, int limit) {
        return toResponse(assistantService.getClassesBySemester(maHocKy, page, limit));
    }

    @POST
    @Path("/semesters/{maHocKy}/classes")
    public Response createClass(@PathParam("maHocKy") String maHocKy,
                                AssistantRequests.CreateClassRequest request) {
        return toResponse(assistantService.createClass(
                maHocKy, getAuthenticatedUserCode(), request));
    }

    @PUT
    @Path("/classes/{maLHP}")
    public Response updateClass(@PathParam("maLHP") String maLHP,
                                AssistantRequests.UpdateClassRequest request) {
        return toResponse(assistantService.updateClass(maLHP, request));
    }

    @DELETE
    @Path("/classes/{maLHP}")
    public Response deleteClass(@PathParam("maLHP") String maLHP) {
        return toResponse(assistantService.deleteClass(maLHP));
    }

    @PATCH
    @Path("/classes/{maLHP}/assign-lecturer")
    public Response assignLecturer(@PathParam("maLHP") String maLHP,
                                   AssistantRequests.AssignLecturerRequest request) {
        return toResponse(assistantService.assignLecturer(maLHP, request));
    }

    @POST
    @Path("/classes/open-registration-period")
    public Response openRegistrationPeriod(AssistantRequests.OpenRegistrationPeriodRequest request) {
        return toResponse(assistantService.openRegistrationPeriod(request));
    }

    @GET
    @Path("/classes/{maLHP}/students")
    public Response getClassStudents(@PathParam("maLHP") String maLHP,
                                     @QueryParam("page") @DefaultValue("1") int page,
                                     @QueryParam("limit") @DefaultValue("10") int limit,
                                     @QueryParam("export") @DefaultValue("false") boolean export) {
        return toResponse(assistantService.getClassStudents(maLHP, page, limit, export));
    }

    @GET
    @Path("/schedules/{maLHP}")
    public Response getClassSchedules(@PathParam("maLHP") String maLHP) {
        return toResponse(assistantService.getClassSchedules(maLHP));
    }

    @PUT
    @Path("/schedules/{maLHP}")
    public Response updateSchedule(@PathParam("maLHP") String maLHP,
                                   AssistantRequests.UpdateScheduleRequest request) {
        return toResponse(assistantService.updateSchedule(maLHP, request));
    }

    @DELETE
    @Path("/schedules/{maLH}")
    public Response deleteSchedule(@PathParam("maLH") String maLH) {
        return toResponse(assistantService.deleteSchedule(maLH));
    }

    @GET
    @Path("/class-requests")
    public Response getClassRequests(@QueryParam("trangThai") String trangThai,
                                     @QueryParam("page") @DefaultValue("1") int page,
                                     @QueryParam("limit") @DefaultValue("10") int limit) {
        return toResponse(assistantService.getClassRequests(trangThai, page, limit));
    }

    @PATCH
    @Path("/class-requests/{maYeuCau}/status")
    public Response updateClassRequestStatus(@PathParam("maYeuCau") String maYeuCau,
                                             AssistantRequests.UpdateClassRequestStatusRequest request) {
        return toResponse(assistantService.updateClassRequestStatus(maYeuCau, request));
    }

    private Response toResponse(AssistantResponses.Result<?> result) {
        if (result.isFileResponse()) {
            AssistantResponses.FilePayload file = result.getFile();
            return Response.status(result.getStatusCode())
                    .type(file.getContentType())
                    .header("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"")
                    .entity(file.getContent())
                    .build();
        }

        Object entity = result.isSuccess()
                ? ApiResponse.success(result.getStatusCode(), result.getMessage(),
                        result.getData(), result.getMeta())
                : ApiResponse.error(result.getStatusCode(), result.getMessage(), result.getErrors());
        return Response.status(result.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(entity)
                .build();
    }
}
