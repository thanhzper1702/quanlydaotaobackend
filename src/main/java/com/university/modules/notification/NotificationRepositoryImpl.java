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
                "SELECT t FROM ThongBao t WHERE (t.deleted = false OR t.deleted IS NULL) ORDER BY COALESCE(t.ngayTao, CURRENT_TIMESTAMP) DESC, t.maThongBao DESC", ThongBao.class)
                .getResultList());
    }

    @Override
    public List<ThongBao> findNotificationsByScope(String phamVi) {
        return execute(em -> em.createQuery(
                "SELECT t FROM ThongBao t WHERE (t.phamVi = :phamVi OR t.phamVi = 'ALL') AND (t.deleted = false OR t.deleted IS NULL) ORDER BY COALESCE(t.ngayTao, CURRENT_TIMESTAMP) DESC, t.maThongBao DESC", ThongBao.class)
                .setParameter("phamVi", phamVi)
                .getResultList());
    }

    @Override
    public List<ThongBao> findActiveNotificationsPaginated(int page, int limit) {
        int firstResult = Math.max(0, (page - 1) * limit);
        return execute(em -> em.createQuery(
                "SELECT t FROM ThongBao t WHERE (t.deleted = false OR t.deleted IS NULL) ORDER BY COALESCE(t.ngayTao, CURRENT_TIMESTAMP) DESC, t.maThongBao DESC", ThongBao.class)
                .setFirstResult(firstResult)
                .setMaxResults(limit)
                .getResultList());
    }

    @Override
    public long countActiveNotifications() {
        return execute(em -> em.createQuery(
                "SELECT COUNT(t) FROM ThongBao t WHERE (t.deleted = false OR t.deleted IS NULL)", Long.class)
                .getSingleResult());
    }

    @Override
    public Optional<ThongBao> findActiveById(Long id) {
        return execute(em -> {
            List<ThongBao> list = em.createQuery(
                    "SELECT t FROM ThongBao t WHERE t.maThongBao = :id AND (t.deleted = false OR t.deleted IS NULL)", ThongBao.class)
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

    @Override
    public void seedInitialNotificationsIfEmpty() {
        executeInTransaction(em -> {
            Long count = em.createQuery("SELECT COUNT(t) FROM ThongBao t WHERE (t.deleted = false OR t.deleted IS NULL)", Long.class)
                    .getSingleResult();
            if (count == 0) {
                ThongBao tb1 = new ThongBao();
                tb1.setTieuDe("Thông báo đăng ký học phần Học kỳ 1 năm học 2025 - 2026");
                tb1.setNoiDung("Sinh viên khóa 2021, 2022 thực hiện đăng ký học phần qua cổng thông tin từ ngày 15/08/2025.");
                tb1.setPhamVi("TOAN_TRUONG");
                tb1.setDeleted(false);
                tb1.setNgayTao(java.time.LocalDateTime.now().minusHours(2));
                em.persist(tb1);

                ThongBao tb2 = new ThongBao();
                tb2.setTieuDe("Thông báo đăng ký thi học kỳ chính thức");
                tb2.setNoiDung("Sinh viên khóa 2021, 2022 thực hiện kiểm tra lịch thi học phần qua cổng thông tin từ ngày 15/08/2025.");
                tb2.setPhamVi("TOAN_TRUONG");
                tb2.setDeleted(false);
                tb2.setNgayTao(java.time.LocalDateTime.now().minusHours(4));
                em.persist(tb2);

                ThongBao tb3 = new ThongBao();
                tb3.setTieuDe("Thông báo nộp học phí và chính sách miễn giảm");
                tb3.setNoiDung("Thời hạn nộp học phí học kỳ 1 năm học 2025 - 2026 đến hết ngày 30/11/2025.");
                tb3.setPhamVi("TOAN_TRUONG");
                tb3.setDeleted(false);
                tb3.setNgayTao(java.time.LocalDateTime.now().minusHours(6));
                em.persist(tb3);
            }
        });
    }
}
