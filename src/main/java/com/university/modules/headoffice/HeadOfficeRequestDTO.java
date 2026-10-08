package com.university.modules.headoffice;

public final class HeadOfficeRequestDTO {
    private HeadOfficeRequestDTO() { }

    public static class CreateCourseRequestDTO {
        private String maHP;
        private String tenHP;
        private Integer soTinChi;
        public String getMaHP() { return maHP; }
        public void setMaHP(String maHP) { this.maHP = maHP; }
        public String getTenHP() { return tenHP; }
        public void setTenHP(String tenHP) { this.tenHP = tenHP; }
        public Integer getSoTinChi() { return soTinChi; }
        public void setSoTinChi(Integer soTinChi) { this.soTinChi = soTinChi; }
    }

    public static class CreateSemesterRequestDTO {
        private String maHocKy;
        private String tenHocKy;
        private String thoiGianBD;
        private String thoiGianKT;
        public String getMaHocKy() { return maHocKy; }
        public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }
        public String getTenHocKy() { return tenHocKy; }
        public void setTenHocKy(String tenHocKy) { this.tenHocKy = tenHocKy; }
        public String getThoiGianBD() { return thoiGianBD; }
        public void setThoiGianBD(String thoiGianBD) { this.thoiGianBD = thoiGianBD; }
        public String getThoiGianKT() { return thoiGianKT; }
        public void setThoiGianKT(String thoiGianKT) { this.thoiGianKT = thoiGianKT; }
    }

    public static class UpdateCourseRequestDTO {
        private String tenHP;
        private Integer soTinChi;
        public String getTenHP() { return tenHP; }
        public void setTenHP(String tenHP) { this.tenHP = tenHP; }
        public Integer getSoTinChi() { return soTinChi; }
        public void setSoTinChi(Integer soTinChi) { this.soTinChi = soTinChi; }
    }

    public static class UpdateGradeLockRequestDTO {
        private Boolean khoaDiem;
        public Boolean getKhoaDiem() { return khoaDiem; }
        public void setKhoaDiem(Boolean khoaDiem) { this.khoaDiem = khoaDiem; }
    }
}
