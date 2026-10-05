package com.university.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DiemDanh", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"maSV", "maLichHoc"})
})
public class DiemDanh extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maDiemDanh")
    private Long maDiemDanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSV", referencedColumnName = "maSV", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLichHoc", referencedColumnName = "maLichHoc", nullable = false)
    private LichHoc lichHoc;

    @Column(name = "coMat", nullable = false)
    private Boolean coMat = true;

    @Column(name = "ghiChu", length = 255)
    private String ghiChu;

    public DiemDanh() {
    }

    public Long getMaDiemDanh() {
        return maDiemDanh;
    }

    public void setMaDiemDanh(Long maDiemDanh) {
        this.maDiemDanh = maDiemDanh;
    }

    public SinhVien getSinhVien() {
        return sinhVien;
    }

    public void setSinhVien(SinhVien sinhVien) {
        this.sinhVien = sinhVien;
    }

    public LichHoc getLichHoc() {
        return lichHoc;
    }

    public void setLichHoc(LichHoc lichHoc) {
        this.lichHoc = lichHoc;
    }

    public Boolean getCoMat() {
        return coMat;
    }

    public void setCoMat(Boolean coMat) {
        this.coMat = coMat;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public boolean isCoMat() {
        return Boolean.TRUE.equals(coMat);
    }

    public boolean isVang() {
        return !isCoMat();
    }

    public String getMaSV() {
        return sinhVien != null ? sinhVien.getMaSV() : null;
    }

    public String getMaLichHoc() {
        return lichHoc != null ? lichHoc.getMaLichHoc() : null;
    }

    public String getHoTen() {
        return sinhVien != null ? sinhVien.getHoTen() : null;
    }
}
