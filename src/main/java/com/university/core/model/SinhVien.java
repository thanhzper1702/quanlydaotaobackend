package com.university.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SinhVien")
@Inheritance(strategy = InheritanceType.JOINED)
public class SinhVien extends BaseEntity {

    @Id
    @Column(name = "maSV", length = 20, nullable = false)
    private String maSV;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CCCD", referencedColumnName = "CCCD", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "nienKhoa", length = 20, nullable = false)
    private String nienKhoa;

    @Column(name = "khoa", length = 100, nullable = false)
    private String khoa;

    @Column(name = "chuyenNganh", length = 100, nullable = false)
    private String chuyenNganh;

    public SinhVien() {
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public String getNienKhoa() {
        return nienKhoa;
    }

    public void setNienKhoa(String nienKhoa) {
        this.nienKhoa = nienKhoa;
    }

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

    public String getChuyenNganh() {
        return chuyenNganh;
    }

    public void setChuyenNganh(String chuyenNganh) {
        this.chuyenNganh = chuyenNganh;
    }

    // Compatibility aliases
    public String getMaSinhVien() {
        return maSV;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSV = maSinhVien;
    }

    public String getNganh() {
        return chuyenNganh;
    }

    public void setNganh(String nganh) {
        this.chuyenNganh = nganh;
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
