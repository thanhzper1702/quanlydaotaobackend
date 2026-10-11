package com.university.modules.headoffice;
import com.university.modules.headoffice.HeadOfficeRequestDTO.CreateCourseRequestDTO;
import com.university.modules.headoffice.HeadOfficeRequestDTO.CreateSemesterRequestDTO;
import com.university.modules.headoffice.HeadOfficeRequestDTO.UpdateCourseRequestDTO;


import com.university.core.common.BaseController;
import com.university.core.common.ApiResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        return execute(() -> {
            HeadOfficeResponseDTO.HeadOfficeCourseResponseDTO data = headOfficeService.getCourses(page, limit);
            ApiResponse<Object> response = ApiResponse.success("Danh mục học phần gốc", data == null ? List.of() : data.getItems());
            response.setMeta(buildPaginationMeta(page, limit, data == null ? 0 : data.getTotalElements()));
            return Response.ok(response).build();
        });
    }

    @POST
    @Path("/courses")
    public Response createCourse(CreateCourseRequestDTO request) {
        return execute(() -> {
            headOfficeService.createCourse(request);
            return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(Response.Status.CREATED.getStatusCode(), "Tạo học phần thành công", null)).build();
        });
    }

    @PUT
    @Path("/courses/{maHP}")
    public Response updateCourse(@PathParam("maHP") String maHP, UpdateCourseRequestDTO request) {
        return execute(() -> {
            headOfficeService.updateCourse(maHP, request);
            return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(Response.Status.CREATED.getStatusCode(), "chỉnh sửa học phần thành công", null)).build();
        });
    }

    @GET
    @Path("/semesters")
    public Response getSemesters() {
        return execute(() -> {
            HeadOfficeResponseDTO.HeadOfficeSemesterResponseDTO data = headOfficeService.getSemesters();
            ApiResponse<Object> response = ApiResponse.success("Toàn bộ danh sách học kỳ", data == null ? List.of() : data.getItems());
            long total = data == null ? 0 : data.getTotalElements();
            response.setMeta(Map.of(
                "tongSoHocKy", total,
                "totalItems", total,
                "totalPages", 1,
                "page", 1
            ));
            return Response.ok(response).build();
        });
    }

    @POST
    @Path("/semesters")
    public Response createSemester(CreateSemesterRequestDTO request) {
        return execute(() -> {
            headOfficeService.createSemester(request);
            return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(Response.Status.CREATED.getStatusCode(), "Tạo học kỳ thành công", null)).build();
        });
    }

    @GET
    @Path("/lecturers")
    public Response getLecturers(@QueryParam("khoa") String khoa,
                                 @QueryParam("page") @DefaultValue("1") int page,
                                 @QueryParam("limit") @DefaultValue("10") int limit) {
        return execute(() -> {
            HeadOfficeResponseDTO.HeadOfficeLecturerResponseDTO data = headOfficeService.getLecturers(khoa, page, limit);
            ApiResponse<Object> response = ApiResponse.success("Danh sách giảng viên kèm meta", data == null ? List.of() : data.getItems());
            response.setMeta(buildPaginationMeta(page, limit, data == null ? 0 : data.getTotalElements()));
            return Response.ok(response).build();
        });
    }

    @GET
    @Path("/students")
    public Response getStudents(@QueryParam("nienKhoa") String nienKhoa,
                                @QueryParam("khoa") String khoa,
                                @QueryParam("keyword") String keyword,
                                @QueryParam("page") @DefaultValue("1") int page,
                                @QueryParam("limit") @DefaultValue("10") int limit) {
        return execute(() -> {
            HeadOfficeResponseDTO.HeadOfficeStudentResponseDTO data = headOfficeService.getStudents(nienKhoa, khoa, keyword, page, limit);
            ApiResponse<Object> response = ApiResponse.success("Danh sách sinh viên kèm meta", data == null ? List.of() : data.getItems());
            response.setMeta(buildPaginationMeta(page, limit, data == null ? 0 : data.getTotalElements()));
            return Response.ok(response).build();
        });
    }

    @GET
    @Path("/statistics")
    public Response getStatistics(@QueryParam("maHocKy") String maHocKy) {
        return execute(() -> {
            HeadOfficeResponseDTO.HeadOfficeStatisticsResponseDTO statistics = headOfficeService.getStatistics(maHocKy);
            Map<String, Object> legacyData = new LinkedHashMap<>();
            legacyData.put("tongSinhVienToanTruong", statistics.getTotalStudents());
            legacyData.put("tongSinhVien", statistics.getTotalStudents());
            legacyData.put("totalStudents", statistics.getTotalStudents());
            legacyData.put("tongGiangVien", statistics.getTotalLecturers());
            legacyData.put("totalLecturers", statistics.getTotalLecturers());
            legacyData.put("tongHocPhanToanTruong", statistics.getTotalCourses());
            legacyData.put("totalCourses", statistics.getTotalCourses());
            legacyData.put("tongLopHocPhan", statistics.getTotalClasses());
            legacyData.put("totalClasses", statistics.getTotalClasses());
            legacyData.put("tyLeHoanThanhDKHP", 98.5);
            legacyData.put("sinhVienTheoKhoa", statistics.getStudentsByKhoa());
            legacyData.put("sinhVienTheoGioiTinh", statistics.getStudentsByGender());
            legacyData.put("xepLoaiHocLuc", toLegacyAcademicResults(statistics.getAcademicResults()));
            legacyData.put("soLuongDinhChiThi", statistics.getSuspendedExamRecords());
            legacyData.put("soLuongRotMon", statistics.getFailedGrades());

            List<Map<String, Object>> thongKeKhoaList = new ArrayList<>();
            if (statistics.getStudentsByKhoa() != null) {
                for (Map.Entry<String, Long> entry : statistics.getStudentsByKhoa().entrySet()) {
                    Map<String, Object> k = new LinkedHashMap<>();
                    k.put("tenKhoa", entry.getKey());
                    k.put("maKhoa", entry.getKey());
                    k.put("soLop", 15);
                    k.put("soSVDK", entry.getValue());
                    k.put("tyLe", 95);
                    thongKeKhoaList.add(k);
                }
            }
            legacyData.put("thongKeKhoa", thongKeKhoaList);

            ApiResponse<Object> response = ApiResponse.success("Dữ liệu thống kê tổng hợp toàn trường", legacyData);
            response.setMeta(buildStatisticsMeta(maHocKy));
            return Response.ok(response).build();
        });
    }

    private static Map<String, Object> buildPaginationMeta(int page, int limit, long totalItems) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("page", page);
        meta.put("pageSize", limit);
        meta.put("totalItems", totalItems);
        meta.put("totalPages", limit > 0 ? (int) Math.ceil((double) totalItems / limit) : 0);
        return meta;
    }

    private static Map<String, Object> buildStatisticsMeta(String maHocKy) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("maHocKy", maHocKy);
        meta.put("ngayXuatThongKe", LocalDateTime.now(ZoneOffset.UTC).toString());
        meta.put("nguoiTaoThongKe", "Trưởng phòng Đào tạo - PGS.TS Trần Văn Hùng");
        return meta;
    }

    private static Map<String, Long> toLegacyAcademicResults(Map<String, Long> source) {
        if (source == null || source.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, Long> result = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : source.entrySet()) {
            String normalized = entry.getKey() == null ? "" : entry.getKey().trim();
            String legacyKey = switch (normalized.toUpperCase()) {
                case "XUAT_SAC" -> "XuatSac";
                case "GIOI" -> "Gioi";
                case "KHA" -> "Kha";
                case "TRUNG_BINH" -> "TrungBinh";
                case "YEU" -> "Yeu";
                case "ROT" -> "Rot";
                default -> normalized;
            };
            result.merge(legacyKey, entry.getValue(), Long::sum);
        }
        return result;
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
