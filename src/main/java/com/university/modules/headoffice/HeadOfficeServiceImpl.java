package com.university.modules.headoffice;

import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.enums.TenHocKy;
import com.university.modules.headoffice.HeadOfficeRequestDTO.*;
import com.university.modules.headoffice.HeadOfficeResponseDTO.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HeadOfficeServiceImpl implements HeadOfficeService {
    private static final List<String> ACADEMIC_STANDING_ORDER = List.of(
            "XUAT_SAC", "GIOI", "KHA", "TRUNG_BINH", "YEU", "CHUA_XEP_LOAI");
    private static final Set<String> ACADEMIC_STANDINGS = Set.copyOf(ACADEMIC_STANDING_ORDER);
    private final HeadOfficeRepository repository;

    public HeadOfficeServiceImpl() {
        this(new HeadOfficeRepositoryImpl());
    }

    public HeadOfficeServiceImpl(HeadOfficeRepository repository) {
        this.repository = repository;
    }

    @Override
    public HeadOfficeCourseResponseDTO getCourses(int page, int limit) {
        validatePage(page, limit);
        long total = repository.countCourses();
        return new HeadOfficeCourseResponseDTO(repository.findCourses(page, limit).stream()
                .map(course -> new HeadOfficeCourseResponseDTO.CourseItem(course.getMaHP(), course.getTenHP(),
                        course.getSoTinChi()))
                .collect(Collectors.toList()), page, limit, total);
    }

    @Override
    public CreateCourseResponseDTO createCourse(CreateCourseRequestDTO request) {
        if (request == null || isBlank(request.getMaHP()) || isBlank(request.getTenHP())
                || request.getSoTinChi() == null || request.getSoTinChi() < 1 || request.getSoTinChi() > 4) {
            throw new IllegalArgumentException("Thông tin học phần không hợp lệ (Số tín chỉ từ 1 đến 4)");
        }
        if (repository.findCourseById(request.getMaHP().trim()).isPresent()) {
            throw new IllegalStateException("Học phần đã tồn tại");
        }
        HocPhan course = new HocPhan();
        course.setMaHP(request.getMaHP().trim());
        course.setTenHP(request.getTenHP().trim());
        course.setSoTinChi(request.getSoTinChi());
        HocPhan saved = repository.saveCourse(course);
        return new CreateCourseResponseDTO(saved.getMaHP(), saved.getTenHP(), saved.getSoTinChi());
    }

    @Override
    public UpdateCourseResponseDTO updateCourse(String maHP, UpdateCourseRequestDTO request) {
        if (isBlank(maHP) || request == null || isBlank(request.getTenHP()) || request.getSoTinChi() == null
                || request.getSoTinChi() < 1 || request.getSoTinChi() > 4) {
            throw new IllegalArgumentException("Thông tin cập nhật học phần không hợp lệ (Số tín chỉ từ 1 đến 4)");
        }
        HocPhan course = repository.findCourseById(maHP.trim())
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy học phần"));
        course.setTenHP(request.getTenHP().trim());
        course.setSoTinChi(request.getSoTinChi());
        HocPhan saved = repository.saveCourse(course);
        return new UpdateCourseResponseDTO(saved.getMaHP(), saved.getTenHP(), saved.getSoTinChi());
    }

    @Override
    public HeadOfficeSemesterResponseDTO getSemesters() {
        var items = repository.findAllSemesters().stream()
                .map(this::toSemesterItem)
                .collect(Collectors.toList());
        return new HeadOfficeSemesterResponseDTO(items, items.size());
    }

    @Override
    public CreateSemesterResponseDTO createSemester(CreateSemesterRequestDTO request) {
        if (request == null || isBlank(request.getMaHocKy()) || isBlank(request.getTenHocKy())
                || isBlank(request.getThoiGianBD()) || isBlank(request.getThoiGianKT())) {
            throw new IllegalArgumentException("Thông tin học kỳ không hợp lệ");
        }
        if (repository.findSemesterById(request.getMaHocKy().trim()).isPresent()) {
            throw new IllegalStateException("Học kỳ đã tồn tại");
        }
        LocalDate start = parseDate(request.getThoiGianBD());
        LocalDate end = parseDate(request.getThoiGianKT());
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Thời gian bắt đầu học kỳ không được trước thời điểm hiện tại");
        }

        // Kiểm tra không cho phép các học kỳ chồng chéo lên nhau
        boolean isOverlapped = repository.findAllSemesters().stream()
                .filter(s -> !s.getMaHocKy().equalsIgnoreCase(request.getMaHocKy().trim()))
                .anyMatch(s -> !start.isAfter(s.getThoiGianKT()) && !end.isBefore(s.getThoiGianBD()));
        if (isOverlapped) {
            throw new IllegalStateException("Thời gian học kỳ bị chồng chéo với học kỳ khác đã tồn tại");
        }

        TenHocKy semesterName;
        try {
            semesterName = TenHocKy.valueOf(request.getTenHocKy().trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Tên học kỳ phải là HOCKY1, HOCKY2 hoặc HOCKYHE");
        }
        HocKy semester = new HocKy();
        semester.setMaHocKy(request.getMaHocKy().trim());
        semester.setTenHocKy(semesterName);
        semester.setThoiGianBD(start);
        semester.setThoiGianKT(end);
        HocKy saved = repository.saveSemester(semester);
        return new CreateSemesterResponseDTO(saved.getMaHocKy(), saved.getTenHocKy().name(), saved.getThoiGianBD(),
                saved.getThoiGianKT());
    }

    @Override
    public void updateSemester(String maHocKy, UpdateSemesterRequestDTO request) {
        if (isBlank(maHocKy) || request == null || isBlank(request.getTenHocKy())
                || isBlank(request.getThoiGianBD()) || isBlank(request.getThoiGianKT())) {
            throw new IllegalArgumentException("Thông tin cập nhật học kỳ không hợp lệ");
        }
        HocKy semester = repository.findSemesterById(maHocKy.trim())
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy học kỳ: " + maHocKy));

        // Kiểm tra: Học kỳ đang diễn ra hoặc đã kết thúc không được sửa
        LocalDate today = LocalDate.now();
        boolean isCurrent = semester.getIsCurrent() || "2026_2027_HK1".equalsIgnoreCase(semester.getMaHocKy());
        boolean isEnded = semester.getThoiGianKT() != null && semester.getThoiGianKT().isBefore(today);
        if (isCurrent || isEnded || !semester.getThoiGianBD().isAfter(today)) {
            throw new IllegalStateException("Học kỳ đang diễn ra hoặc đã kết thúc không được phép chỉnh sửa");
        }

        LocalDate start = parseDate(request.getThoiGianBD());
        LocalDate end = parseDate(request.getThoiGianKT());
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        if (start.isBefore(today)) {
            throw new IllegalArgumentException("Thời gian bắt đầu học kỳ không được trước thời điểm hiện tại");
        }

        // Kiểm tra chồng chéo với học kỳ khác
        boolean isOverlapped = repository.findAllSemesters().stream()
                .filter(s -> !s.getMaHocKy().equalsIgnoreCase(maHocKy.trim()))
                .anyMatch(s -> !start.isAfter(s.getThoiGianKT()) && !end.isBefore(s.getThoiGianBD()));
        if (isOverlapped) {
            throw new IllegalStateException("Thời gian học kỳ bị chồng chéo với học kỳ khác đã tồn tại");
        }

        TenHocKy semesterName;
        try {
            semesterName = TenHocKy.valueOf(request.getTenHocKy().trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Tên học kỳ phải là HOCKY1, HOCKY2 hoặc HOCKYHE");
        }

        semester.setTenHocKy(semesterName);
        semester.setThoiGianBD(start);
        semester.setThoiGianKT(end);
        repository.saveSemester(semester);
    }

    @Override
    public HeadOfficeLecturerResponseDTO getLecturers(String khoa, String keyword, int page, int limit) {
        validatePage(page, limit);
        long total = repository.countLecturers(khoa, keyword);
        var items = repository.findLecturers(khoa, keyword, page, limit).stream()
                .map(lecturer -> new HeadOfficeLecturerResponseDTO.LecturerItem(
                        lecturer.getMaNV(),
                        lecturer.getHoTen(),
                        lecturer.getHocVi(),
                        lecturer.getKhoa(),
                        lecturer.getEmail(),
                        lecturer.getAnhThe(),
                        Math.toIntExact(repository.countClassesByLecturer(lecturer.getMaNV())),
                        lecturer.getSoDienThoai()))
                .collect(Collectors.toList());
        return new HeadOfficeLecturerResponseDTO(items, page, limit, total);
    }

    @Override
    public HeadOfficeLecturerResponseDTO getLecturers(String khoa, int page, int limit) {
        return getLecturers(khoa, null, page, limit);
    }

    @Override
    public HeadOfficeStudentResponseDTO getStudents(String nienKhoa, String khoa, String keyword, int page, int limit) {
        validatePage(page, limit);
        long total = repository.countStudents(nienKhoa, khoa, keyword);
        var items = repository.findStudents(nienKhoa, khoa, keyword, page, limit).stream()
                .map(student -> new HeadOfficeStudentResponseDTO.StudentItem(
                        student.getMaSV(),
                        student.getHoTen(),
                        student.getGioiTinh(),
                        student.getNienKhoa(),
                        student.getKhoa(),
                        student.getChuyenNganh(),
                        student.getAnhThe()))
                .collect(Collectors.toList());
        return new HeadOfficeStudentResponseDTO(items, page, limit, total);
    }

    @Override
    public HeadOfficeStudentResponseDTO getStudents(String nienKhoa, String khoa, int page, int limit) {
        return getStudents(nienKhoa, khoa, null, page, limit);
    }

    @Override
    public HeadOfficeStatisticsResponseDTO getStatistics(String maHocKy) {
        boolean hasSemester = maHocKy != null && !maHocKy.trim().isEmpty();
        if (hasSemester && repository.findSemesterById(maHocKy.trim()).isEmpty()) {
            throw new java.util.NoSuchElementException("Không tìm thấy học kỳ");
        }
        long totalClasses = hasSemester ? repository.countTotalClassesInSemester(maHocKy.trim())
                : repository.countTotalClasses();
        long failedGrades = hasSemester ? repository.countFailedGrades(maHocKy.trim()) : 0;
        long suspendedExamRecords = hasSemester ? repository.countSuspendedExamRecords(maHocKy.trim()) : 0;
        return new HeadOfficeStatisticsResponseDTO(repository.countTotalStudents(), repository.countTotalLecturers(),
                repository.countTotalCourses(), totalClasses, failedGrades, suspendedExamRecords,
                toCountMap(repository.countStudentsByNienKhoa()), toCountMap(repository.countStudentsByKhoa()),
                toCountMap(repository.countStudentsByGender()),
                toAcademicResultMap(repository.countAcademicResults(hasSemester ? maHocKy.trim() : null)));
    }

    @Override
    public SemesterAcademicStatisticsResponseDTO getSemesterAcademicStatistics(String maHocKy, String nienKhoa,
            String khoa, String hocLuc) {
        if (isBlank(maHocKy)) {
            throw new IllegalArgumentException("Vui lòng chọn học kỳ");
        }
        String semester = maHocKy.trim();
        if (repository.findSemesterById(semester).isEmpty()) {
            throw new java.util.NoSuchElementException("Không tìm thấy học kỳ");
        }

        String standingFilter = isBlank(hocLuc) || "all".equalsIgnoreCase(hocLuc.trim())
                ? "all"
                : hocLuc.trim().toUpperCase();
        if (!"all".equals(standingFilter) && !ACADEMIC_STANDINGS.contains(standingFilter)) {
            throw new IllegalArgumentException("Học lực không hợp lệ");
        }

        Map<String, Long> counts = new LinkedHashMap<>();
        ACADEMIC_STANDING_ORDER.forEach(key -> counts.put(key, 0L));
        long total = 0;
        for (Object[] row : repository.findSemesterAcademicAverages(semester, nienKhoa, khoa)) {
            double weightedScore = ((Number) row[1]).doubleValue();
            double scoredCredits = ((Number) row[2]).doubleValue();
            String standing = classifyAcademicStanding(weightedScore, scoredCredits);
            if (!"all".equals(standingFilter) && !standingFilter.equals(standing)) {
                continue;
            }
            counts.merge(standing, 1L, Long::sum);
            total++;
        }

        Map<String, AcademicStandingItemDTO> result = new LinkedHashMap<>();
        for (String key : ACADEMIC_STANDING_ORDER) {
            long count = counts.get(key);
            double rate = total == 0 ? 0 : Math.round(count * 1000.0 / total) / 10.0;
            result.put(key, new AcademicStandingItemDTO(count, rate));
        }
        return new SemesterAcademicStatisticsResponseDTO(
                semester, normalizeFilter(nienKhoa), normalizeFilter(khoa), standingFilter, total, result);
    }

    private static String classifyAcademicStanding(double weightedScore, double scoredCredits) {
        if (scoredCredits <= 0)
            return "CHUA_XEP_LOAI";
        double average = weightedScore / scoredCredits;
        if (average >= 9.0)
            return "XUAT_SAC";
        if (average >= 8.0)
            return "GIOI";
        if (average >= 6.5)
            return "KHA";
        if (average >= 5.0)
            return "TRUNG_BINH";
        return "YEU";
    }

    private static String normalizeFilter(String value) {
        return isBlank(value) || "all".equalsIgnoreCase(value.trim()) ? "all" : value.trim();
    }

    private static Map<String, Long> toCountMap(List<Object[]> rows) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put(row[0] == null ? "KHONG_XAC_DINH" : String.valueOf(row[0]), ((Number) row[1]).longValue());
        }
        return result;
    }

    private static Map<String, Long> toAcademicResultMap(List<Object[]> rows) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String resultCode = row[0] == null ? "CHUA_CO_KET_QUA" : String.valueOf(row[0]);
            String category = switch (resultCode) {
                case "A_PLUS" -> "XUAT_SAC";
                case "A" -> "GIOI";
                case "B_PLUS", "B" -> "KHA";
                case "C_PLUS", "C" -> "TRUNG_BINH";
                case "D_PLUS", "D" -> "YEU";
                case "F" -> "ROT";
                default -> resultCode;
            };
            result.merge(category, ((Number) row[1]).longValue(), Long::sum);
        }
        return result;
    }

    private HeadOfficeSemesterResponseDTO.SemesterItem toSemesterItem(HocKy semester) {
        boolean isCurrent = semester.getIsCurrent() || "2026_2027_HK1".equalsIgnoreCase(semester.getMaHocKy());
        return new HeadOfficeSemesterResponseDTO.SemesterItem(semester.getMaHocKy(), semester.getTenHocKy().name(),
                semester.getThoiGianBD(), semester.getThoiGianKT(), isCurrent, isCurrent);
    }

    private static void validatePage(int page, int limit) {
        if (page < 1 || limit < 1 || limit > 500) {
            throw new IllegalArgumentException("Page phải >= 1 và limit phải trong khoảng 1 đến 500");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Ngày phải có định dạng yyyy-MM-dd");
        }
    }
}
