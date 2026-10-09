package com.university.modules.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Namespace for assistant service results and endpoint payload types.
 *
 * <p>{@link Result} is an internal service-to-controller value and must never be
 * returned directly as an HTTP entity. The controller converts it to the shared
 * {@code com.university.core.common.ApiResponse} envelope, or to a binary file
 * response when {@link Result#getFile()} is present.</p>
 */
public final class AssistantResponses {

    private AssistantResponses() {
    }

    public static final class Result<T> {
        private final boolean success;
        private final int statusCode;
        private final String message;
        private final T data;
        private final Object meta;
        private final List<String> errors;
        private final FilePayload file;

        private Result(boolean success, int statusCode, String message, T data,
                       Object meta, List<String> errors, FilePayload file) {
            this.success = success;
            this.statusCode = statusCode;
            this.message = message;
            this.data = data;
            this.meta = meta;
            this.errors = errors == null ? null : List.copyOf(errors);
            this.file = file;
        }

        public static <T> Result<T> success(int statusCode, String message, T data) {
            return new Result<>(true, statusCode, message, data, null, null, null);
        }

        public static <T> Result<T> success(int statusCode, String message, T data, Object meta) {
            return new Result<>(true, statusCode, message, data, meta, null, null);
        }

        public static Result<Void> success(int statusCode, String message) {
            return new Result<>(true, statusCode, message, null, null, null, null);
        }

        public static <T> Result<T> error(int statusCode, String message) {
            return new Result<>(false, statusCode, message, null, null, null, null);
        }

        public static <T> Result<T> error(int statusCode, String message, List<String> errors) {
            return new Result<>(false, statusCode, message, null, null, errors, null);
        }

        public static <T> Result<T> file(int statusCode, String message, FilePayload file) {
            return new Result<>(true, statusCode, message, null, null, null, file);
        }

        public boolean isSuccess() { return success; }
        public int getStatusCode() { return statusCode; }
        public String getMessage() { return message; }
        public T getData() { return data; }
        public Object getMeta() { return meta; }
        public List<String> getErrors() { return errors; }
        public FilePayload getFile() { return file; }
        public boolean isFileResponse() { return file != null; }
    }

    public static final class FilePayload {
        private final byte[] content;
        private final String fileName;
        private final String contentType;

        public FilePayload(byte[] content, String fileName, String contentType) {
            this.content = content == null ? new byte[0] : content.clone();
            this.fileName = fileName;
            this.contentType = contentType;
        }

        public byte[] getContent() { return content.clone(); }
        public String getFileName() { return fileName; }
        public String getContentType() { return contentType; }
    }

    public static class SemesterDTO {
        private String maHocKy;
        private String tenHocKy;
        private LocalDate thoiGianBD;
        private LocalDate thoiGianKT;

        @JsonProperty("isEditable")
        private boolean editable;

        public SemesterDTO() {
        }

        public SemesterDTO(String maHocKy, String tenHocKy, LocalDate thoiGianBD,
                           LocalDate thoiGianKT, boolean editable) {
            this.maHocKy = maHocKy;
            this.tenHocKy = tenHocKy;
            this.thoiGianBD = thoiGianBD;
            this.thoiGianKT = thoiGianKT;
            this.editable = editable;
        }

        public String getMaHocKy() { return maHocKy; }
        public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }
        public String getTenHocKy() { return tenHocKy; }
        public void setTenHocKy(String tenHocKy) { this.tenHocKy = tenHocKy; }
        public LocalDate getThoiGianBD() { return thoiGianBD; }
        public void setThoiGianBD(LocalDate thoiGianBD) { this.thoiGianBD = thoiGianBD; }
        public LocalDate getThoiGianKT() { return thoiGianKT; }
        public void setThoiGianKT(LocalDate thoiGianKT) { this.thoiGianKT = thoiGianKT; }
        @JsonProperty("isEditable")
        public boolean isEditable() { return editable; }
        @JsonProperty("isEditable")
        public void setEditable(boolean editable) { this.editable = editable; }
    }

    public static class SemesterMeta {
        private int tongSoHocKy;
        private String hocKyHienTai;

        public SemesterMeta() {
        }

        public SemesterMeta(int tongSoHocKy, String hocKyHienTai) {
            this.tongSoHocKy = tongSoHocKy;
            this.hocKyHienTai = hocKyHienTai;
        }

        public int getTongSoHocKy() { return tongSoHocKy; }
        public void setTongSoHocKy(int tongSoHocKy) { this.tongSoHocKy = tongSoHocKy; }
        public String getHocKyHienTai() { return hocKyHienTai; }
        public void setHocKyHienTai(String hocKyHienTai) { this.hocKyHienTai = hocKyHienTai; }
    }

    public static class ClassDTO {
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
        private OffsetDateTime thoiGianBDDK;
        private OffsetDateTime thoiGianKTDK;
        private String trangThai;

        public ClassDTO() {
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

    public static class ClassMeta extends PageMeta {
        private String maHocKy;

        public ClassMeta() {
        }

        public ClassMeta(int page, int pageSize, long totalItems, int totalPages, String maHocKy) {
            super(page, pageSize, totalItems, totalPages);
            this.maHocKy = maHocKy;
        }

        public String getMaHocKy() { return maHocKy; }
        public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }
    }

    public static class CreateClassData {
        private String maLHP;
        private int soBuoiHocDaSinh;

        public CreateClassData() {
        }

        public CreateClassData(String maLHP, int soBuoiHocDaSinh) {
            this.maLHP = maLHP;
            this.soBuoiHocDaSinh = soBuoiHocDaSinh;
        }

        public String getMaLHP() { return maLHP; }
        public void setMaLHP(String maLHP) { this.maLHP = maLHP; }
        public int getSoBuoiHocDaSinh() { return soBuoiHocDaSinh; }
        public void setSoBuoiHocDaSinh(int soBuoiHocDaSinh) { this.soBuoiHocDaSinh = soBuoiHocDaSinh; }
    }

    public static class StudentDTO {
        private String maSV;
        private String hoTen;
        private String anhThe;
        private LocalDateTime ngayDangKy;
        private String trangThai;

        public StudentDTO() {
        }

        public StudentDTO(String maSV, String hoTen, String anhThe,
                          LocalDateTime ngayDangKy, String trangThai) {
            this.maSV = maSV;
            this.hoTen = hoTen;
            this.anhThe = anhThe;
            this.ngayDangKy = ngayDangKy;
            this.trangThai = trangThai;
        }

        public String getMaSV() { return maSV; }
        public void setMaSV(String maSV) { this.maSV = maSV; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getAnhThe() { return anhThe; }
        public void setAnhThe(String anhThe) { this.anhThe = anhThe; }
        public LocalDateTime getNgayDangKy() { return ngayDangKy; }
        public void setNgayDangKy(LocalDateTime ngayDangKy) { this.ngayDangKy = ngayDangKy; }
        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    }

    public static class StudentMeta extends PageMeta {
        private String maLHP;
        private int siSoHienTai;
        private int siSoToiDa;

        public StudentMeta() {
        }

        public StudentMeta(String maLHP, int siSoHienTai, int siSoToiDa, int page,
                           int pageSize, long totalItems, int totalPages) {
            super(page, pageSize, totalItems, totalPages);
            this.maLHP = maLHP;
            this.siSoHienTai = siSoHienTai;
            this.siSoToiDa = siSoToiDa;
        }

        public String getMaLHP() { return maLHP; }
        public void setMaLHP(String maLHP) { this.maLHP = maLHP; }
        public int getSiSoHienTai() { return siSoHienTai; }
        public void setSiSoHienTai(int siSoHienTai) { this.siSoHienTai = siSoHienTai; }
        public int getSiSoToiDa() { return siSoToiDa; }
        public void setSiSoToiDa(int siSoToiDa) { this.siSoToiDa = siSoToiDa; }
    }

    public static class ScheduleDTO {
        private String maLichHoc;
        private LocalDate ngayHoc;
        private String phongHoc;

        public ScheduleDTO() {
        }

        public ScheduleDTO(String maLichHoc, LocalDate ngayHoc, String phongHoc) {
            this.maLichHoc = maLichHoc;
            this.ngayHoc = ngayHoc;
            this.phongHoc = phongHoc;
        }

        public String getMaLichHoc() { return maLichHoc; }
        public void setMaLichHoc(String maLichHoc) { this.maLichHoc = maLichHoc; }
        public LocalDate getNgayHoc() { return ngayHoc; }
        public void setNgayHoc(LocalDate ngayHoc) { this.ngayHoc = ngayHoc; }
        public String getPhongHoc() { return phongHoc; }
        public void setPhongHoc(String phongHoc) { this.phongHoc = phongHoc; }
    }

    public static class ScheduleMeta {
        private int tongSoBuoiHoc;

        public ScheduleMeta() {
        }

        public ScheduleMeta(int tongSoBuoiHoc) {
            this.tongSoBuoiHoc = tongSoBuoiHoc;
        }

        public int getTongSoBuoiHoc() { return tongSoBuoiHoc; }
        public void setTongSoBuoiHoc(int tongSoBuoiHoc) { this.tongSoBuoiHoc = tongSoBuoiHoc; }
    }

    public static class ClassRequestDTO {
        private String maYeuCau;
        private String maSV;
        private String hoTen;
        private String maHP;
        private String tenHP;
        private String lyDo;
        private String lichHocMongMuon;
        private String trangThai;
        private LocalDateTime ngayTao;

        public ClassRequestDTO() {
        }

        public String getMaYeuCau() { return maYeuCau; }
        public void setMaYeuCau(String maYeuCau) { this.maYeuCau = maYeuCau; }
        public String getMaSV() { return maSV; }
        public void setMaSV(String maSV) { this.maSV = maSV; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getMaHP() { return maHP; }
        public void setMaHP(String maHP) { this.maHP = maHP; }
        public String getTenHP() { return tenHP; }
        public void setTenHP(String tenHP) { this.tenHP = tenHP; }
        public String getLyDo() { return lyDo; }
        public void setLyDo(String lyDo) { this.lyDo = lyDo; }
        public String getLichHocMongMuon() { return lichHocMongMuon; }
        public void setLichHocMongMuon(String lichHocMongMuon) { this.lichHocMongMuon = lichHocMongMuon; }
        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
        public LocalDateTime getNgayTao() { return ngayTao; }
        public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    }

    public static class PageMeta {
        private int page;
        private int pageSize;
        private long totalItems;
        private int totalPages;

        public PageMeta() {
        }

        public PageMeta(int page, int pageSize, long totalItems, int totalPages) {
            this.page = page;
            this.pageSize = pageSize;
            this.totalItems = totalItems;
            this.totalPages = totalPages;
        }

        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
        public long getTotalItems() { return totalItems; }
        public void setTotalItems(long totalItems) { this.totalItems = totalItems; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}
