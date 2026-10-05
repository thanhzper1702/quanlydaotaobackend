package com.university.core.model;

import com.university.core.model.enums.TrangThaiLop;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "LopHocPhan")
public class LopHocPhan extends BaseEntity {

    @Id
    @Column(name = "maLHP", length = 20, nullable = false)
    private String maLHP;

    @Column(name = "tenLHP", length = 150, nullable = false)
    private String tenLHP;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maHP", nullable = false)
    private HocPhan monHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maHocKy", nullable = false)
    private HocKy hocKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV_TroLy", referencedColumnName = "maNV", nullable = false)
    private TroLyDaoTao troLyDaoTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maGV", referencedColumnName = "maNV")
    private GiangVien giangVien;

    @Column(name = "siSoToiDa", nullable = false)
    private Integer siSoToiDa = 40;

    @Column(name = "siSoHienTai", nullable = false)
    private Integer siSoHienTai = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", length = 20, nullable = false)
    private TrangThaiLop trangThai = TrangThaiLop.MODANGKY;

    @Column(name = "tietBatDau", nullable = false)
    private Integer tietBatDau;

    @Column(name = "soTiet", nullable = false)
    private Integer soTiet;

    @Column(name = "thu", nullable = false)
    private Integer thu;

    @Column(name = "thoiGianBDDK", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime thoiGianBDDK;

    @Column(name = "thoiGianKTDK", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime thoiGianKTDK;

    @Column(name = "khoaDiem")
    private Boolean khoaDiem = false;

    public LopHocPhan() {
    }

    public boolean isDangMoDangKy() {
        if (trangThai != TrangThaiLop.MODANGKY) {
            return false;
        }
        OffsetDateTime now = OffsetDateTime.now();
        if (thoiGianBDDK != null && now.isBefore(thoiGianBDDK)) {
            return false;
        }
        if (thoiGianKTDK != null && now.isAfter(thoiGianKTDK)) {
            return false;
        }
        return true;
    }

    public String getMaLHP() {
        return maLHP;
    }

    public void setMaLHP(String maLHP) {
        this.maLHP = maLHP;
    }

    public String getTenLHP() {
        return tenLHP;
    }

    public void setTenLHP(String tenLHP) {
        this.tenLHP = tenLHP;
    }

    // Compatibility alias
    public String getTenLop() {
        return tenLHP;
    }

    public void setTenLop(String tenLop) {
        this.tenLHP = tenLop;
    }

    public HocPhan getMonHoc() {
        return monHoc;
    }

    public void setMonHoc(HocPhan monHoc) {
        this.monHoc = monHoc;
    }

    public HocKy getHocKy() {
        return hocKy;
    }

    public void setHocKy(HocKy hocKy) {
        this.hocKy = hocKy;
    }

    public TroLyDaoTao getTroLyDaoTao() {
        return troLyDaoTao;
    }

    public void setTroLyDaoTao(TroLyDaoTao troLyDaoTao) {
        this.troLyDaoTao = troLyDaoTao;
    }

    public GiangVien getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(GiangVien giangVien) {
        this.giangVien = giangVien;
    }

    public Integer getSiSoToiDa() {
        return siSoToiDa;
    }

    public void setSiSoToiDa(Integer siSoToiDa) {
        this.siSoToiDa = siSoToiDa;
    }

    // Compatibility alias
    public Integer getSoLuongToiDa() {
        return siSoToiDa;
    }

    public void setSoLuongToiDa(Integer soLuongToiDa) {
        this.siSoToiDa = soLuongToiDa;
    }

    public Integer getSiSoHienTai() {
        return siSoHienTai;
    }

    public void setSiSoHienTai(Integer siSoHienTai) {
        this.siSoHienTai = siSoHienTai;
    }

    // Compatibility alias
    public Integer getSoLuongDaDangKy() {
        return siSoHienTai;
    }

    public void setSoLuongDaDangKy(Integer soLuongDaDangKy) {
        this.siSoHienTai = soLuongDaDangKy;
    }

    public TrangThaiLop getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiLop trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getTietBatDau() {
        return tietBatDau;
    }

    public void setTietBatDau(Integer tietBatDau) {
        this.tietBatDau = tietBatDau;
    }

    public Integer getSoTiet() {
        return soTiet;
    }

    public void setSoTiet(Integer soTiet) {
        this.soTiet = soTiet;
    }

    public Integer getThu() {
        return thu;
    }

    public void setThu(Integer thu) {
        this.thu = thu;
    }

    public OffsetDateTime getThoiGianBDDK() {
        return thoiGianBDDK;
    }

    public void setThoiGianBDDK(OffsetDateTime thoiGianBDDK) {
        this.thoiGianBDDK = thoiGianBDDK;
    }

    public OffsetDateTime getThoiGianKTDK() {
        return thoiGianKTDK;
    }

    public void setThoiGianKTDK(OffsetDateTime thoiGianKTDK) {
        this.thoiGianKTDK = thoiGianKTDK;
    }

    public Boolean getKhoaDiem() {
        return khoaDiem;
    }

    public Boolean isKhoaDiem() {
        return khoaDiem != null && khoaDiem;
    }

    public void setKhoaDiem(Boolean khoaDiem) {
        this.khoaDiem = khoaDiem;
    }

    public int getTietKetThuc() {
        return (tietBatDau != null && soTiet != null) ? (tietBatDau + soTiet - 1) : 0;
    }

    public boolean isConCho() {
        return siSoHienTai != null && siSoToiDa != null && siSoHienTai < siSoToiDa;
    }

    public boolean tangSiSo() {
        if (isConCho()) {
            this.siSoHienTai = (this.siSoHienTai == null ? 0 : this.siSoHienTai) + 1;
            return true;
        }
        return false;
    }

    public boolean giamSiSo() {
        if (this.siSoHienTai != null && this.siSoHienTai > 0) {
            this.siSoHienTai--;
            return true;
        }
        return false;
    }

    public boolean isDaPhanCongGiangVien() {
        return this.giangVien != null;
    }

    public String getMaHP() {
        return monHoc != null ? monHoc.getMaHP() : null;
    }

    public String getTenHP() {
        return monHoc != null ? monHoc.getTenHP() : null;
    }

    public Integer getSoTinChi() {
        return monHoc != null ? monHoc.getSoTinChi() : null;
    }
}
