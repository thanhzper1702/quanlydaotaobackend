package com.university.core.model;

import com.university.core.model.enums.TenHocKy;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "HocKy")
public class HocKy extends BaseEntity {

    @Id
    @Column(name = "maHocKy", length = 20, nullable = false)
    private String maHocKy;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenHocKy", length = 20, nullable = false)
    private TenHocKy tenHocKy;

    @Column(name = "thoiGianBD", nullable = false)
    private LocalDate thoiGianBD;

    @Column(name = "thoiGianKT", nullable = false)
    private LocalDate thoiGianKT;

    @Column(name = "is_current")
    private Boolean isCurrent = false;

    @Column(name = "manual_override")
    private Boolean manualOverride = false;

    @Column(name = "updated_by_tro_ly", length = 50)
    private String updatedByTroLy;

    @Column(name = "updated_at")
    private java.time.LocalDateTime thoiGianCapNhat;

    public HocKy() {
    }

    public String getMaHocKy() {
        return maHocKy;
    }

    public void setMaHocKy(String maHocKy) {
        this.maHocKy = maHocKy;
    }

    public TenHocKy getTenHocKy() {
        return tenHocKy;
    }

    public void setTenHocKy(TenHocKy tenHocKy) {
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

    public boolean isCurrent() {
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

    public String getUpdatedByTroLy() {
        return updatedByTroLy;
    }

    public void setUpdatedByTroLy(String updatedByTroLy) {
        this.updatedByTroLy = updatedByTroLy;
    }

    public java.time.LocalDateTime getThoiGianCapNhat() {
        return thoiGianCapNhat;
    }

    public void setThoiGianCapNhat(java.time.LocalDateTime thoiGianCapNhat) {
        this.thoiGianCapNhat = thoiGianCapNhat;
        setUpdatedAt(thoiGianCapNhat);
    }

    // Compatibility aliases
    public LocalDate getNgayBatDau() {
        return thoiGianBD;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.thoiGianBD = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return thoiGianKT;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.thoiGianKT = ngayKetThuc;
    }

    public boolean isExpired() {
        return thoiGianKT != null && LocalDate.now().isAfter(thoiGianKT);
    }

    public boolean isHienTai() {
        if (Boolean.TRUE.equals(this.isCurrent)) {
            return true;
        }
        return isWithinSemester(LocalDate.now());
    }

    public boolean isWithinSemester(LocalDate date) {
        if (date == null || thoiGianBD == null || thoiGianKT == null) {
            return false;
        }
        return !date.isBefore(thoiGianBD) && !date.isAfter(thoiGianKT);
    }
}
