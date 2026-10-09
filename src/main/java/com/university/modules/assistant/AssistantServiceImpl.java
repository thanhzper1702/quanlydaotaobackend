package com.university.modules.assistant;

import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.YeuCauMoLop;
import com.university.core.model.enums.TrangThaiYeuCau;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AssistantServiceImpl implements AssistantService {

    private final AssistantRepository repository;
    private final AssistantExcelExporter excelExporter;
    private final ScheduleGenerator scheduleGenerator;
    private final Clock clock;

    public AssistantServiceImpl() {
        this(new AssistantRepositoryImpl(), new AssistantExcelExporter(),
                new ScheduleGenerator(new MockRoomProvider()),
                Clock.system(ZoneId.of("Asia/Ho_Chi_Minh")));
    }

    AssistantServiceImpl(AssistantRepository repository, AssistantExcelExporter excelExporter, Clock clock) {
        this(repository, excelExporter, new ScheduleGenerator(new MockRoomProvider()), clock);
    }

    AssistantServiceImpl(AssistantRepository repository, AssistantExcelExporter excelExporter,
                         ScheduleGenerator scheduleGenerator, Clock clock) {
        this.repository = repository;
        this.excelExporter = excelExporter;
        this.scheduleGenerator = scheduleGenerator;
        this.clock = clock;
    }

    @Override
    public AssistantResponses.Result<List<AssistantResponses.SemesterDTO>> getSemesters() {
        List<HocKy> semesters = repository.findAllSemesters();
        LocalDate today = LocalDate.now(clock);
        List<AssistantResponses.SemesterDTO> data = semesters.stream()
                .map(semester -> AssistantMapper.toSemesterDTO(semester, isEditable(semester, today)))
                .toList();
        String currentSemesterId = semesters.stream()
                .filter(semester -> semester.isWithinSemester(today))
                .map(HocKy::getMaHocKy)
                .findFirst()
                .orElse(null);
        AssistantResponses.SemesterMeta meta =
                new AssistantResponses.SemesterMeta(data.size(), currentSemesterId);
        return AssistantResponses.Result.success(
                200, "Danh sách học kỳ kèm cờ isEditable", data, meta);
    }

    @Override
    public AssistantResponses.Result<List<AssistantResponses.ClassDTO>> getClassesBySemester(
            String maHocKy, int page, int limit) {
        List<String> errors = validationErrors("maHocKy", maHocKy, page, limit);
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }

        String semesterId = maHocKy.trim();
        if (repository.findSemesterById(semesterId).isEmpty()) {
            return AssistantResponses.Result.error(404, "Không tìm thấy học kỳ");
        }

        List<AssistantResponses.ClassDTO> data = repository
                .findClassesBySemester(semesterId, page, limit)
                .stream()
                .map(courseClass -> AssistantMapper.toClassDTO(
                        courseClass, representativeRoom(courseClass.getMaLHP())))
                .toList();
        long totalItems = repository.countClassesBySemester(semesterId);
        AssistantResponses.ClassMeta meta = new AssistantResponses.ClassMeta(
                page, limit, totalItems, totalPages(totalItems, limit), semesterId);
        return AssistantResponses.Result.success(
                200, "Danh sách LHP trong kỳ kèm meta", data, meta);
    }

    @Override
    public AssistantResponses.Result<AssistantResponses.CreateClassData> createClass(
            String maHocKy, String maNV, AssistantRequests.CreateClassRequest request) {
        List<String> errors = new ArrayList<>(AssistantValidator.validateRequiredId("maHocKy", maHocKy));
        errors.addAll(AssistantValidator.validateCreateClass(request));
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }
        if (maNV == null || maNV.isBlank()) {
            return AssistantResponses.Result.error(401, "Chưa xác thực trợ lý đào tạo");
        }

        String semesterId = maHocKy.trim();
        String assistantId = maNV.trim();
        String classId = request.getMaLHP().trim();
        String courseId = request.getMaHP().trim();
        HocKy semester = repository.findSemesterById(semesterId).orElse(null);
        if (semester == null) {
            return AssistantResponses.Result.error(404, "Không tìm thấy học kỳ");
        }
        HocPhan course = repository.findCourseById(courseId).orElse(null);
        if (course == null) {
            return AssistantResponses.Result.error(404, "Không tìm thấy học phần");
        }
        TroLyDaoTao assistant = repository.findAssistantById(assistantId).orElse(null);
        if (assistant == null) {
            return AssistantResponses.Result.error(404, "Không tìm thấy trợ lý đào tạo");
        }
        if (repository.findClassById(classId).isPresent()) {
            return AssistantResponses.Result.error(409, "Mã lớp học phần đã tồn tại");
        }

        LopHocPhan courseClass = AssistantMapper.toNewClassEntity(request, semester, course, assistant);
        try {
            List<LichHoc> schedules = generateConflictFreeSchedules(courseClass, semester, null);
            repository.createClassWithSchedules(courseClass, schedules);
            return AssistantResponses.Result.success(201, "Tạo lớp học phần thành công",
                    new AssistantResponses.CreateClassData(classId, schedules.size()));
        } catch (IllegalStateException exception) {
            return AssistantResponses.Result.error(409, "Không còn phòng trống phù hợp cho lịch học");
        } catch (IllegalArgumentException exception) {
            return AssistantResponses.Result.error(409, "Không thể tạo lớp học phần do dữ liệu bị xung đột");
        }
    }

    @Override
    public AssistantResponses.Result<Void> updateClass(
            String maLHP, AssistantRequests.UpdateClassRequest request) {
        List<String> idErrors = AssistantValidator.validateRequiredId("maLHP", maLHP);
        if (!idErrors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", idErrors);
        }
        String classId = maLHP.trim();
        LopHocPhan courseClass = repository.findClassById(classId).orElse(null);
        if (courseClass == null) {
            return AssistantResponses.Result.error(404, "Không tìm thấy lớp học phần");
        }

        List<String> errors = AssistantValidator.validateUpdateClass(
                request, valueOrZero(courseClass.getSiSoHienTai()), courseClass.getThu(),
                courseClass.getTietBatDau(), courseClass.getSoTiet());
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }

        boolean scheduleChanged = scheduleChanged(request, courseClass);
        AssistantMapper.applyClassUpdate(request, courseClass);
        try {
            if (scheduleChanged) {
                HocKy semester = courseClass.getHocKy();
                List<LichHoc> schedules = generateConflictFreeSchedules(
                        courseClass, semester, courseClass.getMaLHP());
                repository.updateClassWithSchedules(courseClass, schedules);
            } else {
                repository.saveClass(courseClass);
            }
        } catch (IllegalStateException exception) {
            return AssistantResponses.Result.error(409, "Không còn phòng trống phù hợp cho lịch học");
        } catch (IllegalArgumentException exception) {
            return AssistantResponses.Result.error(409, "Không thể cập nhật lớp học phần do dữ liệu bị xung đột");
        }
        return AssistantResponses.Result.success(200, "Cập nhật lớp học phần thành công");
    }

    @Override
    public AssistantResponses.Result<Void> deleteClass(String maLHP) {
        List<String> errors = AssistantValidator.validateRequiredId("maLHP", maLHP);
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }
        String classId = maLHP.trim();
        if (repository.findClassById(classId).isEmpty()) {
            return AssistantResponses.Result.error(404, "Không tìm thấy lớp học phần");
        }
        if (repository.countStudentsByClass(classId) > 0) {
            return AssistantResponses.Result.error(409, "Không thể xóa lớp học phần đã có sinh viên");
        }
        if (!repository.deleteClassWithSchedulesIfNoStudents(classId)) {
            return AssistantResponses.Result.error(409, "Không thể xóa lớp học phần do dữ liệu đã thay đổi");
        }
        return AssistantResponses.Result.success(200, "Xóa lớp học phần thành công");
    }

    @Override
    public AssistantResponses.Result<Void> assignLecturer(
            String maLHP, AssistantRequests.AssignLecturerRequest request) {
        List<String> errors = new ArrayList<>(AssistantValidator.validateRequiredId("maLHP", maLHP));
        errors.addAll(AssistantValidator.validateAssignLecturer(request));
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }

        String classId = maLHP.trim();
        String lecturerId = request.getMaGV().trim();
        LopHocPhan courseClass = repository.findClassById(classId).orElse(null);
        if (courseClass == null) {
            return AssistantResponses.Result.error(404, "Không tìm thấy lớp học phần");
        }
        if (repository.findLecturerById(lecturerId).isEmpty()) {
            return AssistantResponses.Result.error(404, "Không tìm thấy giảng viên");
        }
        if (repository.hasLecturerConflict(
                lecturerId, courseClass.getHocKy().getMaHocKy(), courseClass.getThu(),
                courseClass.getTietBatDau(), courseClass.getSoTiet(), classId)) {
            return AssistantResponses.Result.error(409, "Giảng viên bị trùng lịch dạy vào thời gian này");
        }
        if (!repository.assignLecturer(classId, lecturerId)) {
            return AssistantResponses.Result.error(409, "Không thể phân công do dữ liệu đã thay đổi");
        }
        return AssistantResponses.Result.success(200, "Phân công thành công");
    }

    @Override
    public AssistantResponses.Result<Void> openRegistrationPeriod(
            AssistantRequests.OpenRegistrationPeriodRequest request) {
        List<String> errors = AssistantValidator.validateOpenRegistrationPeriod(request);
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }

        List<String> classIds = AssistantValidator.distinctClassIds(request.getDsMaLHP());
        List<String> missingClasses = new ArrayList<>();
        List<String> conflicts = new ArrayList<>();
        for (String classId : classIds) {
            LopHocPhan courseClass = repository.findClassById(classId).orElse(null);
            if (courseClass == null) {
                missingClasses.add("Không tìm thấy lớp học phần: " + classId);
            } else {
                if (courseClass.getGiangVien() == null) {
                    conflicts.add("Lớp học phần chưa có giảng viên: " + classId);
                }
                if (courseClass.getThoiGianBDDK() != null || courseClass.getThoiGianKTDK() != null) {
                    conflicts.add("Lớp học phần đã được cấu hình đợt đăng ký: " + classId);
                }
            }
        }
        if (!missingClasses.isEmpty()) {
            return AssistantResponses.Result.error(404, "Không tìm thấy lớp học phần", missingClasses);
        }
        if (!conflicts.isEmpty()) {
            return AssistantResponses.Result.error(409, "Không thể mở đợt đăng ký", conflicts);
        }

        OffsetDateTime start = AssistantValidator.parseRegistrationTime(request.getThoiGianBD());
        OffsetDateTime end = AssistantValidator.parseRegistrationTime(request.getThoiGianKT());
        try {
            repository.updateRegistrationPeriods(classIds, start, end);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return AssistantResponses.Result.error(
                    409, "Không thể mở đợt đăng ký do dữ liệu đã thay đổi", List.of(ex.getMessage()));
        }
        return AssistantResponses.Result.success(200, "Cài đặt thời gian mở đăng ký thành công");
    }

    @Override
    public AssistantResponses.Result<List<AssistantResponses.StudentDTO>> getClassStudents(
            String maLHP, int page, int limit, boolean export) {
        List<String> errors = new ArrayList<>(AssistantValidator.validateRequiredId("maLHP", maLHP));
        if (!export) {
            errors.addAll(AssistantValidator.validatePagination(page, limit));
        }
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }

        String classId = maLHP.trim();
        LopHocPhan courseClass = repository.findClassById(classId).orElse(null);
        if (courseClass == null) {
            return AssistantResponses.Result.error(404, "Không tìm thấy lớp học phần");
        }

        if (export) {
            List<AssistantResponses.StudentDTO> allStudents = repository
                    .findStudentsByClass(classId, 1, Integer.MAX_VALUE)
                    .stream()
                    .map(AssistantMapper::toStudentDTO)
                    .toList();
            return AssistantResponses.Result.file(
                    200, "Xuất danh sách sinh viên thành công",
                    excelExporter.exportStudents(classId, allStudents));
        }

        List<AssistantResponses.StudentDTO> data = repository
                .findStudentsByClass(classId, page, limit)
                .stream()
                .map(AssistantMapper::toStudentDTO)
                .toList();
        long totalItems = repository.countStudentsByClass(classId);
        AssistantResponses.StudentMeta meta = new AssistantResponses.StudentMeta(
                classId, valueOrZero(courseClass.getSiSoHienTai()),
                valueOrZero(courseClass.getSiSoToiDa()), page, limit,
                totalItems, totalPages(totalItems, limit));
        return AssistantResponses.Result.success(
                200, "Danh sách sinh viên của lớp học phần", data, meta);
    }

    @Override
    public AssistantResponses.Result<List<AssistantResponses.ScheduleDTO>> getClassSchedules(String maLHP) {
        List<String> errors = AssistantValidator.validateRequiredId("maLHP", maLHP);
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }

        String classId = maLHP.trim();
        if (repository.findClassById(classId).isEmpty()) {
            return AssistantResponses.Result.error(404, "Không tìm thấy lớp học phần");
        }
        List<AssistantResponses.ScheduleDTO> data = repository.findSchedulesByClass(classId)
                .stream()
                .map(AssistantMapper::toScheduleDTO)
                .toList();
        return AssistantResponses.Result.success(
                200, "Chi tiết các buổi học", data,
                new AssistantResponses.ScheduleMeta(data.size()));
    }

    @Override
    public AssistantResponses.Result<Void> updateSchedule(
            String maLHP, AssistantRequests.UpdateScheduleRequest request) {
        List<String> errors = new ArrayList<>(AssistantValidator.validateRequiredId("maLHP", maLHP));
        errors.addAll(AssistantValidator.validateUpdateSchedule(request));
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }

        String classId = maLHP.trim();
        String scheduleId = request.getMaLichHoc().trim();
        LopHocPhan courseClass = repository.findClassById(classId).orElse(null);
        if (courseClass == null) {
            return AssistantResponses.Result.error(404, "Không tìm thấy lớp học phần");
        }
        LichHoc schedule = repository.findScheduleById(scheduleId).orElse(null);
        if (schedule == null || schedule.getLopHocPhan() == null
                || !classId.equals(schedule.getLopHocPhan().getMaLHP())) {
            return AssistantResponses.Result.error(404, "Không tìm thấy buổi học thuộc lớp học phần");
        }

        HocKy semester = courseClass.getHocKy();
        errors.addAll(AssistantValidator.validateDateWithinSemester(
                request.getNgayHoc(), semester == null ? null : semester.getThoiGianBD(),
                semester == null ? null : semester.getThoiGianKT()));
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }

        String room = request.getPhongHoc().trim();
        if (repository.hasRoomConflict(request.getNgayHoc(), room, courseClass.getTietBatDau(),
                courseClass.getSoTiet(), scheduleId)) {
            return AssistantResponses.Result.error(409, "Phòng học bị trùng lịch vào thời gian này");
        }
        schedule.setNgayHoc(request.getNgayHoc());
        schedule.setPhongHoc(room);
        repository.saveSchedule(schedule);
        return AssistantResponses.Result.success(200, "Cập nhật buổi học thành công");
    }

    @Override
    public AssistantResponses.Result<Void> deleteSchedule(String maLH) {
        List<String> errors = AssistantValidator.validateRequiredId("maLH", maLH);
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }
        String scheduleId = maLH.trim();
        if (repository.findScheduleById(scheduleId).isEmpty()) {
            return AssistantResponses.Result.error(404, "Không tìm thấy buổi học");
        }
        repository.deleteScheduleById(scheduleId);
        return AssistantResponses.Result.success(200, "Xóa buổi học thành công");
    }

    @Override
    public AssistantResponses.Result<List<AssistantResponses.ClassRequestDTO>> getClassRequests(
            String trangThai, int page, int limit) {
        List<String> errors = new ArrayList<>(AssistantValidator.validatePagination(page, limit));
        errors.addAll(AssistantValidator.validateClassRequestFilter(trangThai));
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }

        String normalizedStatus = normalizeOptionalStatus(trangThai);
        try {
            List<AssistantResponses.ClassRequestDTO> data = repository
                    .findClassRequests(normalizedStatus, page, limit)
                    .stream()
                    .map(AssistantMapper::toClassRequestDTO)
                    .toList();
            long totalItems = repository.countClassRequests(normalizedStatus);
            AssistantResponses.PageMeta meta = new AssistantResponses.PageMeta(
                    page, limit, totalItems, totalPages(totalItems, limit));
            return AssistantResponses.Result.success(
                    200, "Danh sách đề xuất mở lớp kèm meta", data, meta);
        } catch (IllegalArgumentException exception) {
            return AssistantResponses.Result.error(
                    400, "Dữ liệu không hợp lệ", List.of("trangThai không hợp lệ"));
        }
    }

    @Override
    public AssistantResponses.Result<Void> updateClassRequestStatus(
            String maYeuCau, AssistantRequests.UpdateClassRequestStatusRequest request) {
        List<String> errors = new ArrayList<>(
                AssistantValidator.validateRequiredId("maYeuCau", maYeuCau));
        errors.addAll(AssistantValidator.validateClassRequestStatus(request));
        if (!errors.isEmpty()) {
            return AssistantResponses.Result.error(400, "Dữ liệu không hợp lệ", errors);
        }

        YeuCauMoLop classRequest = repository.findClassRequestById(maYeuCau.trim()).orElse(null);
        if (classRequest == null) {
            return AssistantResponses.Result.error(404, "Không tìm thấy yêu cầu mở lớp");
        }

        TrangThaiYeuCau target = ClassRequestStateMachine.parseTarget(request.getTrangThai());
        if (!ClassRequestStateMachine.canTransition(classRequest.getTrangThai(), target)) {
            return AssistantResponses.Result.error(
                    409, "Chuyển trạng thái yêu cầu mở lớp không hợp lệ");
        }

        classRequest.setTrangThai(target);
        // BaseEntity.updatedAt is transient, so retain it only for this runtime object.
        classRequest.setUpdatedAt(LocalDateTime.now(clock));
        repository.saveClassRequest(classRequest);
        return AssistantResponses.Result.success(200, ClassRequestStateMachine.messageFor(target));
    }

    private List<String> validationErrors(String idField, String id, int page, int limit) {
        List<String> errors = new ArrayList<>(AssistantValidator.validateRequiredId(idField, id));
        errors.addAll(AssistantValidator.validatePagination(page, limit));
        return errors;
    }

    private List<LichHoc> generateConflictFreeSchedules(
            LopHocPhan courseClass, HocKy semester, String excludedClassId) {
        return scheduleGenerator.generate(
                courseClass, semester.getThoiGianBD(), semester.getThoiGianKT(),
                (date, room) -> !repository.hasRoomConflictExcludingClass(
                        date, room, courseClass.getTietBatDau(), courseClass.getSoTiet(), excludedClassId));
    }

    private String representativeRoom(String classId) {
        return repository.findSchedulesByClass(classId).stream()
                .map(LichHoc::getPhongHoc)
                .filter(room -> room != null && !room.isBlank())
                .findFirst()
                .orElse(null);
    }

    private static boolean isEditable(HocKy semester, LocalDate today) {
        return semester.getThoiGianKT() != null && !semester.getThoiGianKT().isBefore(today);
    }

    private static int totalPages(long totalItems, int pageSize) {
        if (totalItems <= 0 || pageSize <= 0) {
            return 0;
        }
        long pages = totalItems / pageSize + (totalItems % pageSize == 0 ? 0 : 1);
        return pages > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) pages;
    }

    private static int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private static boolean scheduleChanged(
            AssistantRequests.UpdateClassRequest request, LopHocPhan courseClass) {
        return request.getThu() != null && !Objects.equals(request.getThu(), courseClass.getThu())
                || request.getTietBD() != null
                && !Objects.equals(request.getTietBD(), courseClass.getTietBatDau())
                || request.getSoTiet() != null
                && !Objects.equals(request.getSoTiet(), courseClass.getSoTiet());
    }

    private static String normalizeOptionalStatus(String status) {
        return status == null ? null : status.trim().toUpperCase(Locale.ROOT);
    }
}
