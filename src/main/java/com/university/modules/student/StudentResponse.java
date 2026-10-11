package com.university.modules.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gom nhóm toàn bộ Response Payload của phân hệ Sinh viên.
 */
public final class StudentResponse {

    private StudentResponse() {
    }

    // 1. Route 11: Bảng điểm sinh viên (GET /student/grades)
    public static class StudentGradeResponse {
        private List<SemesterGradeDTO> semesters = new ArrayList<>();
        private Double diemTBTichLuy;
        private Integer soTinChiTichLuy;
        private String xepLoaiHocLucTichLuy;

        public StudentGradeResponse() {
        }

        public StudentGradeResponse(List<SemesterGradeDTO> semesters, Double diemTBTichLuy,
                                    Integer soTinChiTichLuy, String xepLoaiHocLucTichLuy) {
            this.semesters = semesters != null ? semesters : new ArrayList<>();
            this.diemTBTichLuy = diemTBTichLuy;
            this.soTinChiTichLuy = soTinChiTichLuy;
            this.xepLoaiHocLucTichLuy = xepLoaiHocLucTichLuy;
        }

        public List<SemesterGradeDTO> getSemesters() { return semesters; }
        public void setSemesters(List<SemesterGradeDTO> semesters) { this.semesters = semesters; }

        public Double getDiemTBTichLuy() { return diemTBTichLuy; }
        public void setDiemTBTichLuy(Double diemTBTichLuy) { this.diemTBTichLuy = diemTBTichLuy; }

        public Integer getSoTinChiTichLuy() { return soTinChiTichLuy; }
        public void setSoTinChiTichLuy(Integer soTinChiTichLuy) { this.soTinChiTichLuy = soTinChiTichLuy; }

        public String getXepLoaiHocLucTichLuy() { return xepLoaiHocLucTichLuy; }
        public void setXepLoaiHocLucTichLuy(String xepLoaiHocLucTichLuy) { this.xepLoaiHocLucTichLuy = xepLoaiHocLucTichLuy; }

        public static class SemesterGradeDTO {
            private String maHocKy;
            private String tenHocKy;
            private Double diemTBHK;
            private Integer soTinChiDatHK;
            private String xepLoaiHK;
            private List<CourseGradeItemDTO> danhSachMonHoc = new ArrayList<>();

            public SemesterGradeDTO() {
            }

            public SemesterGradeDTO(String maHocKy, String tenHocKy, Double diemTBHK, Integer soTinChiDatHK,
                                    String xepLoaiHK, List<CourseGradeItemDTO> danhSachMonHoc) {
                this.maHocKy = maHocKy;
                this.tenHocKy = tenHocKy;
                this.diemTBHK = diemTBHK;
                this.soTinChiDatHK = soTinChiDatHK;
                this.xepLoaiHK = xepLoaiHK;
                this.danhSachMonHoc = danhSachMonHoc != null ? danhSachMonHoc : new ArrayList<>();
            }

            public String getMaHocKy() { return maHocKy; }
            public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }

            public String getTenHocKy() { return tenHocKy; }
            public void setTenHocKy(String tenHocKy) { this.tenHocKy = tenHocKy; }

            public Double getDiemTBHK() { return diemTBHK; }
            public void setDiemTBHK(Double diemTBHK) { this.diemTBHK = diemTBHK; }

            public Integer getSoTinChiDatHK() { return soTinChiDatHK; }
            public void setSoTinChiDatHK(Integer soTinChiDatHK) { this.soTinChiDatHK = soTinChiDatHK; }

            public String getXepLoaiHK() { return xepLoaiHK; }
            public void setXepLoaiHK(String xepLoaiHK) { this.xepLoaiHK = xepLoaiHK; }

            public List<CourseGradeItemDTO> getDanhSachMonHoc() { return danhSachMonHoc; }
            public void setDanhSachMonHoc(List<CourseGradeItemDTO> danhSachMonHoc) { this.danhSachMonHoc = danhSachMonHoc; }
        }

