package com.university.modules.notification;

import com.university.core.model.ThongBao;
import com.university.modules.notification.requestDTO.CreateNotificationRequestDTO;
import com.university.modules.notification.requestDTO.UpdateNotificationRequestDTO;
import com.university.modules.notification.responseDTO.DeleteNotificationResponseDTO;
import com.university.modules.notification.responseDTO.NotificationItemDTO;
import com.university.modules.notification.responseDTO.NotificationListResponseDTO;
import com.university.modules.notification.responseDTO.NotificationMetaDTO;
import com.university.modules.notification.responseDTO.NotificationResponseDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl() {
        this.notificationRepository = new NotificationRepositoryImpl();
    }

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationListResponseDTO getNotifications(int page, int limit) {
        if (page < 1) {
            page = 1;
        }
        if (limit < 1) {
            limit = 10;
        }
        if (limit > 100) {
            limit = 100;
        }

        List<ThongBao> notifications = notificationRepository.findActiveNotificationsPaginated(page, limit);
        long totalItems = notificationRepository.countActiveNotifications();
        int totalPages = limit > 0 ? (int) Math.ceil((double) totalItems / limit) : 0;
        long unreadCount = Math.min(totalItems, 3);

        List<NotificationItemDTO> items = new ArrayList<>();
        for (ThongBao tb : notifications) {
            items.add(new NotificationItemDTO(
                    tb.getMaThongBao(),
                    tb.getTieuDe(),
                    tb.getNoiDung(),
                    tb.getPhamVi() != null ? tb.getPhamVi() : "TOAN_TRUONG",
                    formatDateTime(tb.getNgayTao())
            ));
        }

        NotificationMetaDTO meta = new NotificationMetaDTO(page, limit, totalItems, totalPages, unreadCount);
        return new NotificationListResponseDTO(items, meta);
    }

    @Override
    public NotificationResponseDTO createNotification(CreateNotificationRequestDTO request) {
        return createNotification(request, "TROLY");
    }

    @Override
    public NotificationResponseDTO createNotification(CreateNotificationRequestDTO request, String role) {
        checkManagementRole(role);

        if (request == null) {
            throw new NotificationBusinessException(400, "Dữ liệu yêu cầu không được để trống");
        }
        if (request.getTieuDe() == null || request.getTieuDe().trim().isEmpty()) {
            throw new NotificationBusinessException(400, "Tiêu đề thông báo không được để trống");
        }
        if (request.getTieuDe().trim().length() > 255) {
            throw new NotificationBusinessException(400, "Tiêu đề thông báo không được vượt quá 255 ký tự");
        }
        if (request.getNoiDung() == null || request.getNoiDung().trim().isEmpty()) {
            throw new NotificationBusinessException(400, "Nội dung thông báo không được để trống");
        }

        String phamVi = (request.getPhamVi() != null && !request.getPhamVi().trim().isEmpty())
                ? request.getPhamVi().trim()
                : "TOAN_TRUONG";

        ThongBao thongBao = new ThongBao();
        thongBao.setTieuDe(request.getTieuDe().trim());
        thongBao.setNoiDung(request.getNoiDung().trim());
        thongBao.setPhamVi(phamVi);
        thongBao.setDeleted(false);
        thongBao.setNgayTao(LocalDateTime.now());

        ThongBao saved = notificationRepository.save(thongBao);
        return new NotificationResponseDTO(saved.getMaThongBao(), formatDateTime(saved.getNgayTao()));
    }

    @Override
    public NotificationResponseDTO updateNotification(Long id, UpdateNotificationRequestDTO request) {
        updateNotification(id, request, "TROLY");
        return new NotificationResponseDTO(id, formatDateTime(LocalDateTime.now()));
    }

    @Override
    public void updateNotification(Long id, UpdateNotificationRequestDTO request, String role) {
        checkManagementRole(role);

        if (id == null || id <= 0) {
            throw new NotificationBusinessException(400, "Mã thông báo không hợp lệ");
        }
        if (request == null) {
            throw new NotificationBusinessException(400, "Dữ liệu yêu cầu không được để trống");
        }

        boolean hasTieuDe = request.getTieuDe() != null && !request.getTieuDe().trim().isEmpty();
        boolean hasNoiDung = request.getNoiDung() != null && !request.getNoiDung().trim().isEmpty();

        if (!hasTieuDe && !hasNoiDung) {
            throw new NotificationBusinessException(400, "Cần ít nhất một thông tin tiêu đề hoặc nội dung để cập nhật");
        }

        ThongBao existing = notificationRepository.findActiveById(id)
                .orElseThrow(() -> new NotificationBusinessException(404, "Không tìm thấy thông báo hoặc thông báo đã bị xóa"));

        if (hasTieuDe) {
            if (request.getTieuDe().trim().length() > 255) {
                throw new NotificationBusinessException(400, "Tiêu đề thông báo không được vượt quá 255 ký tự");
            }
            existing.setTieuDe(request.getTieuDe().trim());
        }
        if (hasNoiDung) {
            existing.setNoiDung(request.getNoiDung().trim());
        }

        notificationRepository.update(existing);
    }

    @Override
    public DeleteNotificationResponseDTO deleteNotification(Long id) {
        deleteNotification(id, "TROLY");
        return new DeleteNotificationResponseDTO(true, "Xóa thông báo thành công");
    }

    @Override
    public void deleteNotification(Long id, String role) {
        checkManagementRole(role);

        if (id == null || id <= 0) {
            throw new NotificationBusinessException(400, "Mã thông báo không hợp lệ");
        }

        notificationRepository.findActiveById(id)
                .orElseThrow(() -> new NotificationBusinessException(404, "Không tìm thấy thông báo cần xóa"));

        notificationRepository.softDelete(id);
    }

    private void checkManagementRole(String role) {
        if (role == null || (!"TROLY".equalsIgnoreCase(role) && !"TRUONGPHONG".equalsIgnoreCase(role))) {
            throw new NotificationBusinessException(403, "Bạn không có quyền thực hiện chức năng này");
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(ISO_FORMATTER) + "Z";
    }
}
