package com.university.modules.assistant;

import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleGeneratorTest {

    @Test
    void generatesEveryMatchingDayInInclusiveSemesterRange() {
        MockRoomProvider rooms = new MockRoomProvider(List.of("A2-301"), bound -> 0);
        ScheduleGenerator generator = new ScheduleGenerator(rooms);
        LopHocPhan courseClass = new LopHocPhan();
        courseClass.setMaLHP("INT1405_01");
        courseClass.setThu(2);

        List<LichHoc> schedules = generator.generate(
                courseClass, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 19));

        assertEquals(3, schedules.size());
        assertEquals(LocalDate.of(2026, 1, 5), schedules.get(0).getNgayHoc());
        assertEquals(LocalDate.of(2026, 1, 19), schedules.get(2).getNgayHoc());
        assertTrue(schedules.stream().allMatch(schedule -> schedule.getPhongHoc().equals("A2-301")));
        assertTrue(schedules.stream().allMatch(schedule -> schedule.getMaLichHoc().length() <= 30));
        assertTrue(schedules.stream().allMatch(schedule -> schedule.getLopHocPhan() == courseClass));
        assertNotEquals(
                ScheduleGenerator.buildScheduleId("INT_1405_01", LocalDate.of(2026, 1, 5)),
                ScheduleGenerator.buildScheduleId("INT1_405_01", LocalDate.of(2026, 1, 5)));
    }

    @Test
    void mapsContractDayEightToSunday() {
        LocalDate firstSunday = ScheduleGenerator.firstDateForContractDay(
                LocalDate.of(2026, 1, 1), 8);

        assertEquals(DayOfWeek.SUNDAY, firstSunday.getDayOfWeek());
        assertEquals(LocalDate.of(2026, 1, 4), firstSunday);
    }

    @Test
    void rejectsInvalidRangesAndReturnsUnmodifiableSchedules() {
        ScheduleGenerator generator = new ScheduleGenerator(
                new MockRoomProvider(List.of("A1-101"), bound -> 0));
        LopHocPhan courseClass = new LopHocPhan();
        courseClass.setMaLHP("INT1405_01");
        courseClass.setThu(2);

        assertThrows(IllegalArgumentException.class, () -> generator.generate(
                courseClass, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)));
        List<LichHoc> schedules = generator.generate(
                courseClass, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 5));
        assertThrows(UnsupportedOperationException.class, () -> schedules.add(new LichHoc()));
    }
}
