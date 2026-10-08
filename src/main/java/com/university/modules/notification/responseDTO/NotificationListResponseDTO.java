package com.university.modules.notification.responseDTO;

import java.util.List;

public class NotificationListResponseDTO {

    private List<NotificationItemDTO> notifications;
    private NotificationMetaDTO meta;

    public NotificationListResponseDTO() {
    }

    public NotificationListResponseDTO(List<NotificationItemDTO> notifications, NotificationMetaDTO meta) {
        this.notifications = notifications;
        this.meta = meta;
    }

    public List<NotificationItemDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationItemDTO> notifications) {
        this.notifications = notifications;
    }

    public NotificationMetaDTO getMeta() {
        return meta;
    }

    public void setMeta(NotificationMetaDTO meta) {
        this.meta = meta;
    }
}
