package com.university.modules.assistant;

import com.university.core.util.ScheduleConflictUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistantScheduleConflictUtilTest {

    @Test
    void treatsLessonIntervalsAsInclusive() {
        assertTrue(ScheduleConflictUtil.checkTimeConflict(2, 1, 3, 2, 3, 4));
        assertTrue(ScheduleConflictUtil.checkTimeConflict(2, 10, 12, 2, 12, 12));
    }

    @Test
    void acceptsAdjacentNonOverlappingIntervalsAtContractBoundaries() {
        assertFalse(ScheduleConflictUtil.checkTimeConflict(2, 1, 3, 2, 4, 4));
        assertFalse(ScheduleConflictUtil.checkTimeConflict(2, 1, 1, 2, 12, 12));
    }

    @Test
    void doesNotReportConflictForDifferentDays() {
        assertFalse(ScheduleConflictUtil.checkTimeConflict(2, 1, 3, 3, 1, 3));
    }

    @Test
    void studentConflictOnlyAppliesToSameSemester() {
        com.university.core.model.HocKy hk1 = new com.university.core.model.HocKy();
        hk1.setMaHocKy("2025_2026_HK1");

        com.university.core.model.HocKy hk2 = new com.university.core.model.HocKy();
        hk2.setMaHocKy("2025_2026_HK2");

        com.university.core.model.LopHocPhan lhp1 = new com.university.core.model.LopHocPhan();
        lhp1.setMaLHP("LHP01");
        lhp1.setHocKy(hk1);
        lhp1.setThu(2);
        lhp1.setTietBatDau(1);
        lhp1.setSoTiet(3); // Tiết 1 - 3

        java.util.List<com.university.core.model.LopHocPhan> studentClasses = java.util.List.of(lhp1);

        // Cùng Thứ 2, Tiết 2-4 nhưng khác học kỳ (HK2) -> Không bị tính là trùng
        assertFalse(ScheduleConflictUtil.checkStudentScheduleConflict("2025_2026_HK2", 2, 2, 4, studentClasses));

        // Cùng Thứ 2, Tiết 2-4 và cùng học kỳ HK1 -> Bị trùng!
        assertTrue(ScheduleConflictUtil.checkStudentScheduleConflict("2025_2026_HK1", 2, 2, 4, studentClasses));

        // Không truyền mã học kỳ -> Vẫn so sánh toàn bộ theo cách cũ
        assertTrue(ScheduleConflictUtil.checkStudentScheduleConflict(null, 2, 2, 4, studentClasses));
    }

    @Test
    void lecturerConflictOnlyAppliesToSameSemester() {
        com.university.core.model.HocKy hk1 = new com.university.core.model.HocKy();
        hk1.setMaHocKy("2025_2026_HK1");

        com.university.core.model.HocKy hk2 = new com.university.core.model.HocKy();
        hk2.setMaHocKy("2025_2026_HK2");

        com.university.core.model.LopHocPhan lhp1 = new com.university.core.model.LopHocPhan();
        lhp1.setMaLHP("LHP_GV_01");
        lhp1.setHocKy(hk1);
        lhp1.setThu(3);
        lhp1.setTietBatDau(4);
        lhp1.setSoTiet(3); // Tiết 4 - 6

        java.util.List<com.university.core.model.LopHocPhan> lecturerClasses = java.util.List.of(lhp1);

        // Khác học kỳ -> Không trùng lịch
        assertFalse(ScheduleConflictUtil.checkLecturerScheduleConflict("2025_2026_HK2", 3, 5, 7, lecturerClasses));

        // Cùng học kỳ HK1 -> Trùng lịch
        assertTrue(ScheduleConflictUtil.checkLecturerScheduleConflict("2025_2026_HK1", 3, 5, 7, lecturerClasses));
    }
}
