package com.university.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "TroLyDaoTao")
@PrimaryKeyJoinColumn(name = "maNV")
public class TroLyDaoTao extends NhanVien {

    @Column(name = "ngayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    public TroLyDaoTao() {
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }
}
