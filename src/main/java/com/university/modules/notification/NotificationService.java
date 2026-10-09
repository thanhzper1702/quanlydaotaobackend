package com.university.modules.notification;

import com.university.modules.notification.NotificationRequestDTO.CreateNotificationRequestDTO;
import com.university.modules.notification.NotificationRequestDTO.UpdateNotificationRequestDTO;
import com.university.modules.notification.NotificationResponseDTO.CreateNotificationResponseDTO;
import com.university.modules.notification.NotificationResponseDTO.DeleteNotificationResponseDTO;
import com.university.modules.notification.NotificationResponseDTO.NotificationListResponseDTO;

public interface NotificationService {

    NotificationListResponseDTO getNotifications(int page, int limit);

    CreateNotificationResponseDTO createNotification(CreateNotificationRequestDTO request);

    CreateNotificationResponseDTO createNotification(CreateNotificationRequestDTO request, String role);

    CreateNotificationResponseDTO updateNotification(Long id, UpdateNotificationRequestDTO request);

    void updateNotification(Long id, UpdateNotificationRequestDTO request, String role);

    DeleteNotificationResponseDTO deleteNotification(Long id);

    void deleteNotification(Long id, String role);
}
