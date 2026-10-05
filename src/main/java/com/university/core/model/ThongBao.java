package com.university.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThongBao")
public class ThongBao extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maThongBao")
    private Long maThongBao;

    @Column(name = "tieuDe", length = 255, nullable = false)
    private String tieuDe;

    @Column(name = "noiDung", columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Column(name = "phamVi", length = 50, nullable = false)
    private String phamVi = "ALL";

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }

    public ThongBao() {
    }

    public Long getMaThongBao() {
        return maThongBao;
    }

    public void setMaThongBao(Long maThongBao) {
        this.maThongBao = maThongBao;
    }

    // Compatibility aliases
    public Long getId() {
        return maThongBao;
    }

    public void setId(Long id) {
        this.maThongBao = id;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getPhamVi() {
        return phamVi;
    }

    public void setPhamVi(String phamVi) {
        this.phamVi = phamVi;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    // Compatibility alias
    public LocalDateTime getNgayDang() {
        return ngayTao;
    }

    public void setNgayDang(LocalDateTime ngayDang) {
        this.ngayTao = ngayDang;
    }
}
