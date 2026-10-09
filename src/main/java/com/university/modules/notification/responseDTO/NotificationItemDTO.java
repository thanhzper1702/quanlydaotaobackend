package com.university.modules.notification.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationItemDTO {

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
