package com.university.core.model;

import com.university.core.model.enums.TenHocKy;
import jakarta.persistence.*;
import java.time.LocalDate;

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

    public boolean isHienTai() {
        return isWithinSemester(LocalDate.now());
    }

    public boolean isWithinSemester(LocalDate date) {
        if (date == null || thoiGianBD == null || thoiGianKT == null) {
            return false;
        }
        return !date.isBefore(thoiGianBD) && !date.isAfter(thoiGianKT);
    }
}
