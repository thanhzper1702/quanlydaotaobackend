package com.university.core.model;

import com.university.core.model.enums.DienUuTien;
import jakarta.persistence.*;

@Entity
@Table(name = "SinhVienUuTien")
@PrimaryKeyJoinColumn(name = "maSV")
public class SinhVienUuTien extends SinhVien {

    @Enumerated(EnumType.STRING)
    @Column(name = "dienUuTien", length = 50, nullable = false)
    private DienUuTien dienUuTien;

    public SinhVienUuTien() {
    }

    public DienUuTien getDienUuTien() {
        return dienUuTien;
    }

    public void setDienUuTien(DienUuTien dienUuTien) {
        this.dienUuTien = dienUuTien;
    }
}
