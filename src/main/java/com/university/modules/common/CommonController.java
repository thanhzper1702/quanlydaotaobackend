package com.university.modules.common;

import com.university.core.common.ApiResponse;
import com.university.core.common.BaseController;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/common")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommonController extends BaseController {

    private final SemesterService semesterService;

    public CommonController() {
        this.semesterService = new SemesterServiceImpl();
    }

    public CommonController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @GET
    @Path("/current-semester")
    public Response getCurrentSemester() {
        SemesterDTO dto = semesterService.getCurrentSemester();
        if (dto == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(404, "Không tìm thấy học kỳ hiện hành"))
                    .build();
        }
        return Response.ok(ApiResponse.success(200, "Lấy học kỳ hiện hành thành công", dto)).build();
    }

    @GET
    @Path("/semesters")
    public Response getAllSemesters() {
        List<SemesterDTO> list = semesterService.getAllSemesters();
        return Response.ok(ApiResponse.success(200, "Lấy danh sách học kỳ thành công", list)).build();
    }

    @POST
    @Path("/switch-semester")
    public Response switchSemester(Map<String, String> body) {
        String maHocKy = body != null ? body.get("maHocKy") : null;
        if (maHocKy == null || maHocKy.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(400, "Mã học kỳ không được để trống"))
                    .build();
        }
        String userCode = getAuthenticatedUserCode();
        boolean success = semesterService.switchCurrentSemester(maHocKy, userCode != null ? userCode : "ASSISTANT");
        if (!success) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(400, "Không thể chuyển học kỳ hiện hành"))
                    .build();
        }
        return Response.ok(ApiResponse.success(200, "Chuyển học kỳ hiện hành thành công", null)).build();
    }
}
