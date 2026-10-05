package com.university.modules.notification;

import com.university.core.model.ThongBao;
import com.university.core.repository.GenericRepository;
import java.util.List;

public interface NotificationRepository extends GenericRepository<ThongBao, Long> {
    List<ThongBao> findActiveNotifications();
    List<ThongBao> findNotificationsByScope(String phamVi);
}
