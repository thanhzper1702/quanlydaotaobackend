package com.university.core.model;

import com.university.core.model.enums.UserRole;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "NguoiDung")
public class NguoiDung extends BaseEntity {

    @Id
    @Column(name = "CCCD", length = 20, nullable = false)
    private String cccd;

    @Column(name = "hoTen", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "anhThe", length = 255)
    private String anhThe;

    @Column(name = "ngaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "gioiTinh", length = 10)
    private String gioiTinh;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "soDienThoai", length = 15)
    private String soDienThoai;

    @Column(name = "diaChi", length = 255)
    private String diaChi;

    @Column(name = "matKhauHashed", length = 255, nullable = false)
    private String matKhauHashed;

    @Transient
    private UserRole vaiTro;

    public NguoiDung() {
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getAnhThe() {
        return anhThe;
    }

    public void setAnhThe(String anhThe) {
        this.anhThe = anhThe;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getMatKhauHashed() {
        return matKhauHashed;
    }

    public void setMatKhauHashed(String matKhauHashed) {
        this.matKhauHashed = matKhauHashed;
    }

    // Compatibility aliases
    public String getMatKhau() {
        return matKhauHashed;
    }

    public void setMatKhau(String matKhau) {
        this.matKhauHashed = matKhau;
    }

    public UserRole getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(UserRole vaiTro) {
        this.vaiTro = vaiTro;
    }
}
