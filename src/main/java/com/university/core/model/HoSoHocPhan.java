package com.university.core.model;

import com.university.core.model.enums.KetQua;
import com.university.core.model.enums.KetQuaConverter;
import com.university.core.model.enums.TrangThaiHocPhan;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoSoHocPhan", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"maSV", "maLHP"})
})
public class HoSoHocPhan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maHoSo")
    private Long maHoSo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSV", referencedColumnName = "maSV", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLHP", referencedColumnName = "maLHP", nullable = false)
    private LopHocPhan lopHocPhan;

    @Column(name = "ngayDangKy")
    private LocalDateTime ngayDangKy;

    @Column(name = "diemQuaTrinh", precision = 4, scale = 2)
    private BigDecimal diemQuaTrinh;

    @Column(name = "diemCuoiKy", precision = 4, scale = 2)
    private BigDecimal diemCuoiKy;

    @Column(name = "diemTongKet", precision = 4, scale = 2)
    private BigDecimal diemTongKet;

    @Convert(converter = KetQuaConverter.class)
    @Column(name = "ketQua", length = 5)
    private KetQua ketQua;

    @Column(name = "soBuoiVang", nullable = false)
    private Integer soBuoiVang = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", length = 20, nullable = false)
    private TrangThaiHocPhan trangThai = TrangThaiHocPhan.DADANGKY;

    @Column(name = "is_editable")
    private Boolean isEditable = true;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (ngayDangKy == null) {
            ngayDangKy = LocalDateTime.now();
        }
        if (soBuoiVang == null) {
            soBuoiVang = 0;
        }
        if (trangThai == null) {
            trangThai = TrangThaiHocPhan.DADANGKY;
        }
    }

    public HoSoHocPhan() {
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

    public Long getMaHoSo() {
        return maHoSo;
    }

    public void setMaHoSo(Long maHoSo) {
        this.maHoSo = maHoSo;
    }

    public SinhVien getSinhVien() {
        return sinhVien;
    }

    public void setSinhVien(SinhVien sinhVien) {
        this.sinhVien = sinhVien;
    }

    public LopHocPhan getLopHocPhan() {
        return lopHocPhan;
    }

    public void setLopHocPhan(LopHocPhan lopHocPhan) {
        this.lopHocPhan = lopHocPhan;
    }

    public LocalDateTime getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(LocalDateTime ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public BigDecimal getDiemQuaTrinh() {
        return diemQuaTrinh;
    }

    public void setDiemQuaTrinh(BigDecimal diemQuaTrinh) {
        this.diemQuaTrinh = diemQuaTrinh;
        tinhDiemVaKetQua();
    }

    public BigDecimal getDiemCuoiKy() {
        return diemCuoiKy;
    }

    public void setDiemCuoiKy(BigDecimal diemCuoiKy) {
        this.diemCuoiKy = diemCuoiKy;
        tinhDiemVaKetQua();
    }

    public BigDecimal getDiemTongKet() {
        return diemTongKet;
    }

    public void setDiemTongKet(BigDecimal diemTongKet) {
        this.diemTongKet = diemTongKet;
    }

    public KetQua getKetQua() {
        return ketQua;
    }

    public void setKetQua(KetQua ketQua) {
        this.ketQua = ketQua;
    }

    public Integer getSoBuoiVang() {
        return soBuoiVang;
    }

    public void setSoBuoiVang(Integer soBuoiVang) {
        this.soBuoiVang = soBuoiVang;
    }

    public TrangThaiHocPhan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHocPhan trangThai) {
        this.trangThai = trangThai;
    }

    // Compatibility aliases for Float
    public Float getDiemQT() {
        return diemQuaTrinh != null ? diemQuaTrinh.floatValue() : null;
    }

    public void setDiemQT(Float diemQT) {
        this.diemQuaTrinh = diemQT != null ? BigDecimal.valueOf(diemQT) : null;
    }

    public Float getDiemCK() {
        return diemCuoiKy != null ? diemCuoiKy.floatValue() : null;
    }

    public void setDiemCK(Float diemCK) {
        this.diemCuoiKy = diemCK != null ? BigDecimal.valueOf(diemCK) : null;
    }

    public Float getDiemTK() {
        return diemTongKet != null ? diemTongKet.floatValue() : null;
    }

    public void setDiemTK(Float diemTK) {
        this.diemTongKet = diemTK != null ? BigDecimal.valueOf(diemTK) : null;
    }

    public void tinhDiemVaKetQua(double heSoQT, double heSoCK) {
        if (diemQuaTrinh != null && diemCuoiKy != null) {
            BigDecimal tk = diemQuaTrinh.multiply(BigDecimal.valueOf(heSoQT))
                    .add(diemCuoiKy.multiply(BigDecimal.valueOf(heSoCK)));
            this.diemTongKet = tk.setScale(2, RoundingMode.HALF_UP);
            this.ketQua = quyDoiKetQuaChu(this.diemTongKet);
        }
    }

    public void tinhDiemVaKetQua() {
        tinhDiemVaKetQua(0.4, 0.6);
    }

    public void tinhDiemTongKet() {
        tinhDiemVaKetQua();
    }

    public static KetQua quyDoiKetQuaChu(BigDecimal diem10) {
        if (diem10 == null) {
            return null;
        }
        double d = diem10.doubleValue();
        if (d >= 9.0) return KetQua.A_PLUS;
        if (d >= 8.5) return KetQua.A;
        if (d >= 8.0) return KetQua.B_PLUS;
        if (d >= 7.0) return KetQua.B;
        if (d >= 6.5) return KetQua.C_PLUS;
        if (d >= 5.5) return KetQua.C;
        if (d >= 5.0) return KetQua.D_PLUS;
        if (d >= 4.0) return KetQua.D;
        return KetQua.F;
    }

    public boolean isDat() {
        return this.ketQua != null && !KetQua.F.equals(this.ketQua);
    }

    public boolean isRot() {
        return KetQua.F.equals(this.ketQua);
    }

    public boolean isDangHoc() {
        return TrangThaiHocPhan.DANGHOC.equals(this.trangThai);
    }

    public boolean isDaDangKy() {
        return TrangThaiHocPhan.DADANGKY.equals(this.trangThai);
    }

    public boolean isDaHuy() {
        return TrangThaiHocPhan.DAHUY.equals(this.trangThai);
    }

    public void tangBuoiVang() {
        this.soBuoiVang = (this.soBuoiVang == null ? 0 : this.soBuoiVang) + 1;
    }

    public String getMaSV() {
        return sinhVien != null ? sinhVien.getMaSV() : null;
    }

    public String getMaLHP() {
        return lopHocPhan != null ? lopHocPhan.getMaLHP() : null;
    }

    public String getTenLHP() {
        return lopHocPhan != null ? lopHocPhan.getTenLHP() : null;
    }
}
