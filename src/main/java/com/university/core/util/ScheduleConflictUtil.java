package com.university.core.util;

import com.university.core.config.JpaUtil;
import com.university.core.model.LopHocPhan;
import com.university.core.model.enums.TrangThaiHocPhan;
import com.university.core.model.enums.TrangThaiLop;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Tiện ích dùng chung kiểm tra xung đột thời gian lịch học và phân công giảng dạy.
 * 
 * Thiết kế gồm:
 * 1. checkTimeConflict: Hàm kiểm tra xung đột thời gian dùng chung giữa 2 khung giờ (Thứ, Tiết bắt đầu, Tiết kết thúc).
 * 2. checkStudentScheduleConflict: Hàm kiểm tra xung đột lịch học của Sinh viên.
 * 3. checkLecturerScheduleConflict: Hàm kiểm tra xung đột lịch dạy của Giảng viên.
 */
public final class ScheduleConflictUtil {

    private ScheduleConflictUtil() {
    }

    // =========================================================================
    // 1. HÀM KIỂM TRA XUNG ĐỘT THỜI GIAN DÙNG CHUNG
    // =========================================================================

    /**
     * Kiểm tra 2 khung giờ có bị xung đột thời gian hay không.
     * Quy tắc: Cùng Thứ và giao nhau dải tiết (max(tietBD1, tietBD2) <= min(tietKT1, tietKT2)).
     *
     * @param thu1         Thứ của lịch 1 (2 đến 8)
     * @param tietBatDau1  Tiết bắt đầu của lịch 1 (1 đến 15)
     * @param tietKetThuc1 Tiết kết thúc của lịch 1 (1 đến 15)
     * @param thu2         Thứ của lịch 2 (2 đến 8)
     * @param tietBatDau2  Tiết bắt đầu của lịch 2 (1 đến 15)
     * @param tietKetThuc2 Tiết kết thúc của lịch 2 (1 đến 15)
     * @return true nếu cùng Thứ và trùng dải tiết, ngược lại false
     */
    public static boolean checkTimeConflict(int thu1, int tietBatDau1, int tietKetThuc1,
                                            int thu2, int tietBatDau2, int tietKetThuc2) {
        if (thu1 != thu2) {
            return false;
        }
        return Math.max(tietBatDau1, tietBatDau2) <= Math.min(tietKetThuc1, tietKetThuc2);
    }

    /**
     * So sánh khung giờ (Thứ, Tiết bắt đầu, Tiết kết thúc) với một LopHocPhan.
     */
    public static boolean checkTimeConflict(int thu, int tietBatDau, int tietKetThuc, LopHocPhan lhp) {
        if (lhp == null || lhp.getThu() == null || lhp.getTietBatDau() == null || lhp.getSoTiet() == null) {
            return false;
        }
        int lhpStart = lhp.getTietBatDau();
        int lhpEnd = lhpStart + lhp.getSoTiet() - 1;
        return checkTimeConflict(thu, tietBatDau, tietKetThuc, lhp.getThu(), lhpStart, lhpEnd);
    }

    // =========================================================================
    // 2. HÀM KIỂM TRA XUNG ĐỘT LỊCH HỌC CHO SINH VIÊN
    // =========================================================================

    /**
     * Kiểm tra xung đột lịch học của sinh viên theo mã SV và thông số lịch mới.
     * Từ mã SV, lấy danh sách các lớp học phần sinh viên đang học, duyệt qua so sánh và trả về kết quả.
     *
     * @param maSV        Mã sinh viên
     * @param thu         Thứ học dự kiến (2-8)
     * @param tietBatDau  Tiết bắt đầu dự kiến (1-15)
     * @param tietKetThuc Tiết kết thúc dự kiến (1-15)
     * @param em          EntityManager hiện hành
     * @return true nếu bị trùng lịch với lớp đang học của SV, ngược lại false
     */
    public static boolean checkStudentScheduleConflict(String maSV, int thu, int tietBatDau, int tietKetThuc, EntityManager em) {
        if (maSV == null || em == null) {
            return false;
        }
        List<LopHocPhan> activeClasses = em.createQuery(
                "SELECT h.lopHocPhan FROM HoSoHocPhan h " +
                "WHERE h.sinhVien.maSV = :maSV " +
                "  AND h.trangThai IN (:statusDangKy, :statusDangHoc) " +
                "  AND h.deleted = false", LopHocPhan.class)
                .setParameter("maSV", maSV)
                .setParameter("statusDangKy", TrangThaiHocPhan.DADANGKY)
                .setParameter("statusDangHoc", TrangThaiHocPhan.DANGHOC)
                .getResultList();

        return checkStudentScheduleConflict(thu, tietBatDau, tietKetThuc, activeClasses);
    }

