package com.university.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "NhanVien")
@Inheritance(strategy = InheritanceType.JOINED)
public class NhanVien extends BaseEntity {

    @Id
    @Column(name = "maNV", length = 20, nullable = false)
    private String maNV;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CCCD", referencedColumnName = "CCCD", nullable = false)
    private NguoiDung nguoiDung;

    public NhanVien() {
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    // Convenience delegates to NguoiDung
    public String getHoTen() {
        return nguoiDung != null ? nguoiDung.getHoTen() : null;
    }

    public String getEmail() {
        return nguoiDung != null ? nguoiDung.getEmail() : null;
    }

    public String getCccd() {
        return nguoiDung != null ? nguoiDung.getCccd() : null;
    }

    public String getSoDienThoai() {
        return nguoiDung != null ? nguoiDung.getSoDienThoai() : null;
    }

    public String getDiaChi() {
        return nguoiDung != null ? nguoiDung.getDiaChi() : null;
    }

    public String getAnhThe() {
        return nguoiDung != null ? nguoiDung.getAnhThe() : null;
    }

    public String getGioiTinh() {
        return nguoiDung != null ? nguoiDung.getGioiTinh() : null;
    }
}
