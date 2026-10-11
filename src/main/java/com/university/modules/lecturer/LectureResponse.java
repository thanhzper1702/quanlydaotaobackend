package com.university.modules.lecturer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public final class LectureResponse {
    private LectureResponse() {
    }
    public static class LecturerTeachingClassResponse {
        private List<TeachingClass> data;
        private Meta meta;

        public LecturerTeachingClassResponse() {
        }

        public List<TeachingClass> getData() {
            return data;
        }

        public void setData(List<TeachingClass> data) {
            this.data = data;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }
        public static class TeachingClass {
            private String maLHP;
            private String tenHP;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private String tenLHP;
            private Integer siSo;
            private Boolean khoaDiem;
            private String trangThai;
            private Integer soTinChi;
            private Integer thu;
            private Integer tietBatDau;
            private Integer soTiet;
            private String phongHoc;
            private Integer siSoToiDa;

            public TeachingClass() {
            }

            public String getMaLHP() {
                return maLHP;
            }

            public void setMaLHP(String maLHP) {
                this.maLHP = maLHP;
            }

            public String getTenHP() {
                return tenHP;
            }

            public void setTenHP(String tenHP) {
                this.tenHP = tenHP;
            }

            public String getTenLHP() {
                return tenLHP;
            }

            public void setTenLHP(String tenLHP) {
                this.tenLHP = tenLHP;
            }

            public Integer getSiSo() {
                return siSo;
            }

            public void setSiSo(Integer siSo) {
                this.siSo = siSo;
            }

            public Boolean getKhoaDiem() {
                return khoaDiem;
            }

            public void setKhoaDiem(Boolean khoaDiem) {
                this.khoaDiem = khoaDiem;
            }

            public String getTrangThai() {
                return trangThai;
            }

            public void setTrangThai(String trangThai) {
                this.trangThai = trangThai;
            }

            public Integer getSoTinChi() {
                return soTinChi;
            }

            public void setSoTinChi(Integer soTinChi) {
                this.soTinChi = soTinChi;
            }

            public Integer getThu() {
                return thu;
            }

            public void setThu(Integer thu) {
                this.thu = thu;
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

            public String getPhongHoc() {
                return phongHoc;
            }

            public void setPhongHoc(String phongHoc) {
                this.phongHoc = phongHoc;
            }

            public Integer getSiSoToiDa() {
                return siSoToiDa;
            }

            public void setSiSoToiDa(Integer siSoToiDa) {
                this.siSoToiDa = siSoToiDa;
            }
        }
        public static class Meta {
            private Integer tongSoLopPhuTrach;
            private Integer tongSoSinhVien;

            public Meta() {
            }

            public Integer getTongSoLopPhuTrach() {
                return tongSoLopPhuTrach;
            }

            public void setTongSoLopPhuTrach(Integer tongSoLopPhuTrach) {
                this.tongSoLopPhuTrach = tongSoLopPhuTrach;
            }

            public Integer getTongSoSinhVien() {
                return tongSoSinhVien;
            }

            public void setTongSoSinhVien(Integer tongSoSinhVien) {
                this.tongSoSinhVien = tongSoSinhVien;
            }
        }
    }
    public static class LecturerClassGradeResponse {
        private List<StudentGrade> data;
        private Meta meta;

        public LecturerClassGradeResponse() {
        }

        public List<StudentGrade> getData() {
            return data;
        }

        public void setData(List<StudentGrade> data) {
            this.data = data;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }
        public static class StudentGrade {
            private String maSV;
            private String hoTen;
            private String anhThe;
            private BigDecimal diemQuaTrinh;
            private BigDecimal diemCuoiKy;
            private BigDecimal diemTongKet;
            private String ketQua;
            private Integer soBuoiVang;
            private String trangThai;

            public StudentGrade() {
            }

            public String getMaSV() {
                return maSV;
            }

            public void setMaSV(String maSV) {
                this.maSV = maSV;
            }

            public String getHoTen() {
                return hoTen;
            }

            public void setHoTen(String hoTen) {
                this.hoTen = hoTen;
            }

            public String getAnhThe() {
                return anhThe;
            }

            public void setAnhThe(String anhThe) {
                this.anhThe = anhThe;
            }

            public BigDecimal getDiemQuaTrinh() {
                return diemQuaTrinh;
            }

            public void setDiemQuaTrinh(BigDecimal diemQuaTrinh) {
                this.diemQuaTrinh = diemQuaTrinh;
            }

            public BigDecimal getDiemCuoiKy() {
                return diemCuoiKy;
            }

            public void setDiemCuoiKy(BigDecimal diemCuoiKy) {
                this.diemCuoiKy = diemCuoiKy;
            }

            public BigDecimal getDiemTongKet() {
                return diemTongKet;
            }

            public void setDiemTongKet(BigDecimal diemTongKet) {
                this.diemTongKet = diemTongKet;
            }

            public String getKetQua() {
                return ketQua;
            }

            public void setKetQua(String ketQua) {
                this.ketQua = ketQua;
            }

            public Integer getSoBuoiVang() {
                return soBuoiVang;
            }

            public void setSoBuoiVang(Integer soBuoiVang) {
                this.soBuoiVang = soBuoiVang;
            }

            public String getTrangThai() {
                return trangThai;
            }

            public void setTrangThai(String trangThai) {
                this.trangThai = trangThai;
            }
        }
        public static class Meta {
            private String maLHP;
            private Integer siSoHienTai;
            private Integer soSinhVienDaNhapDiem;
            private Integer soSinhVienChuaNhapDiem;
            private Boolean khoaDiem;

            public Meta() {
            }

            public String getMaLHP() {
                return maLHP;
            }

            public void setMaLHP(String maLHP) {
                this.maLHP = maLHP;
            }

            public Integer getSiSoHienTai() {
                return siSoHienTai;
            }

            public void setSiSoHienTai(Integer siSoHienTai) {
                this.siSoHienTai = siSoHienTai;
            }

            public Integer getSoSinhVienDaNhapDiem() {
                return soSinhVienDaNhapDiem;
            }

            public void setSoSinhVienDaNhapDiem(Integer soSinhVienDaNhapDiem) {
                this.soSinhVienDaNhapDiem = soSinhVienDaNhapDiem;
            }

            public Integer getSoSinhVienChuaNhapDiem() {
                return soSinhVienChuaNhapDiem;
            }

            public void setSoSinhVienChuaNhapDiem(Integer soSinhVienChuaNhapDiem) {
                this.soSinhVienChuaNhapDiem = soSinhVienChuaNhapDiem;
            }

            public Boolean getKhoaDiem() {
                return khoaDiem;
            }

            public void setKhoaDiem(Boolean khoaDiem) {
                this.khoaDiem = khoaDiem;
            }
        }
    }
    public static class LecturerTeachingScheduleResponse {
        private List<TeachingSession> data;
        private Meta meta;

        public LecturerTeachingScheduleResponse() {
        }

        public List<TeachingSession> getData() {
            return data;
        }

        public void setData(List<TeachingSession> data) {
            this.data = data;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }
        public static class TeachingSession {
            private String maLichHoc;
            private String maLHP;
            private String tenHP;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private String tenLHP;
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            private LocalDate ngayHoc;
            private Integer thu;
            private Integer tietBatDau;
            private Integer soTiet;
            private String phongHoc;

            public TeachingSession() {
            }

            public String getMaLichHoc() {
                return maLichHoc;
            }

            public void setMaLichHoc(String maLichHoc) {
                this.maLichHoc = maLichHoc;
            }

            public String getMaLHP() {
                return maLHP;
            }

            public void setMaLHP(String maLHP) {
                this.maLHP = maLHP;
            }

            public String getTenHP() {
                return tenHP;
            }

            public void setTenHP(String tenHP) {
                this.tenHP = tenHP;
            }

            public String getTenLHP() {
                return tenLHP;
            }

            public void setTenLHP(String tenLHP) {
                this.tenLHP = tenLHP;
            }

            public LocalDate getNgayHoc() {
                return ngayHoc;
            }

            public void setNgayHoc(LocalDate ngayHoc) {
                this.ngayHoc = ngayHoc;
            }

            public Integer getThu() {
                return thu;
            }

            public void setThu(Integer thu) {
                this.thu = thu;
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

            public String getPhongHoc() {
                return phongHoc;
            }

            public void setPhongHoc(String phongHoc) {
                this.phongHoc = phongHoc;
            }
        }
        public static class Meta {
            private Integer tuanHienTai;
            private Integer tongSoBuoiDayTrongTuan;
            private Integer tongSoTietDayTrongTuan;

            public Meta() {
            }

            public Integer getTuanHienTai() {
                return tuanHienTai;
            }

            public void setTuanHienTai(Integer tuanHienTai) {
                this.tuanHienTai = tuanHienTai;
            }

            public Integer getTongSoBuoiDayTrongTuan() {
                return tongSoBuoiDayTrongTuan;
            }

            public void setTongSoBuoiDayTrongTuan(Integer tongSoBuoiDayTrongTuan) {
                this.tongSoBuoiDayTrongTuan = tongSoBuoiDayTrongTuan;
            }

            public Integer getTongSoTietDayTrongTuan() {
                return tongSoTietDayTrongTuan;
            }

            public void setTongSoTietDayTrongTuan(Integer tongSoTietDayTrongTuan) {
                this.tongSoTietDayTrongTuan = tongSoTietDayTrongTuan;
            }
        }
    }
    public static class LecturerAttendanceResponse {
        private List<AttendanceEntry> data;
        private Meta meta;

        public LecturerAttendanceResponse() {
        }

        public List<AttendanceEntry> getData() {
            return data;
        }

        public void setData(List<AttendanceEntry> data) {
            this.data = data;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }
        public static class AttendanceEntry {
            private String maSV;
            private String hoTen;
            private String anhThe;
            private Boolean coMat;
            private String ghiChu;

            public AttendanceEntry() {
            }

            public String getMaSV() {
                return maSV;
            }

            public void setMaSV(String maSV) {
                this.maSV = maSV;
            }

            public String getHoTen() {
                return hoTen;
            }

            public void setHoTen(String hoTen) {
                this.hoTen = hoTen;
            }

            public String getAnhThe() {
                return anhThe;
            }

            public void setAnhThe(String anhThe) {
                this.anhThe = anhThe;
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
        }
        public static class Meta {
            private String maLichHoc;
            private Integer siSoLop;
            private Integer soCoMat;
            private Integer soVangMat;

            public Meta() {
            }

            public String getMaLichHoc() {
                return maLichHoc;
            }

            public void setMaLichHoc(String maLichHoc) {
                this.maLichHoc = maLichHoc;
            }

            public Integer getSiSoLop() {
                return siSoLop;
            }

            public void setSiSoLop(Integer siSoLop) {
                this.siSoLop = siSoLop;
            }

            public Integer getSoCoMat() {
                return soCoMat;
            }

            public void setSoCoMat(Integer soCoMat) {
                this.soCoMat = soCoMat;
            }

            public Integer getSoVangMat() {
                return soVangMat;
            }

            public void setSoVangMat(Integer soVangMat) {
                this.soVangMat = soVangMat;
            }
        }
    }
    public static class UpdateGradesResponse {
        public UpdateGradesResponse() {
        }
    }
    public static class UpdateAttendanceResponse {
        public UpdateAttendanceResponse() {
        }
    }
}