package com.university.modules.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Gom nhóm toàn bộ Response DTO của phân hệ Profile.
 */
public final class ProfileResponseDTO {

    private ProfileResponseDTO() {
    }

    public static class ChangePasswordResponseDTO {

        @JsonProperty("message")
        private String message;

        public ChangePasswordResponseDTO() {
        }

        public ChangePasswordResponseDTO(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserProfileResponseDTO {

        @JsonProperty("maDinhDanh")
        private String maDinhDanh;

        @JsonProperty("hoTen")
        private String hoTen;

        @JsonProperty("ngaySinh")
        private String ngaySinh;

        @JsonProperty("gioiTinh")
        private String gioiTinh;

        @JsonProperty("email")
        private String email;

        @JsonProperty("soDienThoai")
        private String soDienThoai;

        @JsonProperty("diaChi")
        private String diaChi;

        @JsonProperty("role")
        private String role;

        @JsonProperty("anhThe")
        private String anhThe;

        @JsonProperty("thongTinHocVu")
        private ThongTinHocVuDTO thongTinHocVu;

        @JsonProperty("thongTinCanBo")
        private ThongTinCanBoDTO thongTinCanBo;

        public UserProfileResponseDTO() {
        }

        public UserProfileResponseDTO(String maDinhDanh, String hoTen, String ngaySinh, String gioiTinh,
                                      String email, String soDienThoai, String diaChi, String role,
                                      String anhThe, ThongTinHocVuDTO thongTinHocVu, ThongTinCanBoDTO thongTinCanBo) {
            this.maDinhDanh = maDinhDanh;
            this.hoTen = hoTen;
            this.ngaySinh = ngaySinh;
            this.gioiTinh = gioiTinh;
            this.email = email;
            this.soDienThoai = soDienThoai;
            this.diaChi = diaChi;
            this.role = role;
            this.anhThe = anhThe;
            this.thongTinHocVu = thongTinHocVu;
            this.thongTinCanBo = thongTinCanBo;
        }

        public String getMaDinhDanh() {
            return maDinhDanh;
        }

        public void setMaDinhDanh(String maDinhDanh) {
            this.maDinhDanh = maDinhDanh;
        }

        public String getHoTen() {
            return hoTen;
        }

        public void setHoTen(String hoTen) {
            this.hoTen = hoTen;
        }

        public String getNgaySinh() {
            return ngaySinh;
        }

        public void setNgaySinh(String ngaySinh) {
            this.ngaySinh = ngaySinh;
        }

        public String getGioiTinh() {
            return gioiTinh;
        }

        public void setGioiTinh(String gioiTinh) {
            this.gioiTinh = gioiTinh;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSoDienThoai() {
            return soDienThoai;
        }

        public void setSoDienThoai(String soDienThoai) {
            this.soDienThoai = soDienThoai;
        }

        public String getDiaChi() {
            return diaChi;
        }

        public void setDiaChi(String diaChi) {
            this.diaChi = diaChi;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getAnhThe() {
            return anhThe;
        }

        public void setAnhThe(String anhThe) {
            this.anhThe = anhThe;
        }

        public ThongTinHocVuDTO getThongTinHocVu() {
            return thongTinHocVu;
        }

        public void setThongTinHocVu(ThongTinHocVuDTO thongTinHocVu) {
            this.thongTinHocVu = thongTinHocVu;
        }

        public ThongTinCanBoDTO getThongTinCanBo() {
            return thongTinCanBo;
        }

        public void setThongTinCanBo(ThongTinCanBoDTO thongTinCanBo) {
            this.thongTinCanBo = thongTinCanBo;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class ThongTinHocVuDTO {
            @JsonProperty("nienKhoa")
            private String nienKhoa;

            @JsonProperty("khoa")
            private String khoa;

            @JsonProperty("chuyenNganh")
            private String chuyenNganh;

            @JsonProperty("soTinChiTichLuy")
            private Integer soTinChiTichLuy;

            @JsonProperty("gpaHienTai")
            private Double gpaHienTai;

            public ThongTinHocVuDTO() {
            }

            public ThongTinHocVuDTO(String nienKhoa, String khoa, String chuyenNganh, Integer soTinChiTichLuy, Double gpaHienTai) {
                this.nienKhoa = nienKhoa;
                this.khoa = khoa;
                this.chuyenNganh = chuyenNganh;
                this.soTinChiTichLuy = soTinChiTichLuy;
                this.gpaHienTai = gpaHienTai;
            }

            public String getNienKhoa() {
                return nienKhoa;
            }

            public void setNienKhoa(String nienKhoa) {
                this.nienKhoa = nienKhoa;
            }

            public String getKhoa() {
                return khoa;
            }

            public void setKhoa(String khoa) {
                this.khoa = khoa;
            }

            public String getChuyenNganh() {
                return chuyenNganh;
            }

            public void setChuyenNganh(String chuyenNganh) {
                this.chuyenNganh = chuyenNganh;
            }

            public Integer getSoTinChiTichLuy() {
                return soTinChiTichLuy;
            }

            public void setSoTinChiTichLuy(Integer soTinChiTichLuy) {
                this.soTinChiTichLuy = soTinChiTichLuy;
            }

            public Double getGpaHienTai() {
                return gpaHienTai;
            }

            public void setGpaHienTai(Double gpaHienTai) {
                this.gpaHienTai = gpaHienTai;
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class ThongTinCanBoDTO {
            @JsonProperty("hocVi")
            private String hocVi;

            @JsonProperty("khoa")
            private String khoa;

            @JsonProperty("ngayBatDau")
            private String ngayBatDau;

            @JsonProperty("nhiemKy")
            private String nhiemKy;

            public ThongTinCanBoDTO() {
            }

            public ThongTinCanBoDTO(String hocVi, String khoa, String ngayBatDau, String nhiemKy) {
                this.hocVi = hocVi;
                this.khoa = khoa;
                this.ngayBatDau = ngayBatDau;
                this.nhiemKy = nhiemKy;
            }

            public String getHocVi() {
                return hocVi;
            }

            public void setHocVi(String hocVi) {
                this.hocVi = hocVi;
            }

            public String getKhoa() {
                return khoa;
            }

            public void setKhoa(String khoa) {
                this.khoa = khoa;
            }

            public String getNgayBatDau() {
                return ngayBatDau;
            }

            public void setNgayBatDau(String ngayBatDau) {
                this.ngayBatDau = ngayBatDau;
            }

            public String getNhiemKy() {
                return nhiemKy;
            }

            public void setNhiemKy(String nhiemKy) {
                this.nhiemKy = nhiemKy;
            }
        }
    }
}
