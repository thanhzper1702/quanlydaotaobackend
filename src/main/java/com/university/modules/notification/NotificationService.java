package com.university.modules.notification;
import com.university.modules.notification.requestDTO.*;
import com.university.modules.notification.responseDTO.*;


public interface NotificationService {

    NotificationListResponseDTO getNotifications(int page, int limit);

    NotificationResponseDTO createNotification(CreateNotificationRequestDTO request);

    NotificationResponseDTO updateNotification(Long id, UpdateNotificationRequestDTO request);

    DeleteNotificationResponseDTO deleteNotification(Long id);
}
