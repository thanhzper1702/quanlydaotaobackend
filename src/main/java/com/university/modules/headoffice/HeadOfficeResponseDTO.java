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
            private String maHocKy;
            private String tenHocKy;
            private LocalDate thoiGianBD;
            private LocalDate thoiGianKT;
            private Boolean dangMo;
            @com.fasterxml.jackson.annotation.JsonProperty("isCurrent")
            private Boolean isCurrent;

            public SemesterItem() { }
            public SemesterItem(String maHocKy, String tenHocKy, LocalDate thoiGianBD, LocalDate thoiGianKT) {
                this.maHocKy = maHocKy;
                this.tenHocKy = tenHocKy;
                this.thoiGianBD = thoiGianBD;
                this.thoiGianKT = thoiGianKT;
                this.dangMo = "2026_2027_HK1".equals(maHocKy);
                this.isCurrent = "2026_2027_HK1".equals(maHocKy);
            }
            public SemesterItem(String maHocKy, String tenHocKy, LocalDate thoiGianBD, LocalDate thoiGianKT, Boolean dangMo) {
                this.maHocKy = maHocKy;
                this.tenHocKy = tenHocKy;
                this.thoiGianBD = thoiGianBD;
                this.thoiGianKT = thoiGianKT;
                this.dangMo = dangMo;
                this.isCurrent = Boolean.TRUE.equals(dangMo) || "2026_2027_HK1".equals(maHocKy);
            }
            public SemesterItem(String maHocKy, String tenHocKy, LocalDate thoiGianBD, LocalDate thoiGianKT, Boolean dangMo, Boolean isCurrent) {
                this.maHocKy = maHocKy;
                this.tenHocKy = tenHocKy;
                this.thoiGianBD = thoiGianBD;
                this.thoiGianKT = thoiGianKT;
                this.dangMo = dangMo;
                this.isCurrent = isCurrent != null && isCurrent;
            }
            public String getMaHocKy() { return maHocKy; }
            public String getTenHocKy() { return tenHocKy; }
            public LocalDate getThoiGianBD() { return thoiGianBD; }
            public LocalDate getThoiGianKT() { return thoiGianKT; }
            public Boolean getDangMo() { return dangMo; }
            public void setDangMo(Boolean dangMo) { this.dangMo = dangMo; }
            public Boolean getIsCurrent() { return isCurrent != null && isCurrent; }
            public void setIsCurrent(Boolean isCurrent) { this.isCurrent = isCurrent; }
            @com.fasterxml.jackson.annotation.JsonProperty("is_current")
            public Boolean getIsCurrentSnake() { return isCurrent != null && isCurrent; }
            public Boolean getHienHanh() { return isCurrent != null && isCurrent; }
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
            private String maNV; private String hoTen; private String hocVi; private String khoa; private String email; private String anhThe; private Integer soLopPhuTrach; private String soDienThoai;
            public LecturerItem() { }
            public LecturerItem(String maNV, String hoTen, String hocVi, String khoa, String email, String anhThe, Integer soLopPhuTrach) {
                this(maNV, hoTen, hocVi, khoa, email, anhThe, soLopPhuTrach, null);
            }
            public LecturerItem(String maNV, String hoTen, String hocVi, String khoa, String email, String anhThe, Integer soLopPhuTrach, String soDienThoai) {
                this.maNV = maNV; this.hoTen = hoTen; this.hocVi = hocVi; this.khoa = khoa; this.email = email; this.anhThe = anhThe; this.soLopPhuTrach = soLopPhuTrach; this.soDienThoai = soDienThoai;
            }
            public String getMaNV() { return maNV; }
            public String getHoTen() { return hoTen; }
            public String getHocVi() { return hocVi; }
            public String getKhoa() { return khoa; }
            public String getEmail() { return email; }
            public String getAnhThe() { return anhThe; }
            public Integer getSoLopPhuTrach() { return soLopPhuTrach; }
            public String getSoDienThoai() { return soDienThoai; }
            public String getSdt() { return soDienThoai; }
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

    public static class AcademicStandingItemDTO {
        private long count;
        private double rate;

        public AcademicStandingItemDTO() { }
        public AcademicStandingItemDTO(long count, double rate) {
            this.count = count;
            this.rate = rate;
        }
        public long getCount() { return count; }
        public double getRate() { return rate; }
    }

    public static class SemesterAcademicStatisticsResponseDTO {
        private String maHocKy;
        private String nienKhoa;
        private String khoa;
        private String hocLuc;
        private long totalStudents;
        private Map<String, AcademicStandingItemDTO> academicStandings;

        public SemesterAcademicStatisticsResponseDTO() { }
        public SemesterAcademicStatisticsResponseDTO(String maHocKy, String nienKhoa, String khoa,
                                                       String hocLuc, long totalStudents,
                                                       Map<String, AcademicStandingItemDTO> academicStandings) {
            this.maHocKy = maHocKy;
            this.nienKhoa = nienKhoa;
            this.khoa = khoa;
            this.hocLuc = hocLuc;
            this.totalStudents = totalStudents;
            this.academicStandings = academicStandings;
        }
        public String getMaHocKy() { return maHocKy; }
        public String getNienKhoa() { return nienKhoa; }
        public String getKhoa() { return khoa; }
        public String getHocLuc() { return hocLuc; }
        public long getTotalStudents() { return totalStudents; }
        public Map<String, AcademicStandingItemDTO> getAcademicStandings() { return academicStandings; }
    }
}
