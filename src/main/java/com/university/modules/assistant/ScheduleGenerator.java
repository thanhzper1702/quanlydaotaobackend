package com.university.modules.assistant;

import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/** Generates one active schedule per matching weekday in an inclusive semester range. */
public final class ScheduleGenerator {

    @FunctionalInterface
    public interface RoomAvailability {
        boolean isAvailable(LocalDate date, String room);
    }

    private static final DateTimeFormatter ID_DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final int MAX_CLASS_ID_PART_LENGTH = 9;

    private final MockRoomProvider roomProvider;

    public ScheduleGenerator(MockRoomProvider roomProvider) {
        if (roomProvider == null) {
            throw new IllegalArgumentException("roomProvider không được null");
        }
        this.roomProvider = roomProvider;
    }

    public List<LichHoc> generate(
            LopHocPhan courseClass, LocalDate semesterStart, LocalDate semesterEnd) {
        return generate(courseClass, semesterStart, semesterEnd, (date, room) -> true);
    }

    public List<LichHoc> generate(
            LopHocPhan courseClass, LocalDate semesterStart, LocalDate semesterEnd,
            RoomAvailability roomAvailability) {
        validate(courseClass, semesterStart, semesterEnd);
        if (roomAvailability == null) {
            throw new IllegalArgumentException("Bộ kiểm tra phòng không được null");
        }

        LocalDate firstMeeting = firstDateForContractDay(semesterStart, courseClass.getThu());
        List<LocalDate> meetingDates = new ArrayList<>();
        for (LocalDate date = firstMeeting; !date.isAfter(semesterEnd); date = date.plusWeeks(1)) {
            meetingDates.add(date);
        }

        String preferredRoom = roomProvider.chooseRoom();
        List<String> roomCandidates = new ArrayList<>();
        roomCandidates.add(preferredRoom);
        roomProvider.getRooms().stream()
                .filter(room -> !room.equals(preferredRoom))
                .forEach(roomCandidates::add);

        String room = roomCandidates.stream()
                .filter(candidate -> meetingDates.stream()
                        .allMatch(date -> roomAvailability.isAvailable(date, candidate)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Không còn phòng trống cho toàn bộ lịch học trong học kỳ"));

        List<LichHoc> schedules = new ArrayList<>();
        for (LocalDate date : meetingDates) {
            LichHoc schedule = new LichHoc();
            schedule.setMaLichHoc(buildScheduleId(courseClass.getMaLHP(), date));
            schedule.setLopHocPhan(courseClass);
            schedule.setNgayHoc(date);
            schedule.setPhongHoc(room);
            schedule.setDeleted(false);
            LocalDateTime now = LocalDateTime.now();
            schedule.setCreatedAt(now);
            schedule.setUpdatedAt(now);
            schedules.add(schedule);
        }
        return List.copyOf(schedules);
    }

    public static LocalDate firstDateForContractDay(LocalDate start, int contractDay) {
        if (start == null || contractDay < 2 || contractDay > 8) {
            throw new IllegalArgumentException("Ngày bắt đầu và thứ 2..8 là bắt buộc");
        }
        int targetJavaDay = contractDay == 8 ? DayOfWeek.SUNDAY.getValue() : contractDay - 1;
        int daysToAdd = Math.floorMod(targetJavaDay - start.getDayOfWeek().getValue(), 7);
        return start.plusDays(daysToAdd);
    }

    public static String buildScheduleId(String classId, LocalDate date) {
        if (classId == null || classId.isBlank() || date == null) {
            throw new IllegalArgumentException("maLHP và ngày học là bắt buộc để sinh mã lịch");
        }
        String safeClassId = classId.trim().replaceAll("[^A-Za-z0-9]", "");
        if (safeClassId.isEmpty()) {
            throw new IllegalArgumentException("maLHP phải chứa ít nhất một ký tự chữ hoặc số");
        }
        if (safeClassId.length() > MAX_CLASS_ID_PART_LENGTH) {
            safeClassId = safeClassId.substring(0, MAX_CLASS_ID_PART_LENGTH);
        }
        return "LH_" + safeClassId + "_" + ID_DATE.format(date) + "_" + shortHash(classId.trim());
    }

    private static String shortHash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(8);
            for (int index = 0; index < 4; index++) {
                hash.append(String.format("%02x", digest[index]));
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("JVM không hỗ trợ SHA-256", exception);
        }
    }

    private static void validate(
            LopHocPhan courseClass, LocalDate semesterStart, LocalDate semesterEnd) {
        if (courseClass == null) {
            throw new IllegalArgumentException("Lớp học phần không được null");
        }
        if (semesterStart == null || semesterEnd == null || semesterStart.isAfter(semesterEnd)) {
            throw new IllegalArgumentException("Khoảng ngày học kỳ không hợp lệ");
        }
        if (courseClass.getThu() == null || courseClass.getThu() < 2 || courseClass.getThu() > 8) {
            throw new IllegalArgumentException("thu phải nằm trong khoảng 2..8");
        }
        if (courseClass.getMaLHP() == null || courseClass.getMaLHP().isBlank()) {
            throw new IllegalArgumentException("maLHP là bắt buộc");
        }
    }
}