    /**
     * Overload tự động quản lý EntityManager cho Sinh viên.
     */
    public static boolean checkStudentScheduleConflict(String maSV, int thu, int tietBatDau, int tietKetThuc) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return checkStudentScheduleConflict(maSV, thu, tietBatDau, tietKetThuc, em);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Overload kiểm tra trực tiếp trên danh sách lớp học phần của sinh viên (thuận tiện cho service/test).
     */
    public static boolean checkStudentScheduleConflict(int thu, int tietBatDau, int tietKetThuc, List<LopHocPhan> studentClasses) {
        if (studentClasses == null) {
            return false;
        }
        for (LopHocPhan lhp : studentClasses) {
            if (checkTimeConflict(thu, tietBatDau, tietKetThuc, lhp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Overload tiện ích truyền vào LopHocPhan mới cần đăng ký.
     */
    public static boolean checkStudentScheduleConflict(String maSV, LopHocPhan targetLhp, EntityManager em) {
        if (targetLhp == null || targetLhp.getThu() == null || targetLhp.getTietBatDau() == null || targetLhp.getSoTiet() == null) {
            return false;
        }
        int tietKetThuc = targetLhp.getTietBatDau() + targetLhp.getSoTiet() - 1;
        return checkStudentScheduleConflict(maSV, targetLhp.getThu(), targetLhp.getTietBatDau(), tietKetThuc, em);
    }

    // =========================================================================
    // 3. HÀM KIỂM TRA XUNG ĐỘT LỊCH DẠY CHO GIẢNG VIÊN
    // =========================================================================

    /**
     * Kiểm tra xung đột lịch dạy của giảng viên khi phân công lớp học phần.
     * Từ mã nhân viên (mã GV), lấy danh sách các lớp học phần giảng viên đang phụ trách, duyệt qua so sánh và trả về kết quả.
     *
     * @param maGV        Mã nhân viên / giảng viên
     * @param thu         Thứ phân công dự kiến (2-8)
     * @param tietBatDau  Tiết bắt đầu dự kiến (1-15)
     * @param tietKetThuc Tiết kết thúc dự kiến (1-15)
     * @param em          EntityManager hiện hành
     * @return true nếu bị trùng lịch dạy của giảng viên, ngược lại false
     */
    public static boolean checkLecturerScheduleConflict(String maGV, int thu, int tietBatDau, int tietKetThuc, EntityManager em) {
        if (maGV == null || em == null) {
            return false;
        }
        List<LopHocPhan> activeClasses = em.createQuery(
                "SELECT l FROM LopHocPhan l " +
                "WHERE l.giangVien.maNV = :maGV " +
                "  AND l.trangThai IN (:statusMo, :statusDangHoc) " +
                "  AND l.deleted = false", LopHocPhan.class)
                .setParameter("maGV", maGV)
                .setParameter("statusMo", TrangThaiLop.MODANGKY)
                .setParameter("statusDangHoc", TrangThaiLop.DANGHOC)
                .getResultList();

        return checkLecturerScheduleConflict(thu, tietBatDau, tietKetThuc, activeClasses);
    }

    /**
     * Overload tự động quản lý EntityManager cho Giảng viên.
     */
    public static boolean checkLecturerScheduleConflict(String maGV, int thu, int tietBatDau, int tietKetThuc) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return checkLecturerScheduleConflict(maGV, thu, tietBatDau, tietKetThuc, em);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Overload kiểm tra trực tiếp trên danh sách lớp dạy của giảng viên (thuận tiện cho service/test).
     */
    public static boolean checkLecturerScheduleConflict(int thu, int tietBatDau, int tietKetThuc, List<LopHocPhan> lecturerClasses) {
        if (lecturerClasses == null) {
            return false;
        }
        for (LopHocPhan lhp : lecturerClasses) {
            if (checkTimeConflict(thu, tietBatDau, tietKetThuc, lhp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Overload tiện ích truyền vào LopHocPhan mới cần phân công.
     */
    public static boolean checkLecturerScheduleConflict(String maGV, LopHocPhan targetLhp, EntityManager em) {
        if (targetLhp == null || targetLhp.getThu() == null || targetLhp.getTietBatDau() == null || targetLhp.getSoTiet() == null) {
            return false;
        }
        int tietKetThuc = targetLhp.getTietBatDau() + targetLhp.getSoTiet() - 1;
        return checkLecturerScheduleConflict(maGV, targetLhp.getThu(), targetLhp.getTietBatDau(), tietKetThuc, em);
    }
}