        public static class CourseGradeItemDTO {
            private String maHP;
            private String tenHP;
            private Integer soTinChi;
            private Double diemQuaTrinh;
            private Double diemCuoiKy;
            private Double diemTongKet;
            private String ketQua;

            public CourseGradeItemDTO() {
            }

            public CourseGradeItemDTO(String maHP, String tenHP, Integer soTinChi, Double diemQuaTrinh,
                                      Double diemCuoiKy, Double diemTongKet, String ketQua) {
                this.maHP = maHP;
                this.tenHP = tenHP;
                this.soTinChi = soTinChi;
                this.diemQuaTrinh = diemQuaTrinh;
                this.diemCuoiKy = diemCuoiKy;
                this.diemTongKet = diemTongKet;
                this.ketQua = ketQua;
            }

            public String getMaHP() { return maHP; }
            public void setMaHP(String maHP) { this.maHP = maHP; }

            public String getTenHP() { return tenHP; }
            public void setTenHP(String tenHP) { this.tenHP = tenHP; }

            public Integer getSoTinChi() { return soTinChi; }
            public void setSoTinChi(Integer soTinChi) { this.soTinChi = soTinChi; }

            public Double getDiemQuaTrinh() { return diemQuaTrinh; }
            public void setDiemQuaTrinh(Double diemQuaTrinh) { this.diemQuaTrinh = diemQuaTrinh; }

            public Double getDiemCuoiKy() { return diemCuoiKy; }
            public void setDiemCuoiKy(Double diemCuoiKy) { this.diemCuoiKy = diemCuoiKy; }

            public Double getDiemTongKet() { return diemTongKet; }
            public void setDiemTongKet(Double diemTongKet) { this.diemTongKet = diemTongKet; }

