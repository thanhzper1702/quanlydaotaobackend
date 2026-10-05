package com.university.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "GiangVien")
@PrimaryKeyJoinColumn(name = "maNV")
public class GiangVien extends NhanVien {

    @Column(name = "hocVi", length = 50)
    private String hocVi;

    @Column(name = "khoa", length = 100, nullable = false)
    private String khoa;

    @Column(name = "ngayBatDau")
    private LocalDate ngayBatDau;

    public GiangVien() {
    }

    public String getHocVi() {
        return hocVi;
    }

    public void setHocVi(String hocVi) {
        this.hocVi = hocVi;
    }

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    // Compatibility aliases
    public String getMaGV() {
        return getMaNV();
    }

    public void setMaGV(String maGV) {
        setMaNV(maGV);
    }
}
