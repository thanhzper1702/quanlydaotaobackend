package com.university.modules.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Gom nhóm toàn bộ Response DTO của phân hệ Notification.
 */
public final class NotificationResponseDTO {

    private NotificationResponseDTO() {
    }

    public static class CreateNotificationResponseDTO {

        @JsonProperty("maThongBao")
        private Long maThongBao;

        @JsonProperty("ngayTao")
        private String ngayTao;

        public CreateNotificationResponseDTO() {
        }

        public CreateNotificationResponseDTO(Long maThongBao, String ngayTao) {
            this.maThongBao = maThongBao;
            this.ngayTao = ngayTao;
        }

        public Long getMaThongBao() {
            return maThongBao;
        }

        public void setMaThongBao(Long maThongBao) {
            this.maThongBao = maThongBao;
        }

        public String getNgayTao() {
            return ngayTao;
        }

        public void setNgayTao(String ngayTao) {
            this.ngayTao = ngayTao;
        }
    }

    public static class NotificationListResponseDTO {

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

    public static class NotificationItemDTO {

        @JsonProperty("maThongBao")
        private Long maThongBao;

        @JsonProperty("tieuDe")
        private String tieuDe;

        @JsonProperty("noiDung")
        private String noiDung;

        @JsonProperty("phamVi")
        private String phamVi;

        @JsonProperty("ngayTao")
        private String ngayTao;

        public NotificationItemDTO() {
        }

        public NotificationItemDTO(Long maThongBao, String tieuDe, String noiDung, String phamVi, String ngayTao) {
            this.maThongBao = maThongBao;
            this.tieuDe = tieuDe;
            this.noiDung = noiDung;
            this.phamVi = phamVi;
            this.ngayTao = ngayTao;
        }

        public Long getMaThongBao() {
            return maThongBao;
        }

        public void setMaThongBao(Long maThongBao) {
            this.maThongBao = maThongBao;
        }

        public String getTieuDe() {
            return tieuDe;
        }

        public void setTieuDe(String tieuDe) {
            this.tieuDe = tieuDe;
        }

        public String getNoiDung() {
            return noiDung;
        }

        public void setNoiDung(String noiDung) {
            this.noiDung = noiDung;
        }

        public String getPhamVi() {
            return phamVi;
        }

        public void setPhamVi(String phamVi) {
            this.phamVi = phamVi;
        }

        public String getNgayTao() {
            return ngayTao;
        }

        public void setNgayTao(String ngayTao) {
            this.ngayTao = ngayTao;
        }
    }

    public static class NotificationMetaDTO {

        @JsonProperty("page")
        private int page;

        @JsonProperty("pageSize")
        private int pageSize;

        @JsonProperty("totalItems")
        private long totalItems;

        @JsonProperty("totalPages")
        private int totalPages;

        @JsonProperty("unreadCount")
        private long unreadCount;

        public NotificationMetaDTO() {
        }

        public NotificationMetaDTO(int page, int pageSize, long totalItems, int totalPages, long unreadCount) {
            this.page = page;
            this.pageSize = pageSize;
            this.totalItems = totalItems;
            this.totalPages = totalPages;
            this.unreadCount = unreadCount;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public long getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(long totalItems) {
            this.totalItems = totalItems;
        }

        public int totalPages() {
            return totalPages;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public long getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(long unreadCount) {
            this.unreadCount = unreadCount;
        }
    }

    public static class DeleteNotificationResponseDTO {

        private boolean success;
        private String message;

        public DeleteNotificationResponseDTO() {
        }

        public DeleteNotificationResponseDTO(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
