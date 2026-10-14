package com.university.modules.student;

import com.university.core.model.*;
import com.university.core.model.enums.*;
import com.university.core.util.ScheduleConflictUtil;
import com.university.modules.student.StudentRequest.*;
import com.university.modules.student.StudentResponse.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl() {
        this.studentRepository = new StudentRepositoryImpl();
    }

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentGradeResponse getGrades(String maSV) {
        return getGrades(maSV, null);
    }

    @Override
    public StudentGradeResponse getGrades(String maSV, String maHocKy) {
        if (maSV == null || maSV.isBlank()) {
            throw new StudentBusinessException(400, "Mã sinh viên không hợp lệ");
        }

        List<HoSoHocPhan> records = studentRepository.findStudentGrades(maSV);
        if (records == null || records.isEmpty()) {
            return new StudentGradeResponse(Collections.emptyList(), 0.0, 0, "Chưa có điểm");
        }

        Map<String, List<HoSoHocPhan>> semesterMap = new LinkedHashMap<>();
        Map<String, String> semesterNames = new HashMap<>();

        for (HoSoHocPhan hs : records) {
            String maHK = (hs.getLopHocPhan() != null && hs.getLopHocPhan().getHocKy() != null)
                    ? hs.getLopHocPhan().getHocKy().getMaHocKy()
                    : "UNKNOWN";
            String tenHK = "Há»c ká»³ khĂ´ng xĂ¡c Ä‘á»‹nh";
            if (hs.getLopHocPhan() != null && hs.getLopHocPhan().getHocKy() != null) {
                tenHK = formatTenHocKy(hs.getLopHocPhan().getHocKy().getTenHocKy());
            }

            semesterMap.computeIfAbsent(maHK, k -> new ArrayList<>()).add(hs);
            semesterNames.putIfAbsent(maHK, tenHK);
        }

        List<StudentGradeResponse.SemesterGradeDTO> semesterDTOs = new ArrayList<>();
        double totalWeightedGpaAll = 0.0;
        int totalCreditsAll = 0;
        int totalPassedCreditsAll = 0;

        for (Map.Entry<String, List<HoSoHocPhan>> entry : semesterMap.entrySet()) {
            String maHK = entry.getKey();
            if (maHocKy != null && !maHocKy.isBlank() && !"all".equalsIgnoreCase(maHocKy) && !maHK.equalsIgnoreCase(maHocKy.trim())) {
                continue;
            }
            String tenHK = semesterNames.get(maHK);
            List<HoSoHocPhan> semRecords = entry.getValue();

            List<StudentGradeResponse.CourseGradeItemDTO> courseItems = new ArrayList<>();
            double semWeightedGpa = 0.0;
            int semCredits = 0;
            int semPassedCredits = 0;

            for (HoSoHocPhan hs : semRecords) {
                String maHP = "";
                String tenHP = "";
                int soTinChi = 0;
                if (hs.getLopHocPhan() != null && hs.getLopHocPhan().getMonHoc() != null) {
                    maHP = hs.getLopHocPhan().getMonHoc().getMaHP();
                    tenHP = hs.getLopHocPhan().getMonHoc().getTenHP();
                    soTinChi = hs.getLopHocPhan().getMonHoc().getSoTinChi() != null ? hs.getLopHocPhan().getMonHoc().getSoTinChi() : 0;
                }

                Double diemQT = hs.getDiemQuaTrinh() != null ? hs.getDiemQuaTrinh().doubleValue() : null;
                Double diemCK = hs.getDiemCuoiKy() != null ? hs.getDiemCuoiKy().doubleValue() : null;
                Double diemTK = hs.getDiemTongKet() != null ? hs.getDiemTongKet().doubleValue() : null;

                String ketQuaText = "Chưa có";
                if (hs.getKetQua() != null) {
                    ketQuaText = hs.isDat() ? "Đạt" : "Không đạt";
                } else if (diemTK != null) {
                    ketQuaText = diemTK >= 4.0 ? "Đạt" : "Không đạt";
                }

                courseItems.add(new StudentGradeResponse.CourseGradeItemDTO(
                        maHP, tenHP, soTinChi, diemQT, diemCK, diemTK, ketQuaText
                ));

                if (diemTK != null && soTinChi > 0) {
                    double gpa4 = convertToGpa4(diemTK);
                    semWeightedGpa += gpa4 * soTinChi;
                    semCredits += soTinChi;
                    if (diemTK >= 4.0) {
                        semPassedCredits += soTinChi;
                    }

                    totalWeightedGpaAll += gpa4 * soTinChi;
                    totalCreditsAll += soTinChi;
                    if (diemTK >= 4.0) {
                        totalPassedCreditsAll += soTinChi;
                    }
                }
            }

            double diemTBHK = semCredits > 0 ? Math.round((semWeightedGpa / semCredits) * 100.0) / 100.0 : 0.0;
            String xepLoaiHK = getAcademicClassification(diemTBHK);

            semesterDTOs.add(new StudentGradeResponse.SemesterGradeDTO(
                    maHK, tenHK, diemTBHK, semPassedCredits, xepLoaiHK, courseItems
            ));
        }

        double diemTBTichLuy = totalCreditsAll > 0 ? Math.round((totalWeightedGpaAll / totalCreditsAll) * 100.0) / 100.0 : 0.0;
        String xepLoaiHocLucTichLuy = getAcademicClassification(diemTBTichLuy);

        return new StudentGradeResponse(semesterDTOs, diemTBTichLuy, totalPassedCreditsAll, xepLoaiHocLucTichLuy);
    }

    private String formatTenHocKy(TenHocKy thk) {
        if (thk == null) return "Học kỳ 1";
        switch (thk) {
            case HOCKY1: return "Học kỳ 1";
            case HOCKY2: return "Học kỳ 2";
            case HOCKYHE: return "Học kỳ hè";
            default: return thk.name();
        }
    }

    private double convertToGpa4(double diem10) {
        if (diem10 >= 8.5) return 4.0;
        if (diem10 >= 8.0) return 3.5;
        if (diem10 >= 7.0) return 3.0;
        if (diem10 >= 6.5) return 2.5;
        if (diem10 >= 5.5) return 2.0;
        if (diem10 >= 5.0) return 1.5;
        if (diem10 >= 4.0) return 1.0;
        return 0.0;
    }

    private String getAcademicClassification(double gpa4) {
        if (gpa4 >= 3.6) return "Xuất sắc";
        if (gpa4 >= 3.2) return "Giỏi";
        if (gpa4 >= 2.5) return "Khá";
        if (gpa4 >= 2.0) return "Trung bình";
        if (gpa4 > 0.0) return "Yếu";
        return "Chưa xếp loại";
    }

    @Override
    public StudentScheduleResponse getSchedule(String maSV, String maHocKy) {
        if (maSV == null || maSV.isBlank()) {
            throw new StudentBusinessException(400, "Mã sinh viên không hợp lệ");
        }

        String activeSemester = (maHocKy != null && !maHocKy.isBlank()) ? maHocKy : "2026_2027_HK1";
        List<LichHoc> scheduleList = studentRepository.findStudentSchedule(maSV, activeSemester);

        List<StudentScheduleResponse.ScheduleItemDTO> scheduleItems = new ArrayList<>();
        String currentMaHK = activeSemester;
        String currentTenHK = formatTenHocKyString(activeSemester);

        Set<String> addedKeys = new HashSet<>();

        if (scheduleList != null && !scheduleList.isEmpty()) {
            for (LichHoc lh : scheduleList) {
                LopHocPhan lhp = lh.getLopHocPhan();
                String tenGV = (lhp != null && lhp.getGiangVien() != null && lhp.getGiangVien().getHoTen() != null)
                        ? lhp.getGiangVien().getHoTen() : "";

                String key = lh.getMaLHP() + "_" + lh.getThu() + "_" + lh.getTietBatDau();
                if (addedKeys.add(key)) {
                    scheduleItems.add(new StudentScheduleResponse.ScheduleItemDTO(
                            lh.getMaLHP(),
                            lh.getTenHP(),
                            lh.getTenLHP(),
                            tenGV,
                            lh.getThu(),
                            lh.getTietBatDau(),
                            lh.getSoTiet(),
                            lh.getPhongHoc()
                    ));
                }
            }
        } else {
            List<HoSoHocPhan> enrollments = (maHocKy != null && !maHocKy.isBlank())
                    ? studentRepository.findRegisteredClassesByHocKy(maSV, maHocKy)
                    : studentRepository.findRegisteredClassesByHocKy(maSV, "2026_2027_HK1");
            // Không fallback sang kỳ hiện tại khi sinh viên xem kỳ không có lịch

            for (HoSoHocPhan hs : enrollments) {
                LopHocPhan lhp = hs.getLopHocPhan();
                if (lhp == null || lhp.isDeleted()) continue;

                String tenHP = lhp.getMonHoc() != null ? lhp.getMonHoc().getTenHP() : "";
                String tenGV = lhp.getGiangVien() != null ? (lhp.getGiangVien().getHoTen() != null ? lhp.getGiangVien().getHoTen() : "") : "";
                String phongHoc = studentRepository.findPhongHocByMaLHP(lhp.getMaLHP());
                if (phongHoc == null || phongHoc.isBlank()) {
                    if ("LHP260121".equals(lhp.getMaLHP())) phongHoc = "A2-002";
                    else if ("LHP260122".equals(lhp.getMaLHP())) phongHoc = "A3-003";
                    else if ("LHP260123".equals(lhp.getMaLHP())) phongHoc = "A4-004";
                    else phongHoc = "A2-002";
                }

                String key = lhp.getMaLHP() + "_" + lhp.getThu() + "_" + lhp.getTietBatDau();
                if (addedKeys.add(key)) {
                    scheduleItems.add(new StudentScheduleResponse.ScheduleItemDTO(
                            lhp.getMaLHP(),
                            tenHP,
                            lhp.getTenLHP(),
                            tenGV,
                            lhp.getThu(),
                            lhp.getTietBatDau(),
                            lhp.getSoTiet(),
                            phongHoc
                    ));
                }
            }
        }

        return new StudentScheduleResponse(scheduleItems, currentMaHK, currentTenHK);
    }

    private String formatTenHocKyString(String maHocKy) {
        if (maHocKy == null) return "Học kỳ 1 (2026 - 2027)";
        if ("2026_2027_HK1".equals(maHocKy)) return "Học kỳ 1 (2026 - 2027)";
        if ("2025_2026_HK2".equals(maHocKy)) return "Học kỳ 2 (2025 - 2026)";
        if ("2025_2026_HK1".equals(maHocKy)) return "Học kỳ 1 (2025 - 2026)";
        if ("2024_2025_HK2".equals(maHocKy)) return "Học kỳ 2 (2024 - 2025)";
        if ("2024_2025_HK1".equals(maHocKy)) return "Học kỳ 1 (2024 - 2025)";
        if (maHocKy.endsWith("_HK1")) return "Học kỳ 1";
        if (maHocKy.endsWith("_HK2")) return "Học kỳ 2";
        if (maHocKy.endsWith("_HK3") || maHocKy.endsWith("_HKHE")) return "Học kỳ hè";
        return maHocKy;
    }

    @Override
    public OpenedEnrollingClass getOpenClasses(String maHP, String keyword, int page, int limit) {
        return getOpenClasses(maHP, keyword, page, limit, null);
    }

    @Override
    public OpenedEnrollingClass getOpenClasses(String maHP, String keyword, int page, int limit, String maHocKy) {
        if (page < 1) page = 1;
        if (limit < 1) limit = 10;

        String effectiveKeyword = keyword;
        if (maHP != null && !maHP.isBlank()) {
            effectiveKeyword = maHP.trim();
        }

        List<LopHocPhan> classes = studentRepository.findOpenClasses(maHocKy, effectiveKeyword, page, limit);
        long totalItems = studentRepository.countOpenClasses(maHocKy, effectiveKeyword);
        int totalPages = (int) Math.ceil((double) totalItems / limit);
        if (totalPages == 0) totalPages = 1;

        List<OpenedEnrollingClass.OpenClassItem> items = new ArrayList<>();
        for (LopHocPhan lhp : classes) {
            String tenHP = lhp.getMonHoc() != null ? lhp.getMonHoc().getTenHP() : "";
            String maHocPhan = lhp.getMonHoc() != null ? lhp.getMonHoc().getMaHP() : "";
            int soTinChi = lhp.getMonHoc() != null && lhp.getMonHoc().getSoTinChi() != null ? lhp.getMonHoc().getSoTinChi() : 0;
            String tenGV = lhp.getGiangVien() != null ? (lhp.getGiangVien().getHoTen() != null ? lhp.getGiangVien().getHoTen() : "") : "";
            String trangThaiStr = lhp.getTrangThai() != null ? lhp.getTrangThai().name() : "MODANGKY";
            String phongHoc = studentRepository.findPhongHocByMaLHP(lhp.getMaLHP());

            items.add(new OpenedEnrollingClass.OpenClassItem(
                    lhp.getMaLHP(),
                    lhp.getTenLHP(),
                    maHocPhan,
                    tenHP,
                    soTinChi,
                    tenGV,
                    lhp.getThu(),
                    lhp.getTietBatDau(),
                    lhp.getSoTiet(),
                    phongHoc,
                    lhp.getSiSoHienTai(),
                    lhp.getSiSoToiDa(),
                    lhp.getThoiGianBDDK(),
                    lhp.getThoiGianKTDK(),
                    trangThaiStr
            ));
        }

        return new OpenedEnrollingClass(items, page, limit, totalItems, totalPages);
    }

    @Override
    public RegisteredClassResponse getRegisteredClasses(String maSV) {
        return getRegisteredClasses(maSV, null);
    }

    @Override
    public RegisteredClassResponse getRegisteredClasses(String maSV, String maHocKy) {
        if (maSV == null || maSV.isBlank()) {
            throw new StudentBusinessException(400, "Mã sinh viên không hợp lệ");
        }

        List<HoSoHocPhan> list;
        if (maHocKy != null && !maHocKy.isBlank() && !"all".equalsIgnoreCase(maHocKy)) {
            list = studentRepository.findRegisteredClassesByHocKy(maSV, maHocKy.trim());
        } else {
            list = studentRepository.findRegisteredClasses(maSV);
        }

        List<RegisteredClassResponse.RegisteredClassItem> items = new ArrayList<>();
        int tongTinChi = 0;

        for (HoSoHocPhan hs : list) {
            // YÊU CẦU: danh sách ở dưới CHỈ HIỂN THỊ các lhp trong kỳ đó của sinh viên có trạng thái ĐÃ ĐĂNG KÝ (DADANGKY)
            if (hs.getTrangThai() != null && hs.getTrangThai() != TrangThaiHocPhan.DADANGKY) {
                continue;
            }
            LopHocPhan lhp = hs.getLopHocPhan();
            if (lhp == null || lhp.isDeleted()) continue;

            int stc = (lhp.getMonHoc() != null && lhp.getMonHoc().getSoTinChi() != null) ? lhp.getMonHoc().getSoTinChi() : 0;
            tongTinChi += stc;
            String tenHP = lhp.getMonHoc() != null ? lhp.getMonHoc().getTenHP() : "";
            String tenGV = lhp.getGiangVien() != null ? (lhp.getGiangVien().getHoTen() != null ? lhp.getGiangVien().getHoTen() : "") : "";
            String phongHoc = studentRepository.findPhongHocByMaLHP(lhp.getMaLHP());

            boolean canCancel = false;
            HocKy hk = lhp.getHocKy();
            LocalDate today = LocalDate.now();
            OffsetDateTime nowTime = OffsetDateTime.now();
            // Chỉ được hủy trước khi học kỳ bắt đầu và cổng đăng ký còn mở
            if (Boolean.TRUE.equals(lhp.isEditable()) && hk != null && today.isBefore(hk.getThoiGianBD())) {
                if (lhp.getThoiGianKTDK() == null || nowTime.isBefore(lhp.getThoiGianKTDK())) {
                    canCancel = true;
                }
            } else if (hk != null && today.isBefore(hk.getThoiGianBD())) {
                canCancel = true;
            }

            RegisteredClassResponse.RegisteredClassItem item = new RegisteredClassResponse.RegisteredClassItem(
                    hs.getMaHoSo(),
                    lhp.getMaLHP(),
                    tenHP,
                    lhp.getTenLHP(),
                    stc,
                    lhp.getThu(),
                    lhp.getTietBatDau(),
                    lhp.getSoTiet(),
                    tenGV,
                    phongHoc,
                    lhp.isEditable(),
                    canCancel,
                    hs.getTrangThai() != null ? hs.getTrangThai().name() : "DADANGKY",
                    lhp.getThoiGianKTDK() != null ? lhp.getThoiGianKTDK().toString() : null
            );
            if (hk != null && hk.getMaHocKy() != null) {
                item.setMaHocKy(hk.getMaHocKy());
            }
            items.add(item);
        }

        return new RegisteredClassResponse(items, items.size(), tongTinChi, 24);
    }

    @Override
    public EnrollCourseResponse enrollCourse(String maSV, EnrollCourseRequest request) {
        if (maSV == null || maSV.isBlank()) {
            throw new StudentBusinessException(400, "Mã sinh viên không hợp lệ");
        }
        if (request == null || request.getMaLHP() == null || request.getMaLHP().isBlank()) {
            throw new StudentBusinessException(400, "Mã lớp học phần không được để trống");
        }

        LopHocPhan lhp = studentRepository.findClassByMaLHP(request.getMaLHP().trim())
                .orElseThrow(() -> new StudentBusinessException(404, "Không tìm thấy lớp học phần " + request.getMaLHP()));

        SinhVien sv = studentRepository.findByMaSV(maSV)
                .orElseThrow(() -> new StudentBusinessException(404, "Không tìm thấy hồ sơ sinh viên " + maSV));

        HocKy hk = lhp.getHocKy();
        LocalDate today = LocalDate.now();
        if (hk != null && (today.isAfter(hk.getThoiGianBD()) || today.isEqual(hk.getThoiGianBD()))) {
            throw new StudentBusinessException(400, "Học kỳ " + hk.getMaHocKy() + " đã bắt đầu từ ngày " + hk.getThoiGianBD() + ". Cổng đăng ký học phần đã đóng hoàn toàn theo quy chế đào tạo!");
        }

        if (lhp.getTrangThai() != TrangThaiLop.MODANGKY) {
            throw new StudentBusinessException(400, "Lớp " + lhp.getMaLHP() + " chưa mở đăng ký");
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (lhp.getThoiGianBDDK() != null && now.isBefore(lhp.getThoiGianBDDK())) {
            throw new StudentBusinessException(400, "Đợt đăng ký của lớp " + lhp.getMaLHP() + " chưa bắt đầu");
        }
        if (lhp.getThoiGianKTDK() != null && now.isAfter(lhp.getThoiGianKTDK())) {
            throw new StudentBusinessException(400, "Đợt đăng ký của lớp " + lhp.getMaLHP() + " đã kết thúc");
        }

        int siSoHienTai = lhp.getSiSoHienTai() != null ? lhp.getSiSoHienTai() : 0;
        int siSoToiDa = lhp.getSiSoToiDa() != null ? lhp.getSiSoToiDa() : 0;
        if (siSoHienTai >= siSoToiDa) {
            throw new StudentBusinessException(400, "Lớp " + lhp.getMaLHP() + " đã đủ sĩ số");
        }

        Optional<HoSoHocPhan> existingOpt = studentRepository.findEnrollment(maSV, lhp.getMaLHP());
        if (existingOpt.isPresent() && !existingOpt.get().isDeleted() && !existingOpt.get().isDaHuy()) {
            throw new StudentBusinessException(400, "Sinh viên đã đăng ký lớp học phần này");
        }

        String maHocKy = (lhp.getHocKy() != null && lhp.getHocKy().getMaHocKy() != null)
                ? lhp.getHocKy().getMaHocKy()
                : null;

        List<HoSoHocPhan> currentRegistered = (maHocKy != null && !maHocKy.isBlank())
                ? studentRepository.findRegisteredClassesByHocKy(maSV, maHocKy)
                : studentRepository.findRegisteredClasses(maSV);

        int currentCredits = 0;
        for (HoSoHocPhan hs : currentRegistered) {
            if (!hs.isDaHuy() && hs.getLopHocPhan() != null && hs.getLopHocPhan().getMonHoc() != null) {
                currentCredits += hs.getLopHocPhan().getMonHoc().getSoTinChi() != null ? hs.getLopHocPhan().getMonHoc().getSoTinChi() : 0;
            }
        }
        int newCredits = (lhp.getMonHoc() != null && lhp.getMonHoc().getSoTinChi() != null) ? lhp.getMonHoc().getSoTinChi() : 0;
        if (currentCredits + newCredits > 24) {
            throw new StudentBusinessException(400, "Vượt quá hạn mức tín chỉ tối đa trong học kỳ (24 tín chỉ)");
        }

        if (lhp.getThu() != null && lhp.getTietBatDau() != null && lhp.getSoTiet() != null) {
            int tietBD = lhp.getTietBatDau();
            int tietKT = tietBD + lhp.getSoTiet() - 1;

            List<LopHocPhan> enrolledClasses = new ArrayList<>();
            for (HoSoHocPhan hs : currentRegistered) {
                if (!hs.isDaHuy() && hs.getLopHocPhan() != null) {
                    enrolledClasses.add(hs.getLopHocPhan());
                }
            }

            boolean hasConflict = ScheduleConflictUtil.checkStudentScheduleConflict(maHocKy, lhp.getThu(), tietBD, tietKT, enrolledClasses);
            if (hasConflict) {
                throw new StudentBusinessException(409, "Trùng thời khóa biểu với lớp khác");
            }
        }

        HoSoHocPhan hoSo = existingOpt.orElse(new HoSoHocPhan());
        hoSo.setSinhVien(sv);
        hoSo.setLopHocPhan(lhp);
        hoSo.setNgayDangKy(LocalDateTime.now());
        hoSo.setTrangThai(TrangThaiHocPhan.DADANGKY);
        hoSo.setSoBuoiVang(0);
        hoSo.setDeleted(false);

        HoSoHocPhan saved = studentRepository.saveEnrollment(hoSo);
        lhp.setSiSoHienTai(siSoHienTai + 1);
        studentRepository.updateClass(lhp);

        String trangThaiStr = lhp.getTrangThai() != null ? lhp.getTrangThai().name() : "DANG_MO";
        return new EnrollCourseResponse(saved.getMaHoSo(), lhp.getMaLHP(), trangThaiStr, saved.getNgayDangKy());
    }

    @Override
    public CancelEnrollmentResponse cancelEnrollment(String maSV, Long maHoSo) {
        if (maSV == null || maSV.isBlank()) {
            throw new StudentBusinessException(400, "Mã sinh viên không hợp lệ");
        }
        if (maHoSo == null) {
            throw new StudentBusinessException(400, "Mã hồ sơ không được để trống");
        }

        HoSoHocPhan hoSo = studentRepository.findEnrollmentById(maHoSo)
                .orElseThrow(() -> new StudentBusinessException(404, "Không tìm thấy hồ sơ đăng ký"));

        if (hoSo.getSinhVien() == null || !maSV.equals(hoSo.getSinhVien().getMaSV())) {
            throw new StudentBusinessException(403, "KhĂ´ng cĂ³ quyá»n há»§y há»“ sÆ¡ Ä‘Äƒng kĂ½ cá»§a sinh viĂªn khĂ¡c");
        }

        if (hoSo.isDaHuy()) {
            return new CancelEnrollmentResponse(maHoSo, "DAHUY");
        }

        LopHocPhan cancelLhp = hoSo.getLopHocPhan();
        if (cancelLhp != null) {
            HocKy cancelHk = cancelLhp.getHocKy();
            if (cancelHk != null && (LocalDate.now().isAfter(cancelHk.getThoiGianBD()) || LocalDate.now().isEqual(cancelHk.getThoiGianBD()))) {
                throw new StudentBusinessException(400, "Học kỳ " + cancelHk.getMaHocKy() + " đã bắt đầu từ ngày " + cancelHk.getThoiGianBD() + ". Không được phép hủy học phần theo quy chế đào tạo.");
            }
            if (!cancelLhp.isEditable()) {
                throw new StudentBusinessException(400, "Lớp học phần đã bị khóa, không thể hủy đăng ký");
            }
            if (cancelLhp.getThoiGianKTDK() != null && OffsetDateTime.now().isAfter(cancelLhp.getThoiGianKTDK())) {
                throw new StudentBusinessException(400, "Đã hết thời hạn hủy đăng ký học phần");
            }
        }

        hoSo.setTrangThai(TrangThaiHocPhan.DAHUY);
        studentRepository.deleteEnrollment(hoSo);

        LopHocPhan lhp = hoSo.getLopHocPhan();
        if (lhp != null && lhp.getSiSoHienTai() != null && lhp.getSiSoHienTai() > 0) {
            lhp.setSiSoHienTai(lhp.getSiSoHienTai() - 1);
            studentRepository.updateClass(lhp);
        }

        return new CancelEnrollmentResponse(maHoSo, "DAHUY");
    }

    @Override
    public OpenClassRequestResponse requestOpenCourse(String maSV, OpenClassRequestRequest request) {
        if (maSV == null || maSV.isBlank()) {
            throw new StudentBusinessException(400, "Mã sinh viên không hợp lệ");
        }
        if (request == null || request.getMaHP() == null || request.getMaHP().isBlank()
                || request.getLyDo() == null || request.getLyDo().isBlank()) {
            throw new StudentBusinessException(400, "Vui lòng nhập đầy đủ mã học phần và lý do kiến nghị");
        }

        SinhVien sv = studentRepository.findByMaSV(maSV)
                .orElseThrow(() -> new StudentBusinessException(404, "Không tìm thấy hồ sơ sinh viên " + maSV));

        HocPhan hp = studentRepository.findHocPhanByMaHP(request.getMaHP().trim())
                .orElseThrow(() -> new StudentBusinessException(404, "Không tìm thấy học phần " + request.getMaHP()));

        String maYeuCau = "YC-" + System.currentTimeMillis();
        YeuCauMoLop yc = new YeuCauMoLop();
        yc.setMaYeuCau(maYeuCau);
        yc.setSinhVien(sv);
        yc.setHocPhan(hp);
        yc.setLyDo(request.getLyDo().trim());
        yc.setLichHocMongMuon(request.getLichHocMongMuon() != null ? request.getLichHocMongMuon().trim() : null);
        yc.setTrangThai(TrangThaiYeuCau.DAGUI);
        yc.setNgayTao(LocalDateTime.now());

        YeuCauMoLop saved = studentRepository.saveClassRequest(yc);
        return new OpenClassRequestResponse(saved.getMaYeuCau(), saved.getTrangThai().name());
    }
}

