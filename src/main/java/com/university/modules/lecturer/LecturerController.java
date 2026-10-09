package com.university.modules.lecturer;

import com.university.core.common.ApiResponse;
import com.university.core.common.BaseController;
import com.university.core.util.JsonUtil;
import com.university.modules.lecturer.LectureRequest.UpdateAttendanceRequest;
import com.university.modules.lecturer.LectureRequest.UpdateGradesRequest;
import com.university.modules.lecturer.LectureResponse.LecturerAttendanceResponse;
import com.university.modules.lecturer.LectureResponse.LecturerClassGradeResponse;
import com.university.modules.lecturer.LectureResponse.LecturerTeachingClassResponse;
import com.university.modules.lecturer.LectureResponse.LecturerTeachingScheduleResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/lecturer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LecturerController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(LecturerController.class.getName());
    private static final String INTERNAL_ERROR_MESSAGE = "Đã xảy ra lỗi hệ thống";

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
        try {
            String maGV = requireLecturerCode();
            LecturerTeachingClassResponse result = lecturerService.getTeachingClasses(maGV, maHocKy);
            return jsonResponse(ApiResponse.success(200, "Danh sách lớp giảng dạy", result.getData(), result.getMeta()));
        } catch (LecturerException e) {
            return lecturerErrorResponse(e);
        } catch (RuntimeException e) {
            return internalErrorResponse(e);
        }
    }

    @GET
    @Path("/grades/{maLHP}")
    public Response getClassGrades(@PathParam("maLHP") String maLHP) {
        try {
            String maGV = requireLecturerCode();
            LecturerClassGradeResponse result = lecturerService.getClassGrades(maGV, maLHP);
            return jsonResponse(ApiResponse.success(200, "Danh sách sinh viên và bảng điểm của lớp", result.getData(), result.getMeta()));
        } catch (LecturerException e) {
            return lecturerErrorResponse(e);
        } catch (RuntimeException e) {
            return internalErrorResponse(e);
        }
    }

    @PUT
    @Path("/grades/{maLHP}")
    public Response updateClassGrades(@PathParam("maLHP") String maLHP, UpdateGradesRequest request) {
        try {
            String maGV = requireLecturerCode();
            lecturerService.updateClassGrades(maGV, maLHP, request);
            return jsonResponse(ApiResponse.success(200, "Lưu điểm thành công", null));
        } catch (LecturerException e) {
            return lecturerErrorResponse(e);
        } catch (RuntimeException e) {
            return internalErrorResponse(e);
        }
    }

    @GET
    @Path("/grades/{maLHP}/export")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportGrades(@PathParam("maLHP") String maLHP) {
        try {
            String maGV = requireLecturerCode();
            byte[] workbook = lecturerService.exportGradesExcel(maGV, maLHP);
            String filename = "BangDiem_" + safeFilenamePart(maLHP) + ".xlsx";
            return Response.ok(workbook)
                    .type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
        } catch (LecturerException e) {
            return lecturerErrorResponse(e);
        } catch (RuntimeException e) {
            return internalErrorResponse(e);
        }
    }

    @GET
    @Path("/teaching-schedule")
    public Response getTeachingSchedule(@QueryParam("maHocKy") String maHocKy,
                                        @QueryParam("tuan") String tuan) {
        try {
            String maGV = requireLecturerCode();
            Integer week = parseOptionalWeek(tuan);
            LecturerTeachingScheduleResponse result = lecturerService.getTeachingSchedule(maGV, maHocKy, week);
            return jsonResponse(ApiResponse.success(200, "Ma trận lịch giảng dạy tuần của giảng viên", result.getData(), result.getMeta()));
        } catch (LecturerException e) {
            return lecturerErrorResponse(e);
        } catch (RuntimeException e) {
            return internalErrorResponse(e);
        }
    }

    @GET
    @Path("/attendance/{maLichHoc}")
    public Response getAttendanceList(@PathParam("maLichHoc") String maLichHoc) {
        try {
            String maGV = requireLecturerCode();
            LecturerAttendanceResponse result = lecturerService.getAttendanceList(maGV, maLichHoc);
            return jsonResponse(ApiResponse.success(200, "Danh sách điểm danh của buổi học", result.getData(), result.getMeta()));
        } catch (LecturerException e) {
            return lecturerErrorResponse(e);
        } catch (RuntimeException e) {
            return internalErrorResponse(e);
        }
    }

    @POST
    @Path("/attendance/{maLichHoc}")
    public Response updateAttendance(@PathParam("maLichHoc") String maLichHoc, UpdateAttendanceRequest request) {
        try {
            String maGV = requireLecturerCode();
            lecturerService.updateAttendance(maGV, maLichHoc, request);
            return jsonResponse(ApiResponse.success(200, "Điểm danh hoàn tất", null));
        } catch (LecturerException e) {
            return lecturerErrorResponse(e);
        } catch (RuntimeException e) {
            return internalErrorResponse(e);
        }
    }

    private String requireLecturerCode() {
        String code = getAuthenticatedUserCode();
        if (code == null || code.isBlank()) {
            throw new LecturerException(401, "Yêu cầu xác thực");
        }
        if (!"GIANGVIEN".equals(getAuthenticatedRole())) {
            throw new LecturerException(403, "Chỉ giảng viên được phép truy cập");
        }
        return code;
    }

    private static Integer parseOptionalWeek(String rawWeek) {
        if (rawWeek == null || rawWeek.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(rawWeek.trim());
        } catch (NumberFormatException e) {
            throw new LecturerException(400, "Tham số tuan phải là số nguyên");
        }
    }

    private static String safeFilenamePart(String value) {
        String safe = value == null ? "" : value.replaceAll("[^A-Za-z0-9_-]", "_");
        if (safe.length() > 80) {
            safe = safe.substring(0, 80);
        }
        return safe.isEmpty() ? "unknown" : safe;
    }

    private static Response lecturerErrorResponse(LecturerException exception) {
        return jsonResponse(ApiResponse.error(exception.getStatus(), exception.getMessage(), exception.getErrors()));
    }

    private static Response internalErrorResponse(RuntimeException exception) {
        LOGGER.log(Level.SEVERE, "Unhandled lecturer endpoint failure", exception);
        return jsonResponse(ApiResponse.error(500, INTERNAL_ERROR_MESSAGE));
    }

    private static Response jsonResponse(ApiResponse<?> response) {
        return Response.status(response.getStatus())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(JsonUtil.toJson(response))
                .build();
    }
}
