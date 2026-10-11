package com.university.modules.profile;

import com.university.core.common.ApiResponse;
import com.university.core.common.BaseController;
import com.university.modules.profile.ProfileRequestDTO.ChangePasswordRequestDTO;
import com.university.modules.profile.ProfileResponseDTO.UserProfileResponseDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/profile")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfileController extends BaseController {

    private final ProfileService profileService;

    public ProfileController() {
        this.profileService = new ProfileServiceImpl();
    }

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GET
    @Path("/programs")
    public Response getPrograms() {
        java.util.List<java.util.Map<String, Object>> programs = java.util.List.of(
            java.util.Map.of("maCT", "24110", "tenCT", "Kỹ thuật phần mềm", "khoa", "Công nghệ thông tin", "soTinChi", 126),
            java.util.Map.of("maCT", "24111", "tenCT", "Khoa học máy tính", "khoa", "Công nghệ thông tin", "soTinChi", 126),
            java.util.Map.of("maCT", "24112", "tenCT", "Hệ thống thông tin", "khoa", "Công nghệ thông tin", "soTinChi", 126),
            java.util.Map.of("maCT", "24113", "tenCT", "Mạng máy tính & Truyền thông", "khoa", "Công nghệ thông tin", "soTinChi", 126),
            java.util.Map.of("maCT", "24114", "tenCT", "An toàn thông tin", "khoa", "Công nghệ thông tin", "soTinChi", 126)
        );
        return Response.ok(ApiResponse.success(200, "Danh sách chương trình đào tạo", programs)).build();
    }

    @GET
    public Response getProfileRoot() {
        return getProfile();
    }

    @GET
    @Path("/me")
    public Response getProfile() {
        try {
            String cccd = getAuthenticatedUserCccd();
            String userCode = getAuthenticatedUserCode();
            String role = getAuthenticatedRole();

            if ((cccd == null || cccd.isBlank()) && (userCode == null || userCode.isBlank())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error(401, "Người dùng chưa được xác thực hoặc phiên đăng nhập đã hết hạn"))
                        .build();
            }

            UserProfileResponseDTO profile = profileService.getProfile(cccd, userCode, role);
            return Response.ok(ApiResponse.success(200, "Thông tin người dùng chi tiết", profile)).build();
        } catch (ProfileBusinessException e) {
            return Response.status(e.getStatusCode())
                    .entity(ApiResponse.error(e.getStatusCode(), e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error(500, "Lỗi máy chủ nội bộ: " + e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/change-password")
    public Response changePassword(ChangePasswordRequestDTO request) {
        try {
            String cccd = getAuthenticatedUserCccd();
            if (cccd == null || cccd.isBlank()) {
                cccd = getAuthenticatedUserCode();
            }

            if (cccd == null || cccd.isBlank()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error(401, "Người dùng chưa được xác thực hoặc phiên đăng nhập đã hết hạn"))
                        .build();
            }

            profileService.changePassword(cccd, request);
            return Response.ok(ApiResponse.success(200, "Đổi mật khẩu thành công", null)).build();
        } catch (ProfileBusinessException e) {
            return Response.status(e.getStatusCode())
                    .entity(ApiResponse.error(e.getStatusCode(), e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error(500, "Lỗi máy chủ nội bộ: " + e.getMessage()))
                    .build();
        }
    }
}
