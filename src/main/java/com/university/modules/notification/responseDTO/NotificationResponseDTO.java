package com.university.modules.notification.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationResponseDTO {

    @JsonProperty("maThongBao")
    private Long maThongBao;

    @JsonProperty("ngayTao")
    private String ngayTao;

    public NotificationResponseDTO() {
    }

    public NotificationResponseDTO(Long maThongBao, String ngayTao) {
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
