package com.university.modules.lecturer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
public final class LectureRequest {

    private LectureRequest() {
    }
    public static class UpdateGradesRequest {

        @NotNull(message = "Danh sách điểm không được để trống")
        @Valid
        private List<@NotNull(message = "Thông tin điểm không được để trống") GradeUpdate> bangDiem;

        public UpdateGradesRequest() {
        }

        public List<GradeUpdate> getBangDiem() {
            return bangDiem;
        }

        public void setBangDiem(List<GradeUpdate> bangDiem) {
            this.bangDiem = bangDiem;
        }
        public static class GradeUpdate {

            @NotBlank(message = "Mã sinh viên không được để trống")
            private String maSV;

            @DecimalMin(value = "0.0", message = "Điểm quá trình không được nhỏ hơn 0.0")
            @DecimalMax(value = "10.0", message = "Điểm quá trình không được lớn hơn 10.0")
            private BigDecimal diemQuaTrinh;

            @DecimalMin(value = "0.0", message = "Điểm cuối kỳ không được nhỏ hơn 0.0")
            @DecimalMax(value = "10.0", message = "Điểm cuối kỳ không được lớn hơn 10.0")
            private BigDecimal diemCuoiKy;

            public GradeUpdate() {
            }

            public String getMaSV() {
                return maSV;
            }

            public void setMaSV(String maSV) {
                this.maSV = maSV;
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
        }
    }
    public static class UpdateAttendanceRequest {

        @NotNull(message = "Danh sách điểm danh không được để trống")
        @Valid
        private List<@NotNull(message = "Thông tin điểm danh không được để trống") AttendanceUpdate> danhSach;

        public UpdateAttendanceRequest() {
        }

        public List<AttendanceUpdate> getDanhSach() {
            return danhSach;
        }

        public void setDanhSach(List<AttendanceUpdate> danhSach) {
            this.danhSach = danhSach;
        }
        public static class AttendanceUpdate {

            @NotBlank(message = "Mã sinh viên không được để trống")
            private String maSV;

            @NotNull(message = "Trạng thái có mặt không được để trống")
            private Boolean coMat;

            @Size(max = 255, message = "Ghi chú không được vượt quá 255 ký tự")
            private String ghiChu;

            public AttendanceUpdate() {
            }

            public String getMaSV() {
                return maSV;
            }

            public void setMaSV(String maSV) {
                this.maSV = maSV;
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
    }
}
