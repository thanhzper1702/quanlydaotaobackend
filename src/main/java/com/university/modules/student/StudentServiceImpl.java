package com.university.modules.student;

import com.university.core.model.*;
import com.university.core.model.enums.*;
import com.university.core.util.ScheduleConflictUtil;
import com.university.modules.student.StudentRequest.*;
import com.university.modules.student.StudentResponse.*;

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
        if (maSV == null || maSV.isBlank()) {
            throw new StudentBusinessException(400, "MĂ£ sinh viĂªn khĂ´ng há»£p lá»‡");
        }

        List<HoSoHocPhan> records = studentRepository.findStudentGrades(maSV);
        if (records == null || records.isEmpty()) {
            return new StudentGradeResponse(Collections.emptyList(), 0.0, 0, "ChÆ°a cĂ³ Ä‘iá»ƒm");
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

                String ketQuaText = "ChÆ°a cĂ³";
                if (hs.getKetQua() != null) {
                    ketQuaText = hs.isDat() ? "Äáº¡t" : "KhĂ´ng Ä‘áº¡t";
                } else if (diemTK != null) {
                    ketQuaText = diemTK >= 4.0 ? "Äáº¡t" : "KhĂ´ng Ä‘áº¡t";
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
        if (thk == null) return "Há»c ká»³ 1";
        switch (thk) {
            case HOCKY1: return "Há»c ká»³ 1";
            case HOCKY2: return "Há»c ká»³ 2";
            case HOCKYHE: return "Há»c ká»³ hĂ¨";
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
        if (gpa4 >= 3.6) return "Xuáº¥t sáº¯c";
        if (gpa4 >= 3.2) return "Giá»i";
        if (gpa4 >= 2.5) return "KhĂ¡";
        if (gpa4 >= 2.0) return "Trung bĂ¬nh";
        if (gpa4 > 0.0) return "Yáº¿u";
        return "ChÆ°a xáº¿p loáº¡i";
    }

    @Override
    public StudentScheduleResponse getSchedule(String maSV, String maHocKy) {
        if (maSV == null || maSV.isBlank()) {
            throw new StudentBusinessException(400, "MĂ£ sinh viĂªn khĂ´ng há»£p lá»‡");
        }

        List<LichHoc> scheduleList = (maHocKy != null && !maHocKy.isBlank())
                ? studentRepository.findStudentSchedule(maSV, maHocKy)
                : Collections.emptyList();

        List<StudentScheduleResponse.ScheduleItemDTO> scheduleItems = new ArrayList<>();
        String currentMaHK = maHocKy;
        String currentTenHK = "";

        if (scheduleList != null && !scheduleList.isEmpty()) {
            for (LichHoc lh : scheduleList) {
                LopHocPhan lhp = lh.getLopHocPhan();
                String tenGV = (lhp != null && lhp.getGiangVien() != null && lhp.getGiangVien().getHoTen() != null)
                        ? lhp.getGiangVien().getHoTen() : "";
                if (currentMaHK == null && lhp != null && lhp.getHocKy() != null) {
                    currentMaHK = lhp.getHocKy().getMaHocKy();
                    currentTenHK = formatTenHocKy(lhp.getHocKy().getTenHocKy());
                }

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
        } else {
            List<HoSoHocPhan> enrollments = (maHocKy != null && !maHocKy.isBlank())
                    ? studentRepository.findRegisteredClassesByHocKy(maSV, maHocKy)
                    : studentRepository.findRegisteredClasses(maSV);

            for (HoSoHocPhan hs : enrollments) {
                LopHocPhan lhp = hs.getLopHocPhan();
                if (lhp == null || lhp.isDeleted()) continue;

                if (currentMaHK == null && lhp.getHocKy() != null) {
                    currentMaHK = lhp.getHocKy().getMaHocKy();
                }
                if (currentTenHK.isEmpty() && lhp.getHocKy() != null) {
                    currentTenHK = formatTenHocKy(lhp.getHocKy().getTenHocKy());
                }

                String tenHP = lhp.getMonHoc() != null ? lhp.getMonHoc().getTenHP() : "";
                String tenGV = lhp.getGiangVien() != null ? (lhp.getGiangVien().getHoTen() != null ? lhp.getGiangVien().getHoTen() : "") : "";
                String phongHoc = studentRepository.findPhongHocByMaLHP(lhp.getMaLHP());

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

        if (currentMaHK == null) currentMaHK = "2025_2026_HK1";
        if (currentTenHK.isEmpty()) currentTenHK = "Há»c ká»³ 1 (2025 - 2026)";

        return new StudentScheduleResponse(scheduleItems, currentMaHK, currentTenHK);
    }

    @Override
    public OpenedEnrollingClass getOpenClasses(String maHP, String keyword, int page, int limit) {
        if (page < 1) page = 1;
        if (limit < 1) limit = 10;

        String effectiveKeyword = keyword;
        if (maHP != null && !maHP.isBlank()) {
            effectiveKeyword = maHP.trim();
        }

        List<LopHocPhan> classes = studentRepository.findOpenClasses(null, effectiveKeyword, page, limit);
        long totalItems = studentRepository.countOpenClasses(null, effectiveKeyword);
        int totalPages = (int) Math.ceil((double) totalItems / limit);
        if (totalPages == 0) totalPages = 1;

        List<OpenedEnrollingClass.OpenClassItem> items = new ArrayList<>();
        for (LopHocPhan lhp : classes) {
            String tenHP = lhp.getMonHoc() != null ? lhp.getMonHoc().getTenHP() : "";
            String maHocPhan = lhp.getMonHoc() != null ? lhp.getMonHoc().getMaHP() : "";
            int soTinChi = lhp.getMonHoc() != null && lhp.getMonHoc().getSoTinChi() != null ? lhp.getMonHoc().getSoTinChi() : 0;
            String tenGV = lhp.getGiangVien() != null ? (lhp.getGiangVien().getHoTen() != null ? lhp.getGiangVien().getHoTen() : "") : "";
            String trangThaiStr = lhp.getTrangThai() != null ? lhp.getTrangThai().name() : "DANG_MO";
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
        if (maSV == null || maSV.isBlank()) {
            throw new StudentBusinessException(400, "MĂ£ sinh viĂªn khĂ´ng há»£p lá»‡");
        }

        List<HoSoHocPhan> list = studentRepository.findRegisteredClasses(maSV);
        List<RegisteredClassResponse.RegisteredClassItem> items = new ArrayList<>();
        int tongTinChi = 0;

        for (HoSoHocPhan hs : list) {
            LopHocPhan lhp = hs.getLopHocPhan();
            if (lhp == null || lhp.isDeleted()) continue;

            int stc = (lhp.getMonHoc() != null && lhp.getMonHoc().getSoTinChi() != null) ? lhp.getMonHoc().getSoTinChi() : 0;
            tongTinChi += stc;
            String tenHP = lhp.getMonHoc() != null ? lhp.getMonHoc().getTenHP() : "";
            String tenGV = lhp.getGiangVien() != null ? (lhp.getGiangVien().getHoTen() != null ? lhp.getGiangVien().getHoTen() : "") : "";
            String phongHoc = studentRepository.findPhongHocByMaLHP(lhp.getMaLHP());

            items.add(new RegisteredClassResponse.RegisteredClassItem(
                    hs.getMaHoSo(),
                    lhp.getMaLHP(),
                    tenHP,
                    lhp.getTenLHP(),
                    stc,
                    lhp.getThu(),
                    lhp.getTietBatDau(),
                    lhp.getSoTiet(),
                    tenGV,
                    phongHoc
            ));
        }

        return new RegisteredClassResponse(items, items.size(), tongTinChi, 24);
    }

    @Override
    public EnrollCourseResponse enrollCourse(String maSV, EnrollCourseRequest request) {
        if (maSV == null || maSV.isBlank()) {
            throw new StudentBusinessException(400, "MĂ£ sinh viĂªn khĂ´ng há»£p lá»‡");
        }
        if (request == null || request.getMaLHP() == null || request.getMaLHP().isBlank()) {
            throw new StudentBusinessException(400, "MĂ£ lá»›p há»c pháº§n khĂ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");
        }

        LopHocPhan lhp = studentRepository.findClassByMaLHP(request.getMaLHP().trim())
                .orElseThrow(() -> new StudentBusinessException(404, "KhĂ´ng tĂ¬m tháº¥y lá»›p há»c pháº§n " + request.getMaLHP()));

        SinhVien sv = studentRepository.findByMaSV(maSV)
                .orElseThrow(() -> new StudentBusinessException(404, "KhĂ´ng tĂ¬m tháº¥y há»“ sÆ¡ sinh viĂªn " + maSV));

        if (lhp.getTrangThai() != TrangThaiLop.MODANGKY) {
            throw new StudentBusinessException(400, "Lá»›p Ä‘Ă£ Ä‘áº§y sÄ© sá»‘ hoáº·c chÆ°a tá»›i giá» má»Ÿ cá»•ng");
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (lhp.getThoiGianBDDK() != null && now.isBefore(lhp.getThoiGianBDDK())) {
            throw new StudentBusinessException(400, "Lá»›p Ä‘Ă£ Ä‘áº§y sÄ© sá»‘ hoáº·c chÆ°a tá»›i giá» má»Ÿ cá»•ng");
        }
        if (lhp.getThoiGianKTDK() != null && now.isAfter(lhp.getThoiGianKTDK())) {
            throw new StudentBusinessException(400, "Lá»›p Ä‘Ă£ Ä‘áº§y sÄ© sá»‘ hoáº·c chÆ°a tá»›i giá» má»Ÿ cá»•ng");
        }

        int siSoHienTai = lhp.getSiSoHienTai() != null ? lhp.getSiSoHienTai() : 0;
        int siSoToiDa = lhp.getSiSoToiDa() != null ? lhp.getSiSoToiDa() : 0;
        if (siSoHienTai >= siSoToiDa) {
            throw new StudentBusinessException(400, "Lá»›p Ä‘Ă£ Ä‘áº§y sÄ© sá»‘ hoáº·c chÆ°a tá»›i giá» má»Ÿ cá»•ng");
        }

        Optional<HoSoHocPhan> existingOpt = studentRepository.findEnrollment(maSV, lhp.getMaLHP());
        if (existingOpt.isPresent() && !existingOpt.get().isDeleted() && !existingOpt.get().isDaHuy()) {
            throw new StudentBusinessException(400, "Sinh viĂªn Ä‘Ă£ Ä‘Äƒng kĂ½ lá»›p há»c pháº§n nĂ y");
        }

        List<HoSoHocPhan> currentRegistered = studentRepository.findRegisteredClasses(maSV);
        int currentCredits = 0;
        for (HoSoHocPhan hs : currentRegistered) {
            if (!hs.isDaHuy() && hs.getLopHocPhan() != null && hs.getLopHocPhan().getMonHoc() != null) {
                currentCredits += hs.getLopHocPhan().getMonHoc().getSoTinChi() != null ? hs.getLopHocPhan().getMonHoc().getSoTinChi() : 0;
            }
        }
        int newCredits = (lhp.getMonHoc() != null && lhp.getMonHoc().getSoTinChi() != null) ? lhp.getMonHoc().getSoTinChi() : 0;
        if (currentCredits + newCredits > 24) {
            throw new StudentBusinessException(400, "VÆ°á»£t quĂ¡ háº¡n má»©c tĂ­n chá»‰ tá»‘i Ä‘a trong há»c ká»³ (24 tĂ­n chá»‰)");
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

            boolean hasConflict = ScheduleConflictUtil.checkStudentScheduleConflict(lhp.getThu(), tietBD, tietKT, enrolledClasses);
            if (hasConflict) {
                throw new StudentBusinessException(409, "TrĂ¹ng thá»i khĂ³a biá»ƒu vá»›i lá»›p khĂ¡c");
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
            throw new StudentBusinessException(400, "MĂ£ sinh viĂªn khĂ´ng há»£p lá»‡");
        }
        if (maHoSo == null) {
            throw new StudentBusinessException(400, "MĂ£ há»“ sÆ¡ khĂ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");
        }

        HoSoHocPhan hoSo = studentRepository.findEnrollmentById(maHoSo)
                .orElseThrow(() -> new StudentBusinessException(404, "KhĂ´ng tĂ¬m tháº¥y há»“ sÆ¡ Ä‘Äƒng kĂ½"));

        if (hoSo.getSinhVien() == null || !maSV.equals(hoSo.getSinhVien().getMaSV())) {
            throw new StudentBusinessException(403, "KhĂ´ng cĂ³ quyá»n há»§y há»“ sÆ¡ Ä‘Äƒng kĂ½ cá»§a sinh viĂªn khĂ¡c");
        }

        if (hoSo.isDaHuy()) {
            return new CancelEnrollmentResponse(maHoSo, "DAHUY");
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
            throw new StudentBusinessException(400, "MĂ£ sinh viĂªn khĂ´ng há»£p lá»‡");
        }
        if (request == null || request.getMaHP() == null || request.getMaHP().isBlank()
                || request.getLyDo() == null || request.getLyDo().isBlank()) {
            throw new StudentBusinessException(400, "Vui lĂ²ng nháº­p Ä‘áº§y Ä‘á»§ mĂ£ há»c pháº§n vĂ  lĂ½ do kiáº¿n nghá»‹");
        }

        SinhVien sv = studentRepository.findByMaSV(maSV)
                .orElseThrow(() -> new StudentBusinessException(404, "KhĂ´ng tĂ¬m tháº¥y há»“ sÆ¡ sinh viĂªn " + maSV));

        HocPhan hp = studentRepository.findHocPhanByMaHP(request.getMaHP().trim())
                .orElseThrow(() -> new StudentBusinessException(404, "KhĂ´ng tĂ¬m tháº¥y há»c pháº§n " + request.getMaHP()));

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

