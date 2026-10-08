package com.university.modules.notification;

import com.university.core.common.ApiResponse;
import com.university.core.common.BaseController;
import com.university.modules.notification.requestDTO.CreateNotificationRequestDTO;
import com.university.modules.notification.requestDTO.UpdateNotificationRequestDTO;
import com.university.modules.notification.responseDTO.NotificationListResponseDTO;
import com.university.modules.notification.responseDTO.NotificationResponseDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/notifications")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    public NotificationController() {
        this.notificationService = new NotificationServiceImpl();
    }

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GET
    public Response getNotifications(@QueryParam("page") @DefaultValue("1") int page,
                                     @QueryParam("limit") @DefaultValue("10") int limit) {
        try {
            String role = getAuthenticatedRole();
            String cccd = getAuthenticatedUserCccd();
            String userCode = getAuthenticatedUserCode();

            if ((role == null || role.isBlank()) && (cccd == null || cccd.isBlank()) && (userCode == null || userCode.isBlank())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error(401, "Người dùng chưa được xác thực hoặc phiên đăng nhập đã hết hạn"))
                        .build();
            }

            NotificationListResponseDTO result = notificationService.getNotifications(page, limit);
            return Response.ok(ApiResponse.success(
                    200,
                    "Danh sách thông báo kèm meta phân trang",
                    result.getNotifications(),
                    result.getMeta()
            )).build();
        } catch (NotificationBusinessException e) {
            return Response.status(e.getStatusCode())
                    .entity(ApiResponse.error(e.getStatusCode(), e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error(500, "Lỗi máy chủ nội bộ: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    public Response createNotification(CreateNotificationRequestDTO request) {
        try {
            String role = getAuthenticatedRole();
            String cccd = getAuthenticatedUserCccd();
            String userCode = getAuthenticatedUserCode();

            if ((role == null || role.isBlank()) && (cccd == null || cccd.isBlank()) && (userCode == null || userCode.isBlank())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error(401, "Người dùng chưa được xác thực hoặc phiên đăng nhập đã hết hạn"))
                        .build();
            }

            NotificationResponseDTO responseData = notificationService.createNotification(request, role);
            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success(201, "Phát hành thông báo thành công", responseData))
                    .build();
        } catch (NotificationBusinessException e) {
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
    @Path("/{id}")
    public Response updateNotification(@PathParam("id") Long id, UpdateNotificationRequestDTO request) {
        try {
            String role = getAuthenticatedRole();
            String cccd = getAuthenticatedUserCccd();
            String userCode = getAuthenticatedUserCode();

            if ((role == null || role.isBlank()) && (cccd == null || cccd.isBlank()) && (userCode == null || userCode.isBlank())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error(401, "Người dùng chưa được xác thực hoặc phiên đăng nhập đã hết hạn"))
                        .build();
            }

            notificationService.updateNotification(id, request, role);
            return Response.ok(ApiResponse.success(200, "Cập nhật thành công", null)).build();
        } catch (NotificationBusinessException e) {
            return Response.status(e.getStatusCode())
                    .entity(ApiResponse.error(e.getStatusCode(), e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error(500, "Lỗi máy chủ nội bộ: " + e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteNotification(@PathParam("id") Long id) {
        try {
            String role = getAuthenticatedRole();
            String cccd = getAuthenticatedUserCccd();
            String userCode = getAuthenticatedUserCode();

            if ((role == null || role.isBlank()) && (cccd == null || cccd.isBlank()) && (userCode == null || userCode.isBlank())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error(401, "Người dùng chưa được xác thực hoặc phiên đăng nhập đã hết hạn"))
                        .build();
            }

            notificationService.deleteNotification(id, role);
            return Response.ok(ApiResponse.success(200, "Xóa thông báo thành công", null)).build();
        } catch (NotificationBusinessException e) {
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
