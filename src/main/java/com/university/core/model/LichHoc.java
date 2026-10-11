package com.university.core.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "LichHoc")
public class LichHoc extends BaseEntity {

    @Id
    @Column(name = "maLichHoc", length = 30, nullable = false)
    private String maLichHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLHP", nullable = false)
    private LopHocPhan lopHocPhan;

    @Column(name = "ngayHoc", nullable = false)
    private LocalDate ngayHoc;

    @Column(name = "phongHoc", length = 50, nullable = false)
    private String phongHoc;

    @Column(name = "is_editable")
    private Boolean isEditable = true;

    public LichHoc() {
    }

    public boolean isEditable() {
        if (Boolean.FALSE.equals(this.isEditable)) {
            return false;
        }
        if (this.lopHocPhan != null) {
            return this.lopHocPhan.isEditable();
        }
        return true;
    }

    public Boolean getIsEditable() {
        return isEditable();
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    public String getMaLichHoc() {
        return maLichHoc;
    }

    public void setMaLichHoc(String maLichHoc) {
        this.maLichHoc = maLichHoc;
    }

    public LopHocPhan getLopHocPhan() {
        return lopHocPhan;
    }

    public void setLopHocPhan(LopHocPhan lopHocPhan) {
        this.lopHocPhan = lopHocPhan;
    }

    public LocalDate getNgayHoc() {
        return ngayHoc;
    }

    public void setNgayHoc(LocalDate ngayHoc) {
        this.ngayHoc = ngayHoc;
    }

    public String getPhongHoc() {
        return phongHoc;
    }

    public void setPhongHoc(String phongHoc) {
        this.phongHoc = phongHoc;
    }

    public String getMaLHP() {
        return lopHocPhan != null ? lopHocPhan.getMaLHP() : null;
    }

    public Integer getThu() {
        return lopHocPhan != null ? lopHocPhan.getThu() : null;
    }

    public Integer getTietBatDau() {
        return lopHocPhan != null ? lopHocPhan.getTietBatDau() : null;
    }

    public Integer getSoTiet() {
        return lopHocPhan != null ? lopHocPhan.getSoTiet() : null;
    }

    public Integer getTietKetThuc() {
        return lopHocPhan != null ? lopHocPhan.getTietKetThuc() : null;
    }

    public String getTenLHP() {
        return lopHocPhan != null ? lopHocPhan.getTenLHP() : null;
    }

    public String getTenHP() {
        return lopHocPhan != null ? lopHocPhan.getTenHP() : null;
    }
}
