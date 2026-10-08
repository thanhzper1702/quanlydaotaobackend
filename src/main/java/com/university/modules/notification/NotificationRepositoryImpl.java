package com.university.modules.notification;

import com.university.core.model.ThongBao;
import com.university.core.repository.AbstractJpaRepository;
import java.util.List;
import java.util.Optional;

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

    @Override
    public List<ThongBao> findActiveNotificationsPaginated(int page, int limit) {
        int firstResult = Math.max(0, (page - 1) * limit);
        return execute(em -> em.createQuery(
                "SELECT t FROM ThongBao t WHERE t.deleted = false ORDER BY t.ngayTao DESC", ThongBao.class)
                .setFirstResult(firstResult)
                .setMaxResults(limit)
                .getResultList());
    }

    @Override
    public long countActiveNotifications() {
        return execute(em -> em.createQuery(
                "SELECT COUNT(t) FROM ThongBao t WHERE t.deleted = false", Long.class)
                .getSingleResult());
    }

    @Override
    public Optional<ThongBao> findActiveById(Long id) {
        return execute(em -> {
            List<ThongBao> list = em.createQuery(
                    "SELECT t FROM ThongBao t WHERE t.maThongBao = :id AND t.deleted = false", ThongBao.class)
                    .setParameter("id", id)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    @Override
    public void softDelete(Long id) {
        executeInTransaction(em -> {
            ThongBao entity = em.find(ThongBao.class, id);
            if (entity != null) {
                entity.setDeleted(true);
                em.merge(entity);
            }
        });
    }
}
