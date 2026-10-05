package com.university.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "TruongPhongDaoTao")
@PrimaryKeyJoinColumn(name = "maNV")
public class TruongPhongDaoTao extends NhanVien {

    @Column(name = "nhiemKy", length = 50, nullable = false)
    private String nhiemKy;

    public TruongPhongDaoTao() {
    }

    public String getNhiemKy() {
        return nhiemKy;
    }

    public void setNhiemKy(String nhiemKy) {
        this.nhiemKy = nhiemKy;
    }
}
