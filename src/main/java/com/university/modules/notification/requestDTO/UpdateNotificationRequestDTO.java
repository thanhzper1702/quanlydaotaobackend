package com.university.modules.notification.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateNotificationRequestDTO {

    @JsonProperty("tieuDe")
    private String tieuDe;

    @JsonProperty("noiDung")
    private String noiDung;

    public UpdateNotificationRequestDTO() {
    }

    public UpdateNotificationRequestDTO(String tieuDe, String noiDung) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
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
}
