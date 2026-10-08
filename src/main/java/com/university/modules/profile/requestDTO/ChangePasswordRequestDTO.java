package com.university.modules.profile.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangePasswordRequestDTO {

    @JsonProperty("matKhauCu")
    private String matKhauCu;

    @JsonProperty("matKhauMoi")
    private String matKhauMoi;

    public ChangePasswordRequestDTO() {
    }

    public ChangePasswordRequestDTO(String matKhauCu, String matKhauMoi) {
        this.matKhauCu = matKhauCu;
        this.matKhauMoi = matKhauMoi;
    }

    public String getMatKhauCu() {
        return matKhauCu;
    }

    public void setMatKhauCu(String matKhauCu) {
        this.matKhauCu = matKhauCu;
    }

    public String getMatKhauMoi() {
        return matKhauMoi;
    }

    public void setMatKhauMoi(String matKhauMoi) {
        this.matKhauMoi = matKhauMoi;
    }
}
