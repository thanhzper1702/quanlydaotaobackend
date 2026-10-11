package com.university.modules.common;

import java.time.LocalDate;

public class SemesterDTO {

    private String maHocKy;
    private String tenHocKy;
    private LocalDate thoiGianBD;
    private LocalDate thoiGianKT;
    private Boolean isCurrent;
    private Boolean manualOverride;

    public SemesterDTO() {
    }

    public SemesterDTO(String maHocKy, String tenHocKy, LocalDate thoiGianBD, LocalDate thoiGianKT, Boolean isCurrent, Boolean manualOverride) {
        this.maHocKy = maHocKy;
        this.tenHocKy = tenHocKy;
        this.thoiGianBD = thoiGianBD;
        this.thoiGianKT = thoiGianKT;
        this.isCurrent = isCurrent;
        this.manualOverride = manualOverride;
    }

    public String getMaHocKy() {
        return maHocKy;
    }

    public void setMaHocKy(String maHocKy) {
        this.maHocKy = maHocKy;
    }

    public String getTenHocKy() {
        return tenHocKy;
    }

    public void setTenHocKy(String tenHocKy) {
        this.tenHocKy = tenHocKy;
    }

    public LocalDate getThoiGianBD() {
        return thoiGianBD;
    }

    public void setThoiGianBD(LocalDate thoiGianBD) {
        this.thoiGianBD = thoiGianBD;
    }

    public LocalDate getThoiGianKT() {
        return thoiGianKT;
    }

    public void setThoiGianKT(LocalDate thoiGianKT) {
        this.thoiGianKT = thoiGianKT;
    }

    public Boolean getIsCurrent() {
        return isCurrent != null && isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public Boolean getManualOverride() {
        return manualOverride != null && manualOverride;
    }

    public void setManualOverride(Boolean manualOverride) {
        this.manualOverride = manualOverride;
    }
}
