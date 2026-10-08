package com.university.modules.student;

import jakarta.validation.constraints.NotBlank;

/**
 * Gom nhóm toàn bộ Request DTO của phân hệ Sinh viên.
 */
public final class StudentRequest {

    private StudentRequest() {
    }

    public static class EnrollCourseRequest {
        @NotBlank(message = "Mã lớp học phần không được để trống")
        private String maLHP;

        public EnrollCourseRequest() {
        }

        public EnrollCourseRequest(String maLHP) {
            this.maLHP = maLHP;
        }

        public String getMaLHP() {
            return maLHP;
        }

        public void setMaLHP(String maLHP) {
            this.maLHP = maLHP;
        }
    }

    public static class EnrollCourseRequestDTO extends EnrollCourseRequest {
        public EnrollCourseRequestDTO() { super(); }
        public EnrollCourseRequestDTO(String maLHP) { super(maLHP); }
    }

    public static class OpenClassRequestRequest {
        @NotBlank(message = "Mã học phần không được để trống")
        private String maHP;

        @NotBlank(message = "Lý do mở lớp không được để trống")
        private String lyDo;

        private String lichHocMongMuon;

        public OpenClassRequestRequest() {
        }

        public OpenClassRequestRequest(String maHP, String lyDo, String lichHocMongMuon) {
            this.maHP = maHP;
            this.lyDo = lyDo;
            this.lichHocMongMuon = lichHocMongMuon;
        }

        public String getMaHP() {
            return maHP;
        }

        public void setMaHP(String maHP) {
            this.maHP = maHP;
        }

        public String getLyDo() {
            return lyDo;
        }

        public void setLyDo(String lyDo) {
            this.lyDo = lyDo;
        }

        public String getLichHocMongMuon() {
            return lichHocMongMuon;
        }

        public void setLichHocMongMuon(String lichHocMongMuon) {
            this.lichHocMongMuon = lichHocMongMuon;
        }
    }

    public static class OpenClassRequestRequestDTO extends OpenClassRequestRequest {
        public OpenClassRequestRequestDTO() { super(); }
        public OpenClassRequestRequestDTO(String maHP, String lyDo, String lichHocMongMuon) {
            super(maHP, lyDo, lichHocMongMuon);
        }
    }
}
