package com.university.modules.lecturer;
import com.university.modules.lecturer.requestDTO.*;
import com.university.modules.lecturer.responseDTO.*;


import com.university.core.common.BaseController;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/lecturer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LecturerController extends BaseController {

    private final LecturerService lecturerService;

    public LecturerController() {
        this.lecturerService = new LecturerServiceImpl();
    }

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    @GET
    @Path("/grades")
    public Response getTeachingClasses(@QueryParam("maHocKy") String maHocKy) {
        return null;
    }

    @GET
    @Path("/grades/{maLHP}")
    public Response getClassGrades(@PathParam("maLHP") String maLHP) {
        return null;
    }

    @PUT
    @Path("/grades/{maLHP}")
    public Response updateClassGrades(@PathParam("maLHP") String maLHP, UpdateGradesRequestDTO request) {
        return null;
    }

    @GET
    @Path("/grades/{maLHP}/export")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportGrades(@PathParam("maLHP") String maLHP) {
        return null;
    }

    @GET
    @Path("/teaching-schedule")
    public Response getTeachingSchedule(@QueryParam("maHocKy") String maHocKy) {
        return null;
    }

    @GET
    @Path("/attendance/{maLichHoc}")
    public Response getAttendanceList(@PathParam("maLichHoc") String maLichHoc) {
        return null;
    }

    @POST
    @Path("/attendance/{maLichHoc}")
    public Response updateAttendance(@PathParam("maLichHoc") String maLichHoc, UpdateAttendanceRequestDTO request) {
        return null;
    }
}
