package com.university.modules.assistant;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Namespace containing every JSON request body accepted by the assistant API.
 * Query and path parameters remain explicit controller arguments.
 */
public final class AssistantRequests {

    private AssistantRequests() {
    }

    public static class CreateClassRequest {
        private String maLHP;
        private String tenLHP;
        private String maHP;
        private Integer thu;
        private Integer tietBD;
        private Integer soTiet;
        private Integer siSoToiDa;

        public CreateClassRequest() {
        }

        public CreateClassRequest(String maLHP, String tenLHP, String maHP, Integer thu,
                                  Integer tietBD, Integer soTiet, Integer siSoToiDa) {
            this.maLHP = maLHP;
            this.tenLHP = tenLHP;
            this.maHP = maHP;
            this.thu = thu;
            this.tietBD = tietBD;
            this.soTiet = soTiet;
            this.siSoToiDa = siSoToiDa;
        }

        public String getMaLHP() { return maLHP; }
        public void setMaLHP(String maLHP) { this.maLHP = maLHP; }
        public String getTenLHP() { return tenLHP; }
        public void setTenLHP(String tenLHP) { this.tenLHP = tenLHP; }
        public String getMaHP() { return maHP; }
        public void setMaHP(String maHP) { this.maHP = maHP; }
        public Integer getThu() { return thu; }
        public void setThu(Integer thu) { this.thu = thu; }
        public Integer getTietBD() { return tietBD; }
        public void setTietBD(Integer tietBD) { this.tietBD = tietBD; }
        public Integer getSoTiet() { return soTiet; }
        public void setSoTiet(Integer soTiet) { this.soTiet = soTiet; }
        public Integer getSiSoToiDa() { return siSoToiDa; }
        public void setSiSoToiDa(Integer siSoToiDa) { this.siSoToiDa = siSoToiDa; }
    }

    public static class UpdateClassRequest {
        private String tenLHP;
        private Integer siSoToiDa;
        private Integer thu;
        private Integer tietBD;
        private Integer soTiet;

        public UpdateClassRequest() {
        }

        public UpdateClassRequest(String tenLHP, Integer siSoToiDa, Integer thu,
                                  Integer tietBD, Integer soTiet) {
            this.tenLHP = tenLHP;
            this.siSoToiDa = siSoToiDa;
            this.thu = thu;
            this.tietBD = tietBD;
            this.soTiet = soTiet;
        }

        public String getTenLHP() { return tenLHP; }
        public void setTenLHP(String tenLHP) { this.tenLHP = tenLHP; }
        public Integer getSiSoToiDa() { return siSoToiDa; }
        public void setSiSoToiDa(Integer siSoToiDa) { this.siSoToiDa = siSoToiDa; }
        public Integer getThu() { return thu; }
        public void setThu(Integer thu) { this.thu = thu; }
        public Integer getTietBD() { return tietBD; }
        public void setTietBD(Integer tietBD) { this.tietBD = tietBD; }
        public Integer getSoTiet() { return soTiet; }
        public void setSoTiet(Integer soTiet) { this.soTiet = soTiet; }
    }

    public static class AssignLecturerRequest {
        private String maGV;

        public AssignLecturerRequest() {
        }

        public AssignLecturerRequest(String maGV) {
            this.maGV = maGV;
        }

        public String getMaGV() { return maGV; }
        public void setMaGV(String maGV) { this.maGV = maGV; }
    }

    public static class OpenRegistrationPeriodRequest {
        private List<String> dsMaLHP = new ArrayList<>();
        private String thoiGianBD;
        private String thoiGianKT;

        public OpenRegistrationPeriodRequest() {
        }

        public OpenRegistrationPeriodRequest(List<String> dsMaLHP, String thoiGianBD, String thoiGianKT) {
            this.dsMaLHP = dsMaLHP;
            this.thoiGianBD = thoiGianBD;
            this.thoiGianKT = thoiGianKT;
        }

        public List<String> getDsMaLHP() { return dsMaLHP; }
        public void setDsMaLHP(List<String> dsMaLHP) { this.dsMaLHP = dsMaLHP; }
        public String getThoiGianBD() { return thoiGianBD; }
        public void setThoiGianBD(String thoiGianBD) { this.thoiGianBD = thoiGianBD; }
        public String getThoiGianKT() { return thoiGianKT; }
        public void setThoiGianKT(String thoiGianKT) { this.thoiGianKT = thoiGianKT; }
    }

    public static class UpdateScheduleRequest {
        private String maLichHoc;
        private LocalDate ngayHoc;
        private String phongHoc;

        public UpdateScheduleRequest() {
        }

        public UpdateScheduleRequest(String maLichHoc, LocalDate ngayHoc, String phongHoc) {
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

    public static class UpdateClassRequestStatusRequest {
        private String trangThai;

        public UpdateClassRequestStatusRequest() {
        }

        public UpdateClassRequestStatusRequest(String trangThai) {
            this.trangThai = trangThai;
        }

        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    }
}
