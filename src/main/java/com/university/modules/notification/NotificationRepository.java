package com.university.modules.notification;

import com.university.core.model.ThongBao;
import com.university.core.repository.GenericRepository;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends GenericRepository<ThongBao, Long> {
    List<ThongBao> findActiveNotifications();
    List<ThongBao> findNotificationsByScope(String phamVi);
    List<ThongBao> findActiveNotificationsPaginated(int page, int limit);
    long countActiveNotifications();
    Optional<ThongBao> findActiveById(Long id);
    void softDelete(Long id);
}