            public String getKetQua() { return ketQua; }
            public void setKetQua(String ketQua) { this.ketQua = ketQua; }
        }
    }

    public static class StudentGradeResponseDTO extends StudentGradeResponse {
        public StudentGradeResponseDTO() { super(); }
        public StudentGradeResponseDTO(List<StudentGradeResponse.SemesterGradeDTO> semesters, Double diemTBTichLuy,
                                       Integer soTinChiTichLuy, String xepLoaiHocLucTichLuy) {
            super(semesters, diemTBTichLuy, soTinChiTichLuy, xepLoaiHocLucTichLuy);
        }
    }

    // 2. Route 12: Thời khóa biểu sinh viên (GET /student/schedule)
    public static class StudentScheduleResponse {
        private List<ScheduleItemDTO> schedules = new ArrayList<>();
        private String maHocKyHienTai;
        private String tenHocKy;

        public StudentScheduleResponse() {
        }

        public StudentScheduleResponse(List<ScheduleItemDTO> schedules, String maHocKyHienTai, String tenHocKy) {
            this.schedules = schedules != null ? schedules : new ArrayList<>();
            this.maHocKyHienTai = maHocKyHienTai;
            this.tenHocKy = tenHocKy;
        }

        public List<ScheduleItemDTO> getSchedules() { return schedules; }
        public void setSchedules(List<ScheduleItemDTO> schedules) { this.schedules = schedules; }

        public String getMaHocKyHienTai() { return maHocKyHienTai; }
        public void setMaHocKyHienTai(String maHocKyHienTai) { this.maHocKyHienTai = maHocKyHienTai; }

        public String getTenHocKy() { return tenHocKy; }
        public void setTenHocKy(String tenHocKy) { this.tenHocKy = tenHocKy; }

        public static class ScheduleItemDTO {
            private String maLHP;
            private String tenHP;
            private String tenLHP;
            private String tenGiangVien;
            private Integer thu;

            @JsonProperty("tietBD")
            private Integer tietBD;

            private Integer soTiet;
            private String phongHoc;

            public ScheduleItemDTO() {
            }

            public ScheduleItemDTO(String maLHP, String tenHP, String tenLHP, String tenGiangVien,
                                   Integer thu, Integer tietBD, Integer soTiet, String phongHoc) {
                this.maLHP = maLHP;
                this.tenHP = tenHP;
                this.tenLHP = tenLHP;
                this.tenGiangVien = tenGiangVien;
                this.thu = thu;
                this.tietBD = tietBD;
                this.soTiet = soTiet;
                this.phongHoc = phongHoc;
            }

            public String getMaLHP() { return maLHP; }
            public void setMaLHP(String maLHP) { this.maLHP = maLHP; }

            public String getTenHP() { return tenHP; }
            public void setTenHP(String tenHP) { this.tenHP = tenHP; }

            public String getTenLHP() { return tenLHP; }
            public void setTenLHP(String tenLHP) { this.tenLHP = tenLHP; }

            public String getTenGiangVien() { return tenGiangVien; }
            public void setTenGiangVien(String tenGiangVien) { this.tenGiangVien = tenGiangVien; }

            public Integer getThu() { return thu; }
            public void setThu(Integer thu) { this.thu = thu; }

            public Integer getTietBD() { return tietBD; }
            public void setTietBD(Integer tietBD) { this.tietBD = tietBD; }

            public Integer getSoTiet() { return soTiet; }
            public void setSoTiet(Integer soTiet) { this.soTiet = soTiet; }

            public String getPhongHoc() { return phongHoc; }
            public void setPhongHoc(String phongHoc) { this.phongHoc = phongHoc; }


        }
    }

    public static class StudentScheduleResponseDTO extends StudentScheduleResponse {
        public StudentScheduleResponseDTO() { super(); }
        public StudentScheduleResponseDTO(List<StudentScheduleResponse.ScheduleItemDTO> schedules, String maHocKyHienTai, String tenHocKy) {
            super(schedules, maHocKyHienTai, tenHocKy);
        }
    }

    // 3. Route 13: Danh sách lớp mở đăng ký (GET /student/registration/open-classes)
    public static class OpenedEnrollingClass {
        private List<OpenClassItem> classes = new ArrayList<>();
        private int page = 1;
        private int pageSize = 10;
        private long totalItems = 0;
        private int totalPages = 0;

        public OpenedEnrollingClass() {
        }

        public OpenedEnrollingClass(List<OpenClassItem> classes, int page, int pageSize, long totalItems) {
            this.classes = classes != null ? classes : new ArrayList<>();
            this.page = page;
            this.pageSize = pageSize;
            this.totalItems = totalItems;
            this.totalPages = (pageSize > 0) ? (int) Math.ceil((double) totalItems / pageSize) : 0;
        }

        public OpenedEnrollingClass(List<OpenClassItem> classes, int page, int pageSize, long totalItems, int totalPages) {
            this.classes = classes != null ? classes : new ArrayList<>();
            this.page = page;
            this.pageSize = pageSize;
            this.totalItems = totalItems;
            this.totalPages = totalPages;
        }

        public List<OpenClassItem> getClasses() { return classes; }
        public void setClasses(List<OpenClassItem> classes) { this.classes = classes; }

        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }

        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }

        public long getTotalItems() { return totalItems; }
        public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

        public static class OpenClassItem {
            private String maLHP;
            private String tenLHP;
            private String maHP;
            private String tenHP;
            private Integer soTinChi;
            private String tenGiangVien;
            private Integer thu;
            private Integer tietBatDau;
            private Integer soTiet;
            private String phongHoc;
            private Integer siSoHienTai;
            private Integer siSoToiDa;

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            private OffsetDateTime thoiGianBDDK;

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            private OffsetDateTime thoiGianKTDK;

            private String trangThai;

            public OpenClassItem() {
            }

            public OpenClassItem(String maLHP, String tenLHP, String maHP, String tenHP, Integer soTinChi,
                                 String tenGiangVien, Integer thu, Integer tietBatDau, Integer soTiet,
                                 String phongHoc, Integer siSoHienTai, Integer siSoToiDa,
                                 OffsetDateTime thoiGianBDDK, OffsetDateTime thoiGianKTDK, String trangThai) {
                this.maLHP = maLHP;
                this.tenLHP = tenLHP;
                this.maHP = maHP;
                this.tenHP = tenHP;
                this.soTinChi = soTinChi;
                this.tenGiangVien = tenGiangVien;
                this.thu = thu;
                this.tietBatDau = tietBatDau;
                this.soTiet = soTiet;
                this.phongHoc = phongHoc;
                this.siSoHienTai = siSoHienTai;
                this.siSoToiDa = siSoToiDa;
                this.thoiGianBDDK = thoiGianBDDK;
                this.thoiGianKTDK = thoiGianKTDK;
                this.trangThai = trangThai;
            }

            public String getMaLHP() { return maLHP; }
            public void setMaLHP(String maLHP) { this.maLHP = maLHP; }

            public String getTenLHP() { return tenLHP; }
            public void setTenLHP(String tenLHP) { this.tenLHP = tenLHP; }

            public String getMaHP() { return maHP; }
            public void setMaHP(String maHP) { this.maHP = maHP; }

            public String getTenHP() { return tenHP; }
            public void setTenHP(String tenHP) { this.tenHP = tenHP; }

            public Integer getSoTinChi() { return soTinChi; }
            public void setSoTinChi(Integer soTinChi) { this.soTinChi = soTinChi; }

            public String getTenGiangVien() { return tenGiangVien; }
            public void setTenGiangVien(String tenGiangVien) { this.tenGiangVien = tenGiangVien; }

            public Integer getThu() { return thu; }
            public void setThu(Integer thu) { this.thu = thu; }

            public Integer getTietBatDau() { return tietBatDau; }
            public void setTietBatDau(Integer tietBatDau) { this.tietBatDau = tietBatDau; }

            public Integer getSoTiet() { return soTiet; }
            public void setSoTiet(Integer soTiet) { this.soTiet = soTiet; }

            public String getPhongHoc() { return phongHoc; }
            public void setPhongHoc(String phongHoc) { this.phongHoc = phongHoc; }

            public Integer getSiSoHienTai() { return siSoHienTai; }
            public void setSiSoHienTai(Integer siSoHienTai) { this.siSoHienTai = siSoHienTai; }

            public Integer getSiSoToiDa() { return siSoToiDa; }
            public void setSiSoToiDa(Integer siSoToiDa) { this.siSoToiDa = siSoToiDa; }

            public OffsetDateTime getThoiGianBDDK() { return thoiGianBDDK; }
            public void setThoiGianBDDK(OffsetDateTime thoiGianBDDK) { this.thoiGianBDDK = thoiGianBDDK; }

            public OffsetDateTime getThoiGianKTDK() { return thoiGianKTDK; }
            public void setThoiGianKTDK(OffsetDateTime thoiGianKTDK) { this.thoiGianKTDK = thoiGianKTDK; }

            public String getTrangThai() { return trangThai; }
            public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
        }
    }

    // 4. Route 14: Lớp học phần đã đăng ký (GET /student/registration/registered-classes)
    public static class RegisteredClassResponse {
        private List<RegisteredClassItem> classes = new ArrayList<>();
        private Integer tongSoLopDaDangKy;
        private Integer tongSoTinChiDaDangKy;
        private Integer hanMucTinChiToiDa = 24;

        public RegisteredClassResponse() {
        }

        public RegisteredClassResponse(List<RegisteredClassItem> classes, Integer tongSoLopDaDangKy,
                                       Integer tongSoTinChiDaDangKy, Integer hanMucTinChiToiDa) {
            this.classes = classes != null ? classes : new ArrayList<>();
            this.tongSoLopDaDangKy = tongSoLopDaDangKy;
            this.tongSoTinChiDaDangKy = tongSoTinChiDaDangKy;
            this.hanMucTinChiToiDa = hanMucTinChiToiDa != null ? hanMucTinChiToiDa : 24;
        }

        public List<RegisteredClassItem> getClasses() { return classes; }
        public void setClasses(List<RegisteredClassItem> classes) { this.classes = classes; }

        public List<RegisteredClassItem> getRegisteredClasses() { return classes; }
        public void setRegisteredClasses(List<RegisteredClassItem> classes) { this.classes = classes; }

        public Integer getTongSoLopDaDangKy() { return tongSoLopDaDangKy; }
        public void setTongSoLopDaDangKy(Integer tongSoLopDaDangKy) { this.tongSoLopDaDangKy = tongSoLopDaDangKy; }

        public Integer getTongSoTinChiDaDangKy() { return tongSoTinChiDaDangKy; }
        public void setTongSoTinChiDaDangKy(Integer tongSoTinChiDaDangKy) { this.tongSoTinChiDaDangKy = tongSoTinChiDaDangKy; }

        public Integer getHanMucTinChiToiDa() { return hanMucTinChiToiDa; }
        public void setHanMucTinChiToiDa(Integer hanMucTinChiToiDa) { this.hanMucTinChiToiDa = hanMucTinChiToiDa; }

        public static class RegisteredClassItem {
            private Long maHoSo;
            private String maLHP;
            private String tenHP;
            private String tenLHP;
            private Integer soTinChi;
            private Integer thu;
            private Integer tietBatDau;
            private Integer soTiet;
            private String tenGiangVien;
            private String phongHoc;
            private Boolean isEditable;
            private Boolean canCancel;
            private String trangThai;
            private String thoiGianKTDK;

            public RegisteredClassItem() {
            }

            public RegisteredClassItem(Long maHoSo, String maLHP, String tenHP, String tenLHP, Integer soTinChi,
                                       Integer thu, Integer tietBatDau, Integer soTiet, String tenGiangVien, String phongHoc) {
                this(maHoSo, maLHP, tenHP, tenLHP, soTinChi, thu, tietBatDau, soTiet, tenGiangVien, phongHoc, true, false, "DANGHOC", null);
            }

            public RegisteredClassItem(Long maHoSo, String maLHP, String tenHP, String tenLHP, Integer soTinChi,
                                       Integer thu, Integer tietBatDau, Integer soTiet, String tenGiangVien, String phongHoc,
                                       Boolean isEditable, Boolean canCancel, String trangThai, String thoiGianKTDK) {
                this.maHoSo = maHoSo;
                this.maLHP = maLHP;
                this.tenHP = tenHP;
                this.tenLHP = tenLHP;
                this.soTinChi = soTinChi;
                this.thu = thu;
                this.tietBatDau = tietBatDau;
                this.soTiet = soTiet;
                this.tenGiangVien = tenGiangVien;
                this.phongHoc = phongHoc;
                this.isEditable = isEditable;
                this.canCancel = canCancel;
                this.trangThai = trangThai;
                this.thoiGianKTDK = thoiGianKTDK;
            }

            public Long getMaHoSo() { return maHoSo; }
            public void setMaHoSo(Long maHoSo) { this.maHoSo = maHoSo; }

            public String getMaLHP() { return maLHP; }
            public void setMaLHP(String maLHP) { this.maLHP = maLHP; }

            public String getTenHP() { return tenHP; }
            public void setTenHP(String tenHP) { this.tenHP = tenHP; }

            public String getTenLHP() { return tenLHP; }
            public void setTenLHP(String tenLHP) { this.tenLHP = tenLHP; }

            public Integer getSoTinChi() { return soTinChi; }
            public void setSoTinChi(Integer soTinChi) { this.soTinChi = soTinChi; }

            public Integer getThu() { return thu; }
            public void setThu(Integer thu) { this.thu = thu; }

            public Integer getTietBatDau() { return tietBatDau; }
            public void setTietBatDau(Integer tietBatDau) { this.tietBatDau = tietBatDau; }

            public Integer getSoTiet() { return soTiet; }
            public void setSoTiet(Integer soTiet) { this.soTiet = soTiet; }

            public String getTenGiangVien() { return tenGiangVien; }
            public void setTenGiangVien(String tenGiangVien) { this.tenGiangVien = tenGiangVien; }

            public String getPhongHoc() { return phongHoc; }
            public void setPhongHoc(String phongHoc) { this.phongHoc = phongHoc; }

            public Boolean getIsEditable() { return isEditable; }
            public void setIsEditable(Boolean isEditable) { this.isEditable = isEditable; }

            public Boolean getCanCancel() { return canCancel; }
            public void setCanCancel(Boolean canCancel) { this.canCancel = canCancel; }

            public String getTrangThai() { return trangThai; }
            public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

            public String getThoiGianKTDK() { return thoiGianKTDK; }
            public void setThoiGianKTDK(String thoiGianKTDK) { this.thoiGianKTDK = thoiGianKTDK; }
        }
    }

    public static class RegisteredClassResponseDTO extends RegisteredClassResponse {
        public RegisteredClassResponseDTO() { super(); }
        public RegisteredClassResponseDTO(List<RegisteredClassResponse.RegisteredClassItem> classes, Integer tongSoLopDaDangKy,
                                          Integer tongSoTinChiDaDangKy, Integer hanMucTinChiToiDa) {
            super(classes, tongSoLopDaDangKy, tongSoTinChiDaDangKy, hanMucTinChiToiDa);
        }
    }

    // 5. Route 15: Đăng ký học phần thành công (POST /student/registration/enroll)
    public static class EnrollCourseResponse {
        private Long maHoSo;
        private String maLHP;
        private String trangThai;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime ngayDangKy;

        public EnrollCourseResponse() {
        }

        public EnrollCourseResponse(Long maHoSo, String maLHP, String trangThai, LocalDateTime ngayDangKy) {
            this.maHoSo = maHoSo;
            this.maLHP = maLHP;
            this.trangThai = trangThai;
            this.ngayDangKy = ngayDangKy;
        }

        public Long getMaHoSo() { return maHoSo; }
        public void setMaHoSo(Long maHoSo) { this.maHoSo = maHoSo; }

        public String getMaLHP() { return maLHP; }
        public void setMaLHP(String maLHP) { this.maLHP = maLHP; }

        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

        public LocalDateTime getNgayDangKy() { return ngayDangKy; }
        public void setNgayDangKy(LocalDateTime ngayDangKy) { this.ngayDangKy = ngayDangKy; }
    }

    public static class EnrollCourseResponseDTO extends EnrollCourseResponse {
        public EnrollCourseResponseDTO() { super(); }
        public EnrollCourseResponseDTO(Long maHoSo, String maLHP, String trangThai, LocalDateTime ngayDangKy) {
            super(maHoSo, maLHP, trangThai, ngayDangKy);
        }
    }

    // 6. Route 16: Hủy đăng ký học phần (DELETE /student/registration/cancel/{maHoSo})
    public static class CancelEnrollmentResponse {
        private Long maHoSo;
        private String trangThai;

        public CancelEnrollmentResponse() {
        }

        public CancelEnrollmentResponse(Long maHoSo, String trangThai) {
            this.maHoSo = maHoSo;
            this.trangThai = trangThai;
        }

        public Long getMaHoSo() { return maHoSo; }
        public void setMaHoSo(Long maHoSo) { this.maHoSo = maHoSo; }

        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    }

    public static class CancelEnrollmentResponseDTO extends CancelEnrollmentResponse {
        public CancelEnrollmentResponseDTO() { super(); }
        public CancelEnrollmentResponseDTO(Long maHoSo, String trangThai) {
            super(maHoSo, trangThai);
        }
    }

    // 7. Route 17: Yêu cầu mở lớp học phần (POST /student/registration/open-request)
    public static class OpenClassRequestResponse {
        private String maYeuCau;
        private String trangThai;

        public OpenClassRequestResponse() {
        }

        public OpenClassRequestResponse(String maYeuCau, String trangThai) {
            this.maYeuCau = maYeuCau;
            this.trangThai = trangThai;
        }

        public String getMaYeuCau() { return maYeuCau; }
        public void setMaYeuCau(String maYeuCau) { this.maYeuCau = maYeuCau; }

        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    }

    public static class OpenClassRequestResponseDTO extends OpenClassRequestResponse {
        public OpenClassRequestResponseDTO() { super(); }
        public OpenClassRequestResponseDTO(String maYeuCau, String trangThai) {
            super(maYeuCau, trangThai);
        }
    }
}
