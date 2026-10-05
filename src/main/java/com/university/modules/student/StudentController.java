package com.university.modules.student;

import com.university.core.common.BaseController;
import com.university.modules.student.requestDTO.*;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/student")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentController extends BaseController {

    private final StudentService studentService;

    public StudentController() {
        this.studentService = new StudentServiceImpl();
    }

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GET
    @Path("/grades")
    public Response getGrades() {
        return null;
    }

    @GET
    @Path("/schedule")
    public Response getSchedule(@QueryParam("maHocKy") String maHocKy) {
        return null;
    }

    @GET
    @Path("/registration/open-classes")
    public Response getOpenClasses(@QueryParam("maHP") String maHP,
            @QueryParam("keyword") String keyword,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit) {
        return null;
    }

    @GET
    @Path("/registration/registered-classes")
    public Response getRegisteredClasses() {
        return null;
    }

    @POST
    @Path("/registration/enroll")
    public Response enrollCourse(EnrollCourseRequestDTO request) {
        return null;
    }

    @DELETE
    @Path("/registration/cancel/{maHoSo}")
    public Response cancelEnrollment(@PathParam("maHoSo") Long maHoSo) {
        return null;
    }

    @POST
    @Path("/registration/open-request")
    public Response requestOpenCourse(OpenClassRequestRequestDTO request) {
        return null;
    }
}
