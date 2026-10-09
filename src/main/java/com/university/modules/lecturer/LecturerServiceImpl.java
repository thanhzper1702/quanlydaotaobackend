package com.university.modules.lecturer;

import com.university.core.model.DiemDanh;
import com.university.core.model.GiangVien;
import com.university.core.model.HocKy;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.SinhVien;
import com.university.core.model.enums.TrangThaiHocPhan;
import com.university.core.util.ExcelUtil;
import com.university.modules.lecturer.LectureRequest.UpdateAttendanceRequest;
import com.university.modules.lecturer.LectureRequest.UpdateGradesRequest;
import com.university.modules.lecturer.LectureResponse.LecturerAttendanceResponse;
import com.university.modules.lecturer.LectureResponse.LecturerClassGradeResponse;
import com.university.modules.lecturer.LectureResponse.LecturerTeachingClassResponse;
import com.university.modules.lecturer.LectureResponse.LecturerTeachingScheduleResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LecturerServiceImpl implements LecturerService {

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final List<String> GRADE_HEADERS = List.of(
            "Mã sinh viên", "Họ tên", "Điểm quá trình", "Điểm cuối kỳ", "Điểm tổng kết", "Kết quả",
            "Số buổi vắng", "Trạng thái");

    private final LecturerRepository repository;
    private final Clock clock;

    public LecturerServiceImpl() {
        this(new LecturerRepositoryImpl(), Clock.systemUTC());
    }

    public LecturerServiceImpl(LecturerRepository repository) {
        this(repository, Clock.systemUTC());
    }

    public LecturerServiceImpl(LecturerRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public LecturerTeachingClassResponse getTeachingClasses(String maGV, String maHocKy) {
        GiangVien lecturer = requireLecturer(maGV);
        String lecturerId = trimToNull(lecturer.getMaNV());
        if (lecturerId == null) {
            lecturerId = trimToNull(maGV);
        }
        final String resolvedLecturerId = lecturerId;
        HocKy semester = resolveSemester(maHocKy, LocalDate.ofInstant(clock.instant(), VIETNAM_ZONE));
        validateSemesterInterval(semester);
        String semesterId = requireSemesterId(semester);

        List<LopHocPhan> classes = safeList(repository.findTeachingClasses(resolvedLecturerId, semesterId)).stream()
                .filter(lhp -> isUsableClass(lhp) && belongsToLecturer(lhp, resolvedLecturerId)
                        && belongsToSemester(lhp, semesterId))
                .sorted(Comparator.comparing(LopHocPhan::getMaLHP, Comparator.nullsLast(String::compareTo)))
                .toList();

        List<LecturerTeachingClassResponse.TeachingClass> data = new ArrayList<>();
        int totalStudents = 0;
        for (LopHocPhan lhp : classes) {
            int classSize = activeRoster(repository.findStudentsAndGradesInClass(lhp.getMaLHP()), lhp.getMaLHP()).size();
            totalStudents += classSize;

            LecturerTeachingClassResponse.TeachingClass item = new LecturerTeachingClassResponse.TeachingClass();
            item.setMaLHP(lhp.getMaLHP());
            item.setTenHP(lhp.getTenHP());
            item.setTenLHP(lhp.getTenLHP());
            item.setSiSo(classSize);
            item.setKhoaDiem(Boolean.TRUE.equals(lhp.getKhoaDiem()));
            item.setTrangThai(mapStatus(lhp.getTrangThai()));
            data.add(item);
        }

        LecturerTeachingClassResponse.Meta meta = new LecturerTeachingClassResponse.Meta();
        meta.setTongSoLopPhuTrach(data.size());
        meta.setTongSoSinhVien(totalStudents);

        LecturerTeachingClassResponse response = new LecturerTeachingClassResponse();
        response.setData(data);
        response.setMeta(meta);
        return response;
    }

    @Override
    public LecturerClassGradeResponse getClassGrades(String maGV, String maLHP) {
        String lecturerId = requireLecturerId(maGV);
        LopHocPhan lhp = requireOwnedClass(repository, lecturerId, maLHP);
        return buildGradeResponse(repository, lhp);
    }

    @Override
    public void updateClassGrades(String maGV, String maLHP, UpdateGradesRequest request) {
        String lecturerId = requireLecturerId(maGV);
        validateRequest(request, "Yêu cầu cập nhật điểm không hợp lệ");
        List<UpdateGradesRequest.GradeUpdate> updates = request.getBangDiem();
        validateUniqueStudentIds(updates.stream().map(UpdateGradesRequest.GradeUpdate::getMaSV).toList());
        String classId = requireClassId(maLHP);

        withClassTransaction(classId, txRepository -> {
            requireSameLecturer(txRepository, maGV, lecturerId);
            LopHocPhan lhp = requireOwnedClass(txRepository, lecturerId, classId);
            if (Boolean.TRUE.equals(lhp.getKhoaDiem())) {
                throw new LecturerException(409, "Lớp học phần đã khóa điểm");
            }

            List<HoSoHocPhan> roster = activeRoster(
                    txRepository.findStudentsAndGradesInClass(classId), classId);
            Map<String, HoSoHocPhan> recordsByStudent = roster.stream()
                    .filter(record -> !isBlank(record.getMaSV()))
                    .collect(Collectors.toMap(record -> record.getMaSV().trim(), Function.identity(), (first, ignored) -> first,
                            LinkedHashMap::new));

            List<PreparedGrade> prepared = new ArrayList<>();
            for (UpdateGradesRequest.GradeUpdate update : updates) {
                String studentId = trimToNull(update.getMaSV());
                HoSoHocPhan record = recordsByStudent.get(studentId);
                if (record == null) {
                    throw new LecturerException(400, "Sinh viên không thuộc lớp học phần này: " + studentId);
                }
                prepared.add(new PreparedGrade(record,
                        update.getDiemQuaTrinh() == null ? null : update.getDiemQuaTrinh().setScale(2, RoundingMode.HALF_UP),
                        update.getDiemCuoiKy() == null ? null : update.getDiemCuoiKy().setScale(2, RoundingMode.HALF_UP)));
            }

            for (PreparedGrade item : prepared) {
                HoSoHocPhan record = item.record();
                boolean changed = false;
                if (item.processGrade() != null && !item.processGrade().equals(record.getDiemQuaTrinh())) {
                    record.setDiemQuaTrinh(item.processGrade());
                    changed = true;
                }
                if (item.examGrade() != null && !item.examGrade().equals(record.getDiemCuoiKy())) {
                    record.setDiemCuoiKy(item.examGrade());
                    changed = true;
                }
                if (changed) {
                    txRepository.saveGrade(record);
                }
            }
            return null;
        });
    }

    @Override
    public byte[] exportGradesExcel(String maGV, String maLHP) {
        String lecturerId = requireLecturerId(maGV);
        LopHocPhan lhp = requireOwnedClass(repository, lecturerId, maLHP);
        LecturerClassGradeResponse response = buildGradeResponse(repository, lhp);
        List<List<String>> rows = response.getData().stream().map(student -> List.of(
                stringOrEmpty(student.getMaSV()),
                stringOrEmpty(student.getHoTen()),
                stringOrEmpty(student.getDiemQuaTrinh()),
                stringOrEmpty(student.getDiemCuoiKy()),
                stringOrEmpty(student.getDiemTongKet()),
                stringOrEmpty(student.getKetQua()),
                stringOrEmpty(student.getSoBuoiVang()),
                stringOrEmpty(student.getTrangThai()))).toList();
        try {
            return ExcelUtil.createWorkbook("BangDiem", GRADE_HEADERS, rows);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo tệp bảng điểm", e);
        }
    }

    @Override
    public LecturerTeachingScheduleResponse getTeachingSchedule(String maGV, String maHocKy, Integer tuan) {
        String lecturerId = requireLecturerId(maGV);
        LocalDate today = LocalDate.ofInstant(clock.instant(), VIETNAM_ZONE);
        HocKy semester = resolveSemester(maHocKy, today);
        LocalDate semesterStart = semester.getThoiGianBD();
        LocalDate semesterEnd = semester.getThoiGianKT();
        validateSemesterInterval(semester);
        String semesterId = requireSemesterId(semester);

        LocalDate firstWeekMonday = semesterStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastWeekMonday = semesterEnd.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        int totalWeeks = Math.toIntExact(ChronoUnit.WEEKS.between(firstWeekMonday, lastWeekMonday) + 1);
        int selectedWeek;
        if (tuan == null) {
            long currentWeek = ChronoUnit.WEEKS.between(firstWeekMonday,
                    today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) + 1;
            selectedWeek = (int) Math.max(1, Math.min(totalWeeks, currentWeek));
        } else {
            if (tuan <= 0 || tuan > totalWeeks) {
                throw new LecturerException(400, "Tuần học không hợp lệ");
            }
            selectedWeek = tuan;
        }

        LocalDate weekMonday = firstWeekMonday.plusWeeks(selectedWeek - 1L);
        LocalDate from = weekMonday.isBefore(semesterStart) ? semesterStart : weekMonday;
        LocalDate weekSunday = weekMonday.plusDays(6);
        LocalDate through = weekSunday.isAfter(semesterEnd) ? semesterEnd : weekSunday;

        List<LichHoc> sessions = safeList(repository.findTeachingSchedule(lecturerId, semesterId, from, through)).stream()
                .filter(session -> isSessionWithin(session, from, through)
                        && isUsableClass(session.getLopHocPhan())
                        && belongsToLecturer(session.getLopHocPhan(), lecturerId)
                        && belongsToSemester(session.getLopHocPhan(), semesterId))
                .sorted(Comparator.comparing(LichHoc::getNgayHoc, Comparator.nullsLast(LocalDate::compareTo))
                        .thenComparing(session -> session.getLopHocPhan().getTietBatDau(),
                                Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(LichHoc::getMaLichHoc, Comparator.nullsLast(String::compareTo)))
                .toList();

        List<LecturerTeachingScheduleResponse.TeachingSession> data = sessions.stream().map(session -> {
            LecturerTeachingScheduleResponse.TeachingSession item = new LecturerTeachingScheduleResponse.TeachingSession();
            LopHocPhan sessionClass = session.getLopHocPhan();
            item.setMaLichHoc(session.getMaLichHoc());
            item.setMaLHP(sessionClass.getMaLHP());
            item.setTenHP(sessionClass.getTenHP());
            item.setTenLHP(sessionClass.getTenLHP());
            item.setNgayHoc(session.getNgayHoc());
            item.setThu(session.getNgayHoc().getDayOfWeek().getValue() + 1);
            item.setTietBatDau(sessionClass.getTietBatDau());
            item.setSoTiet(sessionClass.getSoTiet());
            item.setPhongHoc(session.getPhongHoc());
            return item;
        }).toList();

        LecturerTeachingScheduleResponse.Meta meta = new LecturerTeachingScheduleResponse.Meta();
        meta.setTuanHienTai(selectedWeek);
        meta.setTongSoBuoiDayTrongTuan(data.size());
        meta.setTongSoTietDayTrongTuan(data.stream().map(LecturerTeachingScheduleResponse.TeachingSession::getSoTiet)
                .filter(Objects::nonNull).mapToInt(Integer::intValue).sum());

        LecturerTeachingScheduleResponse response = new LecturerTeachingScheduleResponse();
        response.setData(data);
        response.setMeta(meta);
        return response;
    }

    @Override
    public LecturerAttendanceResponse getAttendanceList(String maGV, String maLichHoc) {
        String lecturerId = requireLecturerId(maGV);
        LichHoc session = requireSession(repository, maLichHoc);
        LopHocPhan lhp = requireOwnedClass(repository, lecturerId, session.getLopHocPhan().getMaLHP());
        List<HoSoHocPhan> roster = activeRoster(repository.findStudentsAndGradesInClass(lhp.getMaLHP()), lhp.getMaLHP());

        Map<String, DiemDanh> attendanceByStudent = new HashMap<>();
        for (DiemDanh attendance : safeList(repository.findAttendanceBySession(session.getMaLichHoc()))) {
            if (isUsableAttendance(attendance, session, lhp.getMaLHP()) && !isBlank(attendance.getMaSV())) {
                attendanceByStudent.putIfAbsent(attendance.getMaSV().trim(), attendance);
            }
        }

        List<LecturerAttendanceResponse.AttendanceEntry> data = new ArrayList<>();
        int presentCount = 0;
        int absentCount = 0;
        for (HoSoHocPhan enrollment : roster) {
            SinhVien student = enrollment.getSinhVien();
            DiemDanh attendance = attendanceByStudent.get(student.getMaSV().trim());
            LecturerAttendanceResponse.AttendanceEntry item = new LecturerAttendanceResponse.AttendanceEntry();
            item.setMaSV(student.getMaSV());
            item.setHoTen(student.getHoTen());
            item.setAnhThe(student.getAnhThe());
            item.setCoMat(attendance == null ? Boolean.TRUE : attendance.getCoMat());
            item.setGhiChu(attendance == null ? null : attendance.getGhiChu());
            if (attendance == null || Boolean.TRUE.equals(attendance.getCoMat())) {
                presentCount++;
            } else if (attendance != null && Boolean.FALSE.equals(attendance.getCoMat())) {
                absentCount++;
            }
            data.add(item);
        }

        LecturerAttendanceResponse.Meta meta = new LecturerAttendanceResponse.Meta();
        meta.setMaLichHoc(session.getMaLichHoc());
        meta.setSiSoLop(roster.size());
        meta.setSoCoMat(presentCount);
        meta.setSoVangMat(absentCount);

        LecturerAttendanceResponse response = new LecturerAttendanceResponse();
        response.setData(data);
        response.setMeta(meta);
        return response;
    }

    @Override
    public void updateAttendance(String maGV, String maLichHoc, UpdateAttendanceRequest request) {
        String lecturerId = requireLecturerId(maGV);
        validateRequest(request, "Yêu cầu điểm danh không hợp lệ");
        List<UpdateAttendanceRequest.AttendanceUpdate> updates = request.getDanhSach();
        validateUniqueStudentIds(updates.stream().map(UpdateAttendanceRequest.AttendanceUpdate::getMaSV).toList());

        LichHoc initialSession = requireSession(repository, maLichHoc);
        LopHocPhan initialClass = initialSession.getLopHocPhan();
        String classId = requireClassId(initialClass.getMaLHP());
        String sessionId = initialSession.getMaLichHoc();

        withClassTransaction(classId, txRepository -> {
            requireSameLecturer(txRepository, maGV, lecturerId);
            LopHocPhan lhp = requireOwnedClass(txRepository, lecturerId, classId);
            LichHoc session = requireSession(txRepository, sessionId);
            if (session.getLopHocPhan() == null || !Objects.equals(classId, session.getLopHocPhan().getMaLHP())) {
                throw new LecturerException(404, "Không tìm thấy buổi học");
            }
            if (!belongsToLecturer(session.getLopHocPhan(), lecturerId)) {
                throw new LecturerException(403, "Bạn không được phân công buổi học này");
            }

            List<HoSoHocPhan> roster = activeRoster(
                    txRepository.findStudentsAndGradesInClass(classId), classId);
            Map<String, SinhVien> studentsById = roster.stream()
                    .map(HoSoHocPhan::getSinhVien)
                    .filter(Objects::nonNull)
                    .filter(student -> !isBlank(student.getMaSV()))
                    .collect(Collectors.toMap(student -> student.getMaSV().trim(), Function.identity(), (first, ignored) -> first,
                            LinkedHashMap::new));

            List<PreparedAttendance> prepared = new ArrayList<>();
            for (UpdateAttendanceRequest.AttendanceUpdate update : updates) {
                String studentId = trimToNull(update.getMaSV());
                SinhVien student = studentsById.get(studentId);
                if (student == null) {
                    throw new LecturerException(400, "Sinh viên không thuộc lớp học phần này: " + studentId);
                }
                prepared.add(new PreparedAttendance(student, update.getCoMat(), update.getGhiChu()));
            }

            for (PreparedAttendance item : prepared) {
                Optional<DiemDanh> existingResult = txRepository.findAttendanceBySessionAndStudent(sessionId,
                        item.student().getMaSV());
                DiemDanh attendance = existingResult.filter(row -> isUsableAttendance(row, session, classId)).orElse(null);
                if (attendance == null) {
                    attendance = new DiemDanh();
                    attendance.setSinhVien(item.student());
                    attendance.setLichHoc(session);
                    attendance.setCoMat(item.present());
                    attendance.setGhiChu(item.note());
                    txRepository.saveAttendance(attendance);
                } else if (!Objects.equals(attendance.getCoMat(), item.present())
                        || !Objects.equals(attendance.getGhiChu(), item.note())) {
                    attendance.setCoMat(item.present());
                    attendance.setGhiChu(item.note());
                    txRepository.saveAttendance(attendance);
                }
            }
            return null;
        });
    }

    private String requireLecturerId(String maGV) {
        return requireLecturerId(repository, maGV);
    }

    private static String requireLecturerId(LecturerRepository repo, String maGV) {
        GiangVien lecturer = requireLecturer(repo, maGV);
        return trimToNull(lecturer.getMaNV()) == null ? trimToNull(maGV) : lecturer.getMaNV().trim();
    }

    private GiangVien requireLecturer(String maGV) {
        return requireLecturer(repository, maGV);
    }

    private static GiangVien requireLecturer(LecturerRepository repo, String maGV) {
        if (isBlank(maGV)) {
            throw new LecturerException(401, "Mã giảng viên không được để trống");
        }
        GiangVien lecturer = repo.findByMaGV(maGV.trim())
                .orElseThrow(() -> new LecturerException(403, "Hồ sơ giảng viên không hoạt động"));
        if (lecturer.isDeleted() || (lecturer.getNguoiDung() != null && lecturer.getNguoiDung().isDeleted())) {
            throw new LecturerException(403, "Hồ sơ giảng viên không hoạt động");
        }
        return lecturer;
    }

    private static void requireSameLecturer(LecturerRepository repo, String maGV, String expectedLecturerId) {
        String currentLecturerId = requireLecturerId(repo, maGV);
        if (!Objects.equals(currentLecturerId, expectedLecturerId)) {
            throw new LecturerException(403, "Hồ sơ giảng viên không hoạt động");
        }
    }

    private HocKy resolveSemester(String maHocKy, LocalDate today) {
        Optional<HocKy> semesterResult = isBlank(maHocKy)
                ? repository.findCurrentSemester(today)
                : repository.findSemesterById(maHocKy.trim());
        return semesterResult
                .filter(semester -> semester != null && !semester.isDeleted())
                .orElseThrow(() -> new LecturerException(404, "Không tìm thấy học kỳ"));
    }

    private static void validateSemesterInterval(HocKy semester) {
        LocalDate start = semester.getThoiGianBD();
        LocalDate end = semester.getThoiGianKT();
        if (start == null || end == null || end.isBefore(start)) {
            throw new LecturerException(500, "Khoảng thời gian học kỳ không hợp lệ");
        }
    }

    private static String requireSemesterId(HocKy semester) {
        String semesterId = trimToNull(semester.getMaHocKy());
        if (semesterId == null) {
            throw new LecturerException(500, "Học kỳ không hợp lệ");
        }
        return semesterId;
    }

    private static LopHocPhan requireOwnedClass(LecturerRepository repo, String lecturerId, String maLHP) {
        if (isBlank(maLHP)) {
            throw new LecturerException(404, "Không tìm thấy lớp học phần");
        }
        LopHocPhan lhp = repo.findClassById(maLHP.trim())
                .filter(item -> isUsableClass(item) && Objects.equals(maLHP.trim(), item.getMaLHP()))
                .orElseThrow(() -> new LecturerException(404, "Không tìm thấy lớp học phần"));
        if (!belongsToLecturer(lhp, lecturerId)) {
            throw new LecturerException(403, "Bạn không được phân công lớp học phần này");
        }
        return lhp;
    }

    private static String requireClassId(String maLHP) {
        String classId = trimToNull(maLHP);
        if (classId == null) {
            throw new LecturerException(404, "Không tìm thấy lớp học phần");
        }
        return classId;
    }

    private static LichHoc requireSession(LecturerRepository repo, String maLichHoc) {
        if (isBlank(maLichHoc)) {
            throw new LecturerException(404, "Không tìm thấy buổi học");
        }
        LichHoc session = repo.findScheduleSession(maLichHoc.trim())
                .filter(item -> item != null && !item.isDeleted())
                .orElseThrow(() -> new LecturerException(404, "Không tìm thấy buổi học"));
        if (session.getLopHocPhan() == null || !isUsableClass(session.getLopHocPhan())) {
            throw new LecturerException(404, "Không tìm thấy buổi học");
        }
        return session;
    }

    private static LecturerClassGradeResponse buildGradeResponse(LecturerRepository repo, LopHocPhan lhp) {
        List<HoSoHocPhan> roster = activeRoster(repo.findStudentsAndGradesInClass(lhp.getMaLHP()), lhp.getMaLHP());
        List<LecturerClassGradeResponse.StudentGrade> data = new ArrayList<>();
        int enteredGradeCount = 0;
        for (HoSoHocPhan record : roster) {
            SinhVien student = record.getSinhVien();
            BigDecimal process = record.getDiemQuaTrinh();
            BigDecimal exam = record.getDiemCuoiKy();
            if (process != null && exam != null) {
                enteredGradeCount++;
            }

            LecturerClassGradeResponse.StudentGrade item = new LecturerClassGradeResponse.StudentGrade();
            item.setMaSV(student.getMaSV());
            item.setHoTen(student.getHoTen());
            item.setAnhThe(student.getAnhThe());
            item.setDiemQuaTrinh(process);
            item.setDiemCuoiKy(exam);
            item.setDiemTongKet(record.getDiemTongKet());
            item.setKetQua(mapResult(record));
            item.setSoBuoiVang(record.getSoBuoiVang());
            item.setTrangThai(mapStatus(record.getTrangThai()));
            data.add(item);
        }

        LecturerClassGradeResponse.Meta meta = new LecturerClassGradeResponse.Meta();
        meta.setMaLHP(lhp.getMaLHP());
        meta.setSiSoHienTai(roster.size());
        meta.setSoSinhVienDaNhapDiem(enteredGradeCount);
        meta.setSoSinhVienChuaNhapDiem(roster.size() - enteredGradeCount);
        meta.setKhoaDiem(Boolean.TRUE.equals(lhp.getKhoaDiem()));

        LecturerClassGradeResponse response = new LecturerClassGradeResponse();
        response.setData(data);
        response.setMeta(meta);
        return response;
    }

    private static List<HoSoHocPhan> activeRoster(List<HoSoHocPhan> records, String maLHP) {
        return safeList(records).stream()
                .filter(Objects::nonNull)
                .filter(record -> !record.isDeleted())
                .filter(record -> record.getTrangThai() != TrangThaiHocPhan.DAHUY)
                .filter(record -> {
                    LopHocPhan recordClass = record.getLopHocPhan();
                    return recordClass == null || (isUsableClass(recordClass)
                            && (isBlank(recordClass.getMaLHP()) || Objects.equals(maLHP, recordClass.getMaLHP())));
                })
                .filter(record -> {
                    SinhVien student = record.getSinhVien();
                    return student != null && !isBlank(student.getMaSV()) && !student.isDeleted()
                            && (student.getNguoiDung() == null || !student.getNguoiDung().isDeleted());
                })
                .sorted(Comparator.comparing(HoSoHocPhan::getMaSV, Comparator.nullsLast(String::compareTo)))
                .toList();
    }

    private static boolean isUsableClass(LopHocPhan lhp) {
        return lhp != null && !lhp.isDeleted()
                && (lhp.getMonHoc() == null || !lhp.getMonHoc().isDeleted())
                && (lhp.getHocKy() == null || !lhp.getHocKy().isDeleted());
    }

    private static boolean belongsToLecturer(LopHocPhan lhp, String lecturerId) {
        return lhp != null && lhp.getGiangVien() != null
                && !lhp.getGiangVien().isDeleted()
                && (lhp.getGiangVien().getNguoiDung() == null || !lhp.getGiangVien().getNguoiDung().isDeleted())
                && Objects.equals(trimToNull(lecturerId), trimToNull(lhp.getGiangVien().getMaNV()));
    }

    private static boolean belongsToSemester(LopHocPhan lhp, String semesterId) {
        return lhp != null && lhp.getHocKy() != null
                && Objects.equals(trimToNull(semesterId), trimToNull(lhp.getHocKy().getMaHocKy()));
    }

    private static boolean isSessionWithin(LichHoc session, LocalDate from, LocalDate through) {
        if (session == null || session.isDeleted() || session.getNgayHoc() == null) {
            return false;
        }
        LocalDate date = session.getNgayHoc();
        return !date.isBefore(from) && !date.isAfter(through);
    }

    private static boolean isUsableAttendance(DiemDanh attendance, LichHoc session, String maLHP) {
        if (attendance == null || attendance.isDeleted() || attendance.getSinhVien() == null
                || attendance.getSinhVien().isDeleted()
                || (attendance.getSinhVien().getNguoiDung() != null && attendance.getSinhVien().getNguoiDung().isDeleted())) {
            return false;
        }
        LichHoc attendanceSession = attendance.getLichHoc();
        if (attendanceSession != null && (attendanceSession.isDeleted()
                || !Objects.equals(session.getMaLichHoc(), attendanceSession.getMaLichHoc()))) {
            return false;
        }
        return attendanceSession == null || attendanceSession.getLopHocPhan() == null
                || (isUsableClass(attendanceSession.getLopHocPhan())
                && Objects.equals(maLHP, attendanceSession.getLopHocPhan().getMaLHP()));
    }

    private static String mapStatus(Enum<?> status) {
        if (status == null) {
            return null;
        }
        String name = status.name();
        return "MODANGKY".equals(name) || "DADANGKY".equals(name) ? "DANG_MO" : name;
    }

    private static String mapResult(HoSoHocPhan record) {
        if (record.getTrangThai() == TrangThaiHocPhan.DINHCHITHI) {
            return "Không đạt";
        }
        BigDecimal total = record.getDiemTongKet();
        if (total == null) {
            return null;
        }
        return total.compareTo(BigDecimal.valueOf(4)) >= 0 ? "Đạt" : "Không đạt";
    }

    private static void validateRequest(Object request, String message) {
        if (request == null) {
            throw new LecturerException(400, message);
        }
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(request);
        if (!violations.isEmpty()) {
            List<String> errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .distinct()
                    .sorted()
                    .toList();
            throw new LecturerException(400, message, errors);
        }
    }

    private static void validateUniqueStudentIds(List<String> studentIds) {
        Set<String> seen = new HashSet<>();
        for (String studentId : studentIds) {
            String normalized = trimToNull(studentId);
            if (normalized != null && !seen.add(normalized)) {
                throw new LecturerException(400, "Mã sinh viên bị trùng trong danh sách cập nhật");
            }
        }
    }

    private static String stringOrEmpty(Object value) {
        if (value == null) {
            return "";
        }
        return value instanceof BigDecimal decimal ? decimal.toPlainString() : value.toString();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean isBlank(String value) {
        return trimToNull(value) == null;
    }

    private static <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private <T> T withClassTransaction(String maLHP, Function<LecturerRepository, T> callback) {
        return repository.withClassTransaction(maLHP, callback);
    }

    private record PreparedGrade(HoSoHocPhan record, BigDecimal processGrade, BigDecimal examGrade) {
    }

    private record PreparedAttendance(SinhVien student, Boolean present, String note) {
    }
}
