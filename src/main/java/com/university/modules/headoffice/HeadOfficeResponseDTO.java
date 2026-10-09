package com.university.modules.headoffice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class HeadOfficeResponseDTO {
    private HeadOfficeResponseDTO() { }

    public static class CreateCourseResponseDTO {
        private String maHP; private String tenHP; private Integer soTinChi;
        public CreateCourseResponseDTO() { }
        public CreateCourseResponseDTO(String maHP, String tenHP, Integer soTinChi) { this.maHP = maHP; this.tenHP = tenHP; this.soTinChi = soTinChi; }
        public String getMaHP() { return maHP; }
        public String getTenHP() { return tenHP; }
        public Integer getSoTinChi() { return soTinChi; }
    }

    public static class CreateSemesterResponseDTO {
        private String maHocKy; private String tenHocKy; private LocalDate thoiGianBD; private LocalDate thoiGianKT;
        public CreateSemesterResponseDTO() { }
        public CreateSemesterResponseDTO(String maHocKy, String tenHocKy, LocalDate thoiGianBD, LocalDate thoiGianKT) { this.maHocKy = maHocKy; this.tenHocKy = tenHocKy; this.thoiGianBD = thoiGianBD; this.thoiGianKT = thoiGianKT; }
        public String getMaHocKy() { return maHocKy; }
        public String getTenHocKy() { return tenHocKy; }
        public LocalDate getThoiGianBD() { return thoiGianBD; }
        public LocalDate getThoiGianKT() { return thoiGianKT; }
    }

    public static class UpdateCourseResponseDTO {
        private String maHP; private String tenHP; private Integer soTinChi;
        public UpdateCourseResponseDTO() { }
        public UpdateCourseResponseDTO(String maHP, String tenHP, Integer soTinChi) { this.maHP = maHP; this.tenHP = tenHP; this.soTinChi = soTinChi; }
        public String getMaHP() { return maHP; }
        public String getTenHP() { return tenHP; }
        public Integer getSoTinChi() { return soTinChi; }
    }

    public static class HeadOfficeCourseResponseDTO {
        private List<CourseItem> items; private int page; private int size; private long totalElements; private int totalPages;
        public HeadOfficeCourseResponseDTO() { }
        public HeadOfficeCourseResponseDTO(List<CourseItem> items, int page, int size, long totalElements) { this.items = items; this.page = page; this.size = size; this.totalElements = totalElements; this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0; }
        public List<CourseItem> getItems() { return items; }
        public int getPage() { return page; }
        public int getSize() { return size; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public static class CourseItem {
            private String maHP; private String tenHP; private Integer soTinChi;
            public CourseItem() { }
            public CourseItem(String maHP, String tenHP, Integer soTinChi) { this.maHP = maHP; this.tenHP = tenHP; this.soTinChi = soTinChi; }
            public String getMaHP() { return maHP; }
            public String getTenHP() { return tenHP; }
            public Integer getSoTinChi() { return soTinChi; }
        }
    }

    public static class HeadOfficeSemesterResponseDTO {
        private List<SemesterItem> items; private long totalElements;
        public HeadOfficeSemesterResponseDTO() { }
        public HeadOfficeSemesterResponseDTO(List<SemesterItem> items, long totalElements) { this.items = items; this.totalElements = totalElements; }
        public List<SemesterItem> getItems() { return items; }
        public long getTotalElements() { return totalElements; }
        public static class SemesterItem {
            private String maHocKy; private String tenHocKy; private LocalDate thoiGianBD; private LocalDate thoiGianKT;
            public SemesterItem() { }
            public SemesterItem(String maHocKy, String tenHocKy, LocalDate thoiGianBD, LocalDate thoiGianKT) { this.maHocKy = maHocKy; this.tenHocKy = tenHocKy; this.thoiGianBD = thoiGianBD; this.thoiGianKT = thoiGianKT; }
            public String getMaHocKy() { return maHocKy; }
            public String getTenHocKy() { return tenHocKy; }
            public LocalDate getThoiGianBD() { return thoiGianBD; }
            public LocalDate getThoiGianKT() { return thoiGianKT; }
        }
    }

    public static class HeadOfficeLecturerResponseDTO {
        private List<LecturerItem> items; private int page; private int size; private long totalElements; private int totalPages;
        public HeadOfficeLecturerResponseDTO() { }
        public HeadOfficeLecturerResponseDTO(List<LecturerItem> items, int page, int size, long totalElements) { this.items = items; this.page = page; this.size = size; this.totalElements = totalElements; this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0; }
        public List<LecturerItem> getItems() { return items; }
        public int getPage() { return page; }
        public int getSize() { return size; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public static class LecturerItem {
            private String maNV; private String hoTen; private String hocVi; private String khoa; private String email; private String anhThe; private Integer soLopPhuTrach;
            public LecturerItem() { }
            public LecturerItem(String maNV, String hoTen, String hocVi, String khoa, String email, String anhThe, Integer soLopPhuTrach) {
                this.maNV = maNV; this.hoTen = hoTen; this.hocVi = hocVi; this.khoa = khoa; this.email = email; this.anhThe = anhThe; this.soLopPhuTrach = soLopPhuTrach;
            }
            public String getMaNV() { return maNV; }
            public String getHoTen() { return hoTen; }
            public String getHocVi() { return hocVi; }
            public String getKhoa() { return khoa; }
            public String getEmail() { return email; }
            public String getAnhThe() { return anhThe; }
            public Integer getSoLopPhuTrach() { return soLopPhuTrach; }
        }
    }

    public static class HeadOfficeStudentResponseDTO {
        private List<StudentItem> items; private int page; private int size; private long totalElements; private int totalPages;
        public HeadOfficeStudentResponseDTO() { }
        public HeadOfficeStudentResponseDTO(List<StudentItem> items, int page, int size, long totalElements) { this.items = items; this.page = page; this.size = size; this.totalElements = totalElements; this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0; }
        public List<StudentItem> getItems() { return items; }
        public int getPage() { return page; }
        public int getSize() { return size; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public static class StudentItem {
            private String maSV; private String hoTen; private String gioiTinh; private String nienKhoa; private String khoa; private String chuyenNganh; private String anhThe;
            public StudentItem() { }
            public StudentItem(String maSV, String hoTen, String gioiTinh, String nienKhoa, String khoa, String chuyenNganh, String anhThe) {
                this.maSV = maSV; this.hoTen = hoTen; this.gioiTinh = gioiTinh; this.nienKhoa = nienKhoa; this.khoa = khoa; this.chuyenNganh = chuyenNganh; this.anhThe = anhThe;
            }
            public String getMaSV() { return maSV; }
            public String getHoTen() { return hoTen; }
            public String getGioiTinh() { return gioiTinh; }
            public String getNienKhoa() { return nienKhoa; }
            public String getKhoa() { return khoa; }
            public String getChuyenNganh() { return chuyenNganh; }
            public String getAnhThe() { return anhThe; }
        }
    }

    public static class HeadOfficeClassResponseDTO {
        private List<ClassItem> items; private int page; private int size; private long totalElements; private int totalPages;
        public HeadOfficeClassResponseDTO() { }
        public HeadOfficeClassResponseDTO(List<ClassItem> items, int page, int size, long totalElements) { this.items = items; this.page = page; this.size = size; this.totalElements = totalElements; this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0; }
        public List<ClassItem> getItems() { return items; }
        public int getPage() { return page; }
        public int getSize() { return size; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public static class ClassItem {
            private String maLHP; private String tenLHP; private String maHP; private String tenHP; private String maGV; private String tenGV; private Integer siSoHienTai; private Integer siSoToiDa; private String trangThai; private Boolean khoaDiem;
            public ClassItem() { }
            public ClassItem(String maLHP, String tenLHP, String maHP, String tenHP, String maGV, String tenGV, Integer siSoHienTai, Integer siSoToiDa, String trangThai, Boolean khoaDiem) { this.maLHP = maLHP; this.tenLHP = tenLHP; this.maHP = maHP; this.tenHP = tenHP; this.maGV = maGV; this.tenGV = tenGV; this.siSoHienTai = siSoHienTai; this.siSoToiDa = siSoToiDa; this.trangThai = trangThai; this.khoaDiem = khoaDiem; }
            public String getMaLHP() { return maLHP; }
            public String getTenLHP() { return tenLHP; }
            public String getMaHP() { return maHP; }
            public String getTenHP() { return tenHP; }
            public String getMaGV() { return maGV; }
            public String getTenGV() { return tenGV; }
            public Integer getSiSoHienTai() { return siSoHienTai; }
            public Integer getSiSoToiDa() { return siSoToiDa; }
            public String getTrangThai() { return trangThai; }
            public Boolean getKhoaDiem() { return khoaDiem; }
        }
    }

    public static class HeadOfficeClassGradeResponseDTO {
        private String maLHP; private String tenLHP; private Boolean khoaDiem; private Integer siSoHienTai; private List<GradeItem> items;
        public HeadOfficeClassGradeResponseDTO() { }
        public HeadOfficeClassGradeResponseDTO(String maLHP, String tenLHP, Boolean khoaDiem, Integer siSoHienTai, List<GradeItem> items) { this.maLHP = maLHP; this.tenLHP = tenLHP; this.khoaDiem = khoaDiem; this.siSoHienTai = siSoHienTai; this.items = items; }
        public String getMaLHP() { return maLHP; }
        public String getTenLHP() { return tenLHP; }
        public Boolean getKhoaDiem() { return khoaDiem; }
        public Integer getSiSoHienTai() { return siSoHienTai; }
        public List<GradeItem> getItems() { return items; }
        public static class GradeItem {
            private Long maHoSo; private String maSV; private String hoTen; private String anhThe; private BigDecimal diemQuaTrinh; private BigDecimal diemCuoiKy; private BigDecimal diemTongKet; private String ketQua; private Integer soBuoiVang; private String trangThai;
            public GradeItem() { }
            public GradeItem(Long maHoSo, String maSV, String hoTen, String anhThe, BigDecimal diemQuaTrinh, BigDecimal diemCuoiKy, BigDecimal diemTongKet, String ketQua, Integer soBuoiVang, String trangThai) { this.maHoSo = maHoSo; this.maSV = maSV; this.hoTen = hoTen; this.anhThe = anhThe; this.diemQuaTrinh = diemQuaTrinh; this.diemCuoiKy = diemCuoiKy; this.diemTongKet = diemTongKet; this.ketQua = ketQua; this.soBuoiVang = soBuoiVang; this.trangThai = trangThai; }
            public Long getMaHoSo() { return maHoSo; }
            public String getMaSV() { return maSV; }
            public String getHoTen() { return hoTen; }
            public String getAnhThe() { return anhThe; }
            public BigDecimal getDiemQuaTrinh() { return diemQuaTrinh; }
            public BigDecimal getDiemCuoiKy() { return diemCuoiKy; }
            public BigDecimal getDiemTongKet() { return diemTongKet; }
            public String getKetQua() { return ketQua; }
            public Integer getSoBuoiVang() { return soBuoiVang; }
            public String getTrangThai() { return trangThai; }
        }
    }

    public static class HeadOfficeStatisticsResponseDTO {
        private long totalStudents; private long totalLecturers; private long totalCourses; private long totalClasses; private long failedGrades; private long suspendedExamRecords;
        private Map<String, Long> studentsByNienKhoa;
        private Map<String, Long> studentsByKhoa;
        private Map<String, Long> studentsByGender;
        private Map<String, Long> academicResults;
        public HeadOfficeStatisticsResponseDTO() { }
        public HeadOfficeStatisticsResponseDTO(long totalStudents, long totalLecturers, long totalCourses, long totalClasses) { this.totalStudents = totalStudents; this.totalLecturers = totalLecturers; this.totalCourses = totalCourses; this.totalClasses = totalClasses; }
        public HeadOfficeStatisticsResponseDTO(long totalStudents, long totalLecturers, long totalCourses, long totalClasses, long failedGrades, long suspendedExamRecords) { this(totalStudents, totalLecturers, totalCourses, totalClasses); this.failedGrades = failedGrades; this.suspendedExamRecords = suspendedExamRecords; }
        public HeadOfficeStatisticsResponseDTO(long totalStudents, long totalLecturers, long totalCourses, long totalClasses, long failedGrades, long suspendedExamRecords, Map<String, Long> studentsByNienKhoa, Map<String, Long> studentsByKhoa, Map<String, Long> studentsByGender, Map<String, Long> academicResults) {
            this(totalStudents, totalLecturers, totalCourses, totalClasses, failedGrades, suspendedExamRecords);
            this.studentsByNienKhoa = studentsByNienKhoa;
            this.studentsByKhoa = studentsByKhoa;
            this.studentsByGender = studentsByGender;
            this.academicResults = academicResults;
        }
        public long getTotalStudents() { return totalStudents; }
        public long getTotalLecturers() { return totalLecturers; }
        public long getTotalCourses() { return totalCourses; }
        public long getTotalClasses() { return totalClasses; }
        public long getFailedGrades() { return failedGrades; }
        public long getSuspendedExamRecords() { return suspendedExamRecords; }
        public Map<String, Long> getStudentsByNienKhoa() { return studentsByNienKhoa; }
        public Map<String, Long> getStudentsByKhoa() { return studentsByKhoa; }
        public Map<String, Long> getStudentsByGender() { return studentsByGender; }
        public Map<String, Long> getAcademicResults() { return academicResults; }
    }
}
