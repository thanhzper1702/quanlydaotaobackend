package com.university.modules.notification;
import com.university.modules.notification.requestDTO.*;
import com.university.modules.notification.responseDTO.*;


import com.university.core.common.BaseController;
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
        return null;
    }

    @POST
    public Response createNotification(CreateNotificationRequestDTO request) {
        return null;
    }

    @PUT
    @Path("/{id}")
    public Response updateNotification(@PathParam("id") Long id, UpdateNotificationRequestDTO request) {
        return null;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteNotification(@PathParam("id") Long id) {
        return null;
    }
}
