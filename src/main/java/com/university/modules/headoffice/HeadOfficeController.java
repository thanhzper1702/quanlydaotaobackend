package com.university.modules.headoffice;
import com.university.modules.headoffice.HeadOfficeRequestDTO.CreateCourseRequestDTO;
import com.university.modules.headoffice.HeadOfficeRequestDTO.CreateSemesterRequestDTO;
import com.university.modules.headoffice.HeadOfficeRequestDTO.UpdateCourseRequestDTO;
import com.university.modules.headoffice.HeadOfficeRequestDTO.UpdateGradeLockRequestDTO;
import com.university.modules.headoffice.HeadOfficeResponseDTO.*;


import com.university.core.common.BaseController;
import com.university.core.common.ApiResponse;
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
        return execute(() -> Response.ok(ApiResponse.success("Danh mục học phần gốc", headOfficeService.getCourses(page, limit))).build());
    }

    @POST
    @Path("/courses")
    public Response createCourse(CreateCourseRequestDTO request) {
        return execute(() -> Response.status(Response.Status.CREATED)
            .entity(ApiResponse.success(Response.Status.CREATED.getStatusCode(), "Tạo học phần thành công", headOfficeService.createCourse(request))).build());
    }

    @PUT
    @Path("/courses/{maHP}")
    public Response updateCourse(@PathParam("maHP") String maHP, UpdateCourseRequestDTO request) {
        return execute(() -> Response.ok(ApiResponse.success("Cập nhật học phần thành công", headOfficeService.updateCourse(maHP, request))).build());
    }

    @DELETE
    @Path("/courses/{maHP}")
    public Response deleteCourse(@PathParam("maHP") String maHP) {
        return execute(() -> {
            headOfficeService.deleteCourse(maHP);
            return Response.ok(ApiResponse.success("Xóa học phần thành công", null)).build();
        });
    }

    @GET
    @Path("/semesters")
    public Response getSemesters() {
        return execute(() -> Response.ok(ApiResponse.success("Toàn bộ danh sách học kỳ", headOfficeService.getSemesters())).build());
    }

    @POST
    @Path("/semesters")
    public Response createSemester(CreateSemesterRequestDTO request) {
        return execute(() -> Response.status(Response.Status.CREATED)
            .entity(ApiResponse.success(Response.Status.CREATED.getStatusCode(), "Tạo học kỳ thành công", headOfficeService.createSemester(request))).build());
    }

    @GET
    @Path("/semesters/{maHocKy}/classes")
    public Response getClasses(@PathParam("maHocKy") String maHocKy,
                               @QueryParam("page") @DefaultValue("1") int page,
                               @QueryParam("limit") @DefaultValue("10") int limit) {
        return execute(() -> Response.ok(ApiResponse.success("Danh sách lớp học phần", headOfficeService.getClasses(maHocKy, page, limit))).build());
    }

    @GET
    @Path("/classes/{maLHP}/students")
    public Response getClassGrades(@PathParam("maLHP") String maLHP) {
        return execute(() -> Response.ok(ApiResponse.success("Danh sách sinh viên và bảng điểm", headOfficeService.getClassGrades(maLHP))).build());
    }

    @GET
    @Path("/classes/{maLHP}/students/export")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportClassGrades(@PathParam("maLHP") String maLHP) {
        return execute(() -> Response.ok(headOfficeService.exportClassGradesExcel(maLHP))
                .header("Content-Disposition", "attachment; filename=headoffice-class-grades.xlsx")
                .type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .build());
    }

    @PATCH
    @Path("/classes/{maLHP}/grade-lock")
    public Response updateGradeLock(@PathParam("maLHP") String maLHP, UpdateGradeLockRequestDTO request) {
        return execute(() -> {
            if (request == null || request.getKhoaDiem() == null) {
                throw new IllegalArgumentException("khoaDiem là bắt buộc");
            }
            return Response.ok(ApiResponse.success("Cập nhật trạng thái khóa điểm thành công",
                    headOfficeService.updateGradeLock(maLHP, request.getKhoaDiem()))).build();
        });
    }

    @GET
    @Path("/lecturers")
    public Response getLecturers(@QueryParam("khoa") String khoa,
                                 @QueryParam("page") @DefaultValue("1") int page,
                                 @QueryParam("limit") @DefaultValue("10") int limit) {
        return execute(() -> Response.ok(ApiResponse.success("Danh sách giảng viên", headOfficeService.getLecturers(khoa, page, limit))).build());
    }

    @GET
    @Path("/lecturers/export")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportLecturers(@QueryParam("khoa") String khoa) {
        return execute(() -> Response.ok(headOfficeService.exportLecturersExcel(khoa))
                .header("Content-Disposition", "attachment; filename=headoffice-lecturers.xlsx")
                .type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .build());
    }

    @GET
    @Path("/students")
    public Response getStudents(@QueryParam("nienKhoa") String nienKhoa,
                                @QueryParam("khoa") String khoa,
                                @QueryParam("page") @DefaultValue("1") int page,
                                @QueryParam("limit") @DefaultValue("10") int limit) {
        return execute(() -> Response.ok(ApiResponse.success("Danh sách sinh viên", headOfficeService.getStudents(nienKhoa, khoa, page, limit))).build());
    }

    @GET
    @Path("/students/export")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportStudents(@QueryParam("nienKhoa") String nienKhoa,
                                   @QueryParam("khoa") String khoa) {
        return execute(() -> Response.ok(headOfficeService.exportStudentsExcel(nienKhoa, khoa))
                .header("Content-Disposition", "attachment; filename=headoffice-students.xlsx")
                .type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .build());
    }

    @GET
    @Path("/statistics")
    public Response getStatistics(@QueryParam("maHocKy") String maHocKy) {
        return execute(() -> Response.ok(ApiResponse.success("Thống kê đào tạo", headOfficeService.getStatistics(maHocKy))).build());
    }

    @GET
    @Path("/statistics/export")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportStatistics(@QueryParam("maHocKy") String maHocKy) {
        return execute(() -> Response.ok(headOfficeService.exportStatisticsExcel(maHocKy))
                .header("Content-Disposition", "attachment; filename=headoffice-statistics.xlsx")
                .type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .build());
    }

    private Response execute(java.util.function.Supplier<Response> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
                    .entity(ApiResponse.error(400, exception.getMessage())).build();
        } catch (java.util.NoSuchElementException exception) {
            return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON)
                    .entity(ApiResponse.error(404, exception.getMessage())).build();
        } catch (IllegalStateException exception) {
            return Response.status(Response.Status.CONFLICT).type(MediaType.APPLICATION_JSON)
                    .entity(ApiResponse.error(409, exception.getMessage())).build();
        }
    }
}
