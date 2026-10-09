package com.university.modules.notification.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateNotificationRequestDTO {

    @JsonProperty("tieuDe")
    private String tieuDe;

    @JsonProperty("noiDung")
    private String noiDung;

    @JsonProperty("phamVi")
    private String phamVi;

    public CreateNotificationRequestDTO() {
    }

    public CreateNotificationRequestDTO(String tieuDe, String noiDung, String phamVi) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.phamVi = phamVi;
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
}
