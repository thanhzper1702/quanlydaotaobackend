package com.university.core.model;

import com.university.core.model.enums.TrangThaiYeuCau;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "YeuCauMoLop")
public class YeuCauMoLop extends BaseEntity {

    @Id
    @Column(name = "maYeuCau", length = 30, nullable = false)
    private String maYeuCau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSV", referencedColumnName = "maSV", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maHocPhan", referencedColumnName = "maHP", nullable = false)
    private HocPhan hocPhan;

    @Column(name = "lyDo", columnDefinition = "TEXT", nullable = false)
    private String lyDo;

    @Column(name = "lichHocMongMuon", length = 100)
    private String lichHocMongMuon;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", length = 20, nullable = false)
    private TrangThaiYeuCau trangThai = TrangThaiYeuCau.DAGUI;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = TrangThaiYeuCau.DAGUI;
        }
    }

    public YeuCauMoLop() {
    }

    public String getMaYeuCau() {
        return maYeuCau;
    }

    public void setMaYeuCau(String maYeuCau) {
        this.maYeuCau = maYeuCau;
    }

    public SinhVien getSinhVien() {
        return sinhVien;
    }

    public void setSinhVien(SinhVien sinhVien) {
        this.sinhVien = sinhVien;
    }

    public HocPhan getHocPhan() {
        return hocPhan;
    }

    public void setHocPhan(HocPhan hocPhan) {
        this.hocPhan = hocPhan;
    }

    // Compatibility alias
    public HocPhan getMonHoc() {
        return hocPhan;
    }

    public void setMonHoc(HocPhan monHoc) {
        this.hocPhan = monHoc;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getLichHocMongMuon() {
        return lichHocMongMuon;
    }

    public void setLichHocMongMuon(String lichHocMongMuon) {
        this.lichHocMongMuon = lichHocMongMuon;
    }

    public TrangThaiYeuCau getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiYeuCau trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    // Compatibility aliases
    public LocalDateTime getNgayYeuCau() {
        return ngayTao;
    }

    public void setNgayYeuCau(LocalDateTime ngayYeuCau) {
        this.ngayTao = ngayYeuCau;
    }

    public boolean isDaGui() {
        return TrangThaiYeuCau.DAGUI.equals(this.trangThai);
    }

    public boolean isDaTiepNhan() {
        return TrangThaiYeuCau.DATIEPNHAN.equals(this.trangThai);
    }

    public boolean isChoDuyet() {
        return TrangThaiYeuCau.CHODUYET.equals(this.trangThai);
    }

    public boolean isDaDuyet() {
        return TrangThaiYeuCau.DADUYET.equals(this.trangThai);
    }

    public boolean isDaTuChoi() {
        return TrangThaiYeuCau.DATUCHOI.equals(this.trangThai);
    }

    public String getMaSV() {
        return sinhVien != null ? sinhVien.getMaSV() : null;
    }

    public String getHoTenSinhVien() {
        return sinhVien != null ? sinhVien.getHoTen() : null;
    }

    public String getMaHP() {
        return hocPhan != null ? hocPhan.getMaHP() : null;
    }

    public String getTenHP() {
        return hocPhan != null ? hocPhan.getTenHP() : null;
    }
}
