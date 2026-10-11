package com.university.modules.student;

import com.university.core.common.ApiResponse;
import com.university.core.common.BaseController;
import com.university.modules.student.StudentRequest.*;
import com.university.modules.student.StudentResponse.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.LinkedHashMap;
import java.util.Map;

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

    protected String getRequiredStudentCode() {
        String code = getAuthenticatedUserCode();
        if (code == null || code.isBlank()) {
            throw new StudentBusinessException(401, "Người dùng chưa được xác thực hoặc không có quyền truy cập");
        }
        return code;
    }

    @GET
    @Path("/grades")
    public Response getGrades(@QueryParam("maHocKy") String maHocKy) {
        try {
            String maSV = getRequiredStudentCode();
            StudentGradeResponse dto = studentService.getGrades(maSV, maHocKy);

            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("diemTBTichLuy", dto.getDiemTBTichLuy());
            meta.put("soTinChiTichLuy", dto.getSoTinChiTichLuy());
            meta.put("xepLoaiHocLucTichLuy", dto.getXepLoaiHocLucTichLuy());

            return Response.ok(ApiResponse.success(200, "Bảng điểm sinh viên kèm meta học vụ tích lũy", dto.getSemesters(), meta)).build();
        } catch (StudentBusinessException e) {
            return Response.status(e.getStatusCode()).entity(ApiResponse.error(e.getStatusCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiResponse.error(500, e.getMessage())).build();
        }
    }

    public Response getGrades() {
        return getGrades(null);
    }

    @GET
    @Path("/schedule")
    public Response getSchedule(@QueryParam("maHocKy") String maHocKy) {
        try {
            String maSV = getRequiredStudentCode();
            StudentScheduleResponse dto = studentService.getSchedule(maSV, maHocKy);

            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("maHocKyHienTai", dto.getMaHocKyHienTai());
            meta.put("tenHocKy", dto.getTenHocKy());

            return Response.ok(ApiResponse.success(200, "Thời khóa biểu sinh viên cho ma trận tuần", dto.getSchedules(), meta)).build();
        } catch (StudentBusinessException e) {
            return Response.status(e.getStatusCode()).entity(ApiResponse.error(e.getStatusCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiResponse.error(500, e.getMessage())).build();
        }
    }

    @GET
    @Path("/registration/open-classes")
    public Response getOpenClasses(@QueryParam("maHP") String maHP,
                                  @QueryParam("keyword") String keyword,
                                  @QueryParam("page") @DefaultValue("1") int page,
                                  @QueryParam("limit") @DefaultValue("10") int limit) {
        try {
            OpenedEnrollingClass dto = studentService.getOpenClasses(maHP, keyword, page, limit);

            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("page", dto.getPage());
            meta.put("pageSize", dto.getPageSize());
            meta.put("totalItems", dto.getTotalItems());
            meta.put("totalPages", dto.getTotalPages());

            return Response.ok(ApiResponse.success(200, "Danh sách lớp mở kèm meta phân trang", dto.getClasses(), meta)).build();
        } catch (StudentBusinessException e) {
            return Response.status(e.getStatusCode()).entity(ApiResponse.error(e.getStatusCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiResponse.error(500, e.getMessage())).build();
        }
    }

    @GET
    @Path("/registration/registered-classes")
    public Response getRegisteredClasses() {
        try {
            String maSV = getRequiredStudentCode();
            RegisteredClassResponse dto = studentService.getRegisteredClasses(maSV);

            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("tongSoLopDaDangKy", dto.getTongSoLopDaDangKy());
            meta.put("tongSoTinChiDaDangKy", dto.getTongSoTinChiDaDangKy());
            meta.put("hanMucTinChiToiDa", dto.getHanMucTinChiToiDa());

            return Response.ok(ApiResponse.success(200, "Danh sách lớp đã ghi danh kèm meta tổng tín chỉ", dto.getRegisteredClasses(), meta)).build();
        } catch (StudentBusinessException e) {
            return Response.status(e.getStatusCode()).entity(ApiResponse.error(e.getStatusCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiResponse.error(500, e.getMessage())).build();
        }
    }

    @POST
    @Path("/registration/enroll")
    public Response enrollCourse(EnrollCourseRequest request) {
        try {
            String maSV = getRequiredStudentCode();
            EnrollCourseResponse dto = studentService.enrollCourse(maSV, request);
            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success(201, "Đăng ký thành công", dto))
                    .build();
        } catch (StudentBusinessException e) {
            return Response.status(e.getStatusCode()).entity(ApiResponse.error(e.getStatusCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiResponse.error(500, e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/registration/cancel/{maHoSo}")
    public Response cancelEnrollment(@PathParam("maHoSo") Long maHoSo) {
        try {
            String maSV = getRequiredStudentCode();
            CancelEnrollmentResponse dto = studentService.cancelEnrollment(maSV, maHoSo);
            return Response.ok(ApiResponse.success(200, "Hủy lớp thành công", dto)).build();
        } catch (StudentBusinessException e) {
            return Response.status(e.getStatusCode()).entity(ApiResponse.error(e.getStatusCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiResponse.error(500, e.getMessage())).build();
        }
    }

    @POST
    @Path("/registration/open-request")
    public Response requestOpenCourse(OpenClassRequestRequest request) {
        try {
            String maSV = getRequiredStudentCode();
            OpenClassRequestResponse dto = studentService.requestOpenCourse(maSV, request);
            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success(201, "Gửi kiến nghị thành công", dto))
                    .build();
        } catch (StudentBusinessException e) {
            return Response.status(e.getStatusCode()).entity(ApiResponse.error(e.getStatusCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiResponse.error(500, e.getMessage())).build();
        }
    }
}
