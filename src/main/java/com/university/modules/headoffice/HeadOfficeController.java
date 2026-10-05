package com.university.modules.headoffice;
import com.university.modules.headoffice.requestDTO.*;
import com.university.modules.headoffice.responseDTO.*;


import com.university.core.common.BaseController;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/head-office")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HeadOfficeController extends BaseController {

    private final HeadOfficeService headOfficeService;

    public HeadOfficeController() {
        this.headOfficeService = new HeadOfficeServiceImpl();
    }

    public HeadOfficeController(HeadOfficeService headOfficeService) {
        this.headOfficeService = headOfficeService;
    }

    @GET
    @Path("/courses")
    public Response getCourses(@QueryParam("page") @DefaultValue("1") int page,
                               @QueryParam("limit") @DefaultValue("10") int limit) {
        return null;
    }

    @POST
    @Path("/courses")
    public Response createCourse(CreateCourseRequestDTO request) {
        return null;
    }

    @PUT
    @Path("/courses/{maHP}")
    public Response updateCourse(@PathParam("maHP") String maHP, UpdateCourseRequestDTO request) {
        return null;
    }

    @GET
    @Path("/semesters")
    public Response getSemesters() {
        return null;
    }

    @POST
    @Path("/semesters")
    public Response createSemester(CreateSemesterRequestDTO request) {
        return null;
    }

    @GET
    @Path("/lecturers")
    public Response getLecturers(@QueryParam("khoa") String khoa,
                                 @QueryParam("page") @DefaultValue("1") int page,
                                 @QueryParam("limit") @DefaultValue("10") int limit) {
        return null;
    }

    @GET
    @Path("/students")
    public Response getStudents(@QueryParam("nienKhoa") String nienKhoa,
                                @QueryParam("khoa") String khoa,
                                @QueryParam("page") @DefaultValue("1") int page,
                                @QueryParam("limit") @DefaultValue("10") int limit) {
        return null;
    }

    @GET
    @Path("/statistics")
    public Response getStatistics(@QueryParam("maHocKy") String maHocKy) {
        return null;
    }
}
