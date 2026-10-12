package com.university.modules.assistant;

import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.YeuCauMoLop;
import com.university.core.model.enums.TrangThaiHocPhan;
import com.university.core.model.enums.TrangThaiLop;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Maps persistence entities to assistant contract payloads and back. */
public final class AssistantMapper {

    private static final Pattern SEMESTER_CODE =
            Pattern.compile("^(\\d{4})_(\\d{4})_(?:HK[12]|HE)$", Pattern.CASE_INSENSITIVE);

    private AssistantMapper() {
    }

    public static AssistantResponses.SemesterDTO toSemesterDTO(HocKy semester, boolean editable) {
        if (semester == null) {
            return null;
        }
        return new AssistantResponses.SemesterDTO(
                semester.getMaHocKy(),
                formatSemesterName(semester),
                semester.getThoiGianBD(),
                semester.getThoiGianKT(),
                editable);
    }

    public static AssistantResponses.ClassDTO toClassDTO(LopHocPhan courseClass, String room) {
        if (courseClass == null) {
            return null;
        }
        AssistantResponses.ClassDTO dto = new AssistantResponses.ClassDTO();
        dto.setMaLHP(courseClass.getMaLHP());
        dto.setTenLHP(courseClass.getTenLHP());
        dto.setMaHP(courseClass.getMaHP());
        dto.setTenHP(courseClass.getTenHP());
        dto.setSoTinChi(courseClass.getSoTinChi());
        dto.setTenGiangVien(courseClass.getGiangVien() == null
                ? null : courseClass.getGiangVien().getHoTen());
        dto.setThu(courseClass.getThu());
        dto.setTietBatDau(courseClass.getTietBatDau());
        dto.setSoTiet(courseClass.getSoTiet());
        dto.setPhongHoc(room);
        dto.setSiSoHienTai(courseClass.getSiSoHienTai());
        dto.setSiSoToiDa(courseClass.getSiSoToiDa());
        dto.setThoiGianBDDK(courseClass.getThoiGianBDDK());
        dto.setThoiGianKTDK(courseClass.getThoiGianKTDK());
        dto.setTrangThai(toContractClassStatus(courseClass.getTrangThai()));
        return dto;
    }

    public static LopHocPhan toNewClassEntity(
            AssistantRequests.CreateClassRequest request,
            HocKy semester,
            HocPhan course,
            TroLyDaoTao assistant) {
        if (request == null) {
            throw new IllegalArgumentException("CreateClassRequest không được null");
        }
        LopHocPhan entity = new LopHocPhan();
        entity.setMaLHP(trim(request.getMaLHP()));
        entity.setTenLHP(trim(request.getTenLHP()));
        entity.setMonHoc(course);
        entity.setHocKy(semester);
        entity.setTroLyDaoTao(assistant);
        entity.setThu(request.getThu());
        entity.setTietBatDau(request.getTietBD());
        entity.setSoTiet(request.getSoTiet());
        entity.setSiSoToiDa(request.getSiSoToiDa());
        entity.setSiSoHienTai(0);
        entity.setTrangThai(TrangThaiLop.CHUA_MO);
        entity.setDeleted(false);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }

    public static void applyClassUpdate(
            AssistantRequests.UpdateClassRequest request, LopHocPhan entity) {
        if (request == null || entity == null) {
            throw new IllegalArgumentException("Request và lớp học phần không được null");
        }
        if (request.getTenLHP() != null) entity.setTenLHP(request.getTenLHP().trim());
        if (request.getSiSoToiDa() != null) entity.setSiSoToiDa(request.getSiSoToiDa());
        if (request.getThu() != null) entity.setThu(request.getThu());
        if (request.getTietBD() != null) entity.setTietBatDau(request.getTietBD());
        if (request.getSoTiet() != null) entity.setSoTiet(request.getSoTiet());
        entity.setUpdatedAt(LocalDateTime.now());
    }

    public static AssistantResponses.StudentDTO toStudentDTO(HoSoHocPhan enrollment) {
        if (enrollment == null) {
            return null;
        }
        return new AssistantResponses.StudentDTO(
                enrollment.getMaSV(),
                enrollment.getSinhVien() == null ? null : enrollment.getSinhVien().getHoTen(),
                enrollment.getSinhVien() == null ? null : enrollment.getSinhVien().getAnhThe(),
                enrollment.getNgayDangKy(),
                toContractEnrollmentStatus(enrollment.getTrangThai()));
    }

    public static AssistantResponses.ScheduleDTO toScheduleDTO(LichHoc schedule) {
        if (schedule == null) {
            return null;
        }
        return new AssistantResponses.ScheduleDTO(
                schedule.getMaLichHoc(), schedule.getNgayHoc(), schedule.getPhongHoc());
    }

    public static AssistantResponses.ClassRequestDTO toClassRequestDTO(YeuCauMoLop request) {
        if (request == null) {
            return null;
        }
        AssistantResponses.ClassRequestDTO dto = new AssistantResponses.ClassRequestDTO();
        dto.setMaYeuCau(request.getMaYeuCau());
        dto.setMaSV(request.getMaSV());
        dto.setHoTen(request.getHoTenSinhVien());
        dto.setMaHP(request.getMaHP());
        dto.setTenHP(request.getTenHP());
        dto.setLyDo(request.getLyDo());
        dto.setLichHocMongMuon(request.getLichHocMongMuon());
        dto.setTrangThai(request.getTrangThai() == null ? null : request.getTrangThai().name());
        dto.setNgayTao(request.getNgayTao());
        return dto;
    }

    public static String toContractClassStatus(TrangThaiLop status) {
        if (status == null) {
            return null;
        }
        return status == TrangThaiLop.MODANGKY ? "DANG_MO" : status.name();
    }

    public static TrangThaiLop toEntityClassStatus(String contractStatus) {
        String normalized = AssistantValidator.normalizeStatus(contractStatus);
        if (normalized == null) {
            return null;
        }
        if ("DANG_MO".equals(normalized)) {
            return TrangThaiLop.MODANGKY;
        }
        try {
            return TrangThaiLop.valueOf(normalized);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Trạng thái lớp không hợp lệ: " + contractStatus, exception);
        }
    }

    private static String toContractEnrollmentStatus(TrangThaiHocPhan status) {
        if (status == null) {
            return null;
        }
        return status == TrangThaiHocPhan.DADANGKY ? "DANG_MO" : status.name();
    }

    private static String formatSemesterName(HocKy semester) {
        String label = "Học kỳ";
        if (semester.getTenHocKy() != null) {
            label = switch (semester.getTenHocKy()) {
                case HOCKY1 -> "Học kỳ 1";
                case HOCKY2 -> "Học kỳ 2";
                case HOCKYHE -> "Học kỳ hè";
            };
        }
        Matcher matcher = SEMESTER_CODE.matcher(semester.getMaHocKy() == null ? "" : semester.getMaHocKy());
        return matcher.matches()
                ? label + " (" + matcher.group(1) + " - " + matcher.group(2) + ")"
                : label;
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
