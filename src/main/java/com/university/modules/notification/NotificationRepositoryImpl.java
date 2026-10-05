package com.university.modules.notification;

import com.university.core.model.ThongBao;
import com.university.core.repository.AbstractJpaRepository;
import java.util.List;

public class NotificationRepositoryImpl extends AbstractJpaRepository<ThongBao, Long> implements NotificationRepository {

    public NotificationRepositoryImpl() {
        super(ThongBao.class);
    }

    @Override
    public List<ThongBao> findActiveNotifications() {
        return execute(em -> em.createQuery(
                "SELECT t FROM ThongBao t WHERE t.deleted = false ORDER BY t.ngayTao DESC", ThongBao.class)
                .getResultList());
    }

    @Override
    public List<ThongBao> findNotificationsByScope(String phamVi) {
        return execute(em -> em.createQuery(
                "SELECT t FROM ThongBao t WHERE (t.phamVi = :phamVi OR t.phamVi = 'ALL') AND t.deleted = false ORDER BY t.ngayTao DESC", ThongBao.class)
                .setParameter("phamVi", phamVi)
                .getResultList());
    }
}
