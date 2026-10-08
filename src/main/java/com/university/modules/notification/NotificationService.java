package com.university.modules.notification;

import com.university.modules.notification.requestDTO.CreateNotificationRequestDTO;
import com.university.modules.notification.requestDTO.UpdateNotificationRequestDTO;
import com.university.modules.notification.responseDTO.DeleteNotificationResponseDTO;
import com.university.modules.notification.responseDTO.NotificationListResponseDTO;
import com.university.modules.notification.responseDTO.NotificationResponseDTO;

public interface NotificationService {

    NotificationListResponseDTO getNotifications(int page, int limit);

    NotificationResponseDTO createNotification(CreateNotificationRequestDTO request);

    NotificationResponseDTO createNotification(CreateNotificationRequestDTO request, String role);

    NotificationResponseDTO updateNotification(Long id, UpdateNotificationRequestDTO request);

    void updateNotification(Long id, UpdateNotificationRequestDTO request, String role);

    DeleteNotificationResponseDTO deleteNotification(Long id);

    void deleteNotification(Long id, String role);
}
