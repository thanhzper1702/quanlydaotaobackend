package com.university.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "HocPhan")
public class HocPhan extends BaseEntity {

    @Id
    @Column(name = "maHP", length = 20, nullable = false)
    private String maHP;

    @Column(name = "tenHP", length = 150, nullable = false)
    private String tenHP;

    @Column(name = "soTinChi", nullable = false)
    private Integer soTinChi;

    public HocPhan() {
    }

    public String getMaHP() {
        return maHP;
    }

    public void setMaHP(String maHP) {
        this.maHP = maHP;
    }

    public String getTenHP() {
        return tenHP;
    }

    public void setTenHP(String tenHP) {
        this.tenHP = tenHP;
    }

    public Integer getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(Integer soTinChi) {
        this.soTinChi = soTinChi;
    }

    // Compatibility aliases
    public String getId() {
        return maHP;
    }

    public void setId(String id) {
        this.maHP = id;
    }

    public String getMaMonHoc() {
        return maHP;
    }

    public void setMaMonHoc(String maMonHoc) {
        this.maHP = maMonHoc;
    }

    public String getTenMonHoc() {
        return tenHP;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenHP = tenMonHoc;
    }
}
