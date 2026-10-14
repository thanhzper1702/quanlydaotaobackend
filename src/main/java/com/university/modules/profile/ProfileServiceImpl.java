package com.university.modules.profile;

import com.university.core.model.GiangVien;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.NguoiDung;
import com.university.core.model.NhanVien;
import com.university.core.model.SinhVien;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.TruongPhongDaoTao;
import com.university.core.util.PasswordUtil;
import com.university.modules.profile.ProfileRequestDTO.ChangePasswordRequestDTO;
import com.university.modules.profile.ProfileResponseDTO.ChangePasswordResponseDTO;
import com.university.modules.profile.ProfileResponseDTO.UserProfileResponseDTO;
import com.university.modules.profile.ProfileResponseDTO.UserProfileResponseDTO.ThongTinCanBoDTO;
import com.university.modules.profile.ProfileResponseDTO.UserProfileResponseDTO.ThongTinHocVuDTO;

import java.util.List;
import java.util.Optional;

public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl() {
        this.profileRepository = new ProfileRepositoryImpl();
    }

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public UserProfileResponseDTO getProfile(String cccd, String userCode, String role) {
        NguoiDung user = null;

        // 1. Tìm theo CCCD nếu có
        if (cccd != null && !cccd.isBlank()) {
            user = profileRepository.findUserByCccd(cccd).orElse(null);
        }

        // 2. Nếu chưa tìm thấy và có userCode, tìm qua SinhVien hoặc NhanVien
        if (user == null && userCode != null && !userCode.isBlank()) {
            Optional<SinhVien> svOpt = profileRepository.findStudentByMaSV(userCode);
            if (svOpt.isPresent()) {
                user = svOpt.get().getNguoiDung();
                if (role == null || role.isBlank()) {
                    role = "SINHVIEN";
                }
            } else {
                Optional<NhanVien> nvOpt = profileRepository.findStaffByMaNV(userCode);
                if (nvOpt.isPresent()) {
                    user = nvOpt.get().getNguoiDung();
                }
            }
        }

        if (user == null) {
            throw new ProfileBusinessException(404, "Không tìm thấy thông tin người dùng");
        }

        // 3. Tự động nhận diện vai trò nếu role chưa có
        if (role == null || role.isBlank()) {
            if (profileRepository.findStudentByCccd(user.getCccd()).isPresent()) {
                role = "SINHVIEN";
            } else if (profileRepository.findLecturerByCccd(user.getCccd()).isPresent()) {
                role = "GIANGVIEN";
            } else if (profileRepository.findAssistantByCccd(user.getCccd()).isPresent()) {
                role = "TROLY";
            } else if (profileRepository.findHeadOfficeByCccd(user.getCccd()).isPresent()) {
                role = "TRUONGPHONG";
            } else {
                role = "NHANVIEN";
            }
        }

        // 4. Xác định mã định danh hiển thị (Mã SV hoặc Mã NV)
        String maDinhDanh = userCode;
        if (maDinhDanh == null || maDinhDanh.isBlank()) {
            if ("SINHVIEN".equalsIgnoreCase(role)) {
                maDinhDanh = profileRepository.findStudentByCccd(user.getCccd())
                        .map(SinhVien::getMaSV)
                        .orElse(user.getCccd());
            } else {
                maDinhDanh = profileRepository.findStaffByCccd(user.getCccd())
                        .map(NhanVien::getMaNV)
                        .orElse(user.getCccd());
            }
        }

        String hoTen = user.getHoTen();
        String ngaySinh = user.getNgaySinh() != null ? user.getNgaySinh().toString() : null;
        String gioiTinh = user.getGioiTinh();
        String email = user.getEmail();
        String soDienThoai = user.getSoDienThoai();
        String diaChi = user.getDiaChi();
        String anhThe = user.getAnhThe();

        final String effectiveMaDinhDanh = maDinhDanh;

        ThongTinHocVuDTO thongTinHocVu = null;
        ThongTinCanBoDTO thongTinCanBo = null;

        // 5. Bổ sung thông tin nghiệp vụ theo vai trò
        if ("SINHVIEN".equalsIgnoreCase(role)) {
            SinhVien sv = profileRepository.findStudentByCccd(user.getCccd())
                    .or(() -> profileRepository.findStudentByMaSV(effectiveMaDinhDanh))
                    .orElse(null);

            if (sv != null) {
                List<HoSoHocPhan> records = profileRepository.findStudentGradeRecords(sv.getMaSV());
                int totalPassedCredits = 0;
                double totalWeightedGpa = 0.0;
                int totalCreditsAll = 0;

                for (HoSoHocPhan hs : records) {
                    if (hs.getDiemTongKet() != null && hs.getLopHocPhan() != null && hs.getLopHocPhan().getMonHoc() != null) {
                        int cr = hs.getLopHocPhan().getMonHoc().getSoTinChi();
                        double diem10 = hs.getDiemTongKet().doubleValue();
                        if (cr > 0) {
                            double gpa4 = convertToGpa4(diem10);
                            totalWeightedGpa += gpa4 * cr;
                            totalCreditsAll += cr;
                            if (diem10 >= 4.0) {
                                totalPassedCredits += cr;
                            }
                        }
                    }
                }

                double gpaHienTai = totalCreditsAll > 0
                        ? Math.round((totalWeightedGpa / totalCreditsAll) * 100.0) / 100.0
                        : 0.0;

                String dienUuTien = profileRepository.findStudentPriority(sv.getMaSV()).orElse("Không (Bình thường)");
                String lopSinhHoat = "221101A";
                if (sv.getChuyenNganh() != null && sv.getNienKhoa() != null) {
                    String prefix = sv.getChuyenNganh().contains("Kỹ thuật phần mềm") ? "KTPM"
                            : (sv.getChuyenNganh().contains("Khoa học máy tính") ? "KHMT" : "CNTT");
                    String year = sv.getNienKhoa().split("-")[0].trim();
                    lopSinhHoat = prefix + year;
                }

                thongTinHocVu = new ThongTinHocVuDTO(
                        sv.getNienKhoa(),
                        sv.getKhoa(),
                        sv.getChuyenNganh(),
                        totalPassedCredits,
                        gpaHienTai,
                        lopSinhHoat,
                        dienUuTien,
                        "Đang học"
                );
            }
        } else if ("GIANGVIEN".equalsIgnoreCase(role)) {
            GiangVien gv = profileRepository.findLecturerByCccd(user.getCccd())
                    .or(() -> profileRepository.findLecturerByMaNV(effectiveMaDinhDanh))
                    .orElse(null);
            if (gv != null) {
                thongTinCanBo = new ThongTinCanBoDTO(
                        gv.getHocVi(),
                        gv.getKhoa(),
                        gv.getNgayBatDau() != null ? gv.getNgayBatDau().toString() : null,
                        null,
                        "Công nghệ phần mềm",
                        "Kiến trúc phần mềm, Thiết kế giao diện Web"
                );
            }
        } else if ("TROLY".equalsIgnoreCase(role)) {
            TroLyDaoTao tl = profileRepository.findAssistantByCccd(user.getCccd())
                    .or(() -> profileRepository.findAssistantByMaNV(effectiveMaDinhDanh))
                    .orElse(null);
            if (tl != null) {
                thongTinCanBo = new ThongTinCanBoDTO(
                        null,
                        null,
                        tl.getNgayBatDau() != null ? tl.getNgayBatDau().toString() : null,
                        null
                );
            }
        } else if ("TRUONGPHONG".equalsIgnoreCase(role)) {
            TruongPhongDaoTao tp = profileRepository.findHeadOfficeByCccd(user.getCccd())
                    .or(() -> profileRepository.findHeadOfficeByMaNV(effectiveMaDinhDanh))
                    .orElse(null);
            if (tp != null) {
                thongTinCanBo = new ThongTinCanBoDTO(
                        null,
                        null,
                        null,
                        tp.getNhiemKy()
                );
            }
        }

        return new UserProfileResponseDTO(
                user.getCccd(),
                maDinhDanh,
                hoTen,
                ngaySinh,
                gioiTinh,
                email,
                soDienThoai,
                diaChi,
                role,
                anhThe,
                thongTinHocVu,
                thongTinCanBo
        );
    }

    @Override
    public UserProfileResponseDTO getProfile(String userCode) {
        return getProfile(null, userCode, null);
    }

    @Override
    public void changePassword(String cccd, ChangePasswordRequestDTO request) {
        if (request == null || request.getMatKhauCu() == null || request.getMatKhauCu().isBlank()
                || request.getMatKhauMoi() == null || request.getMatKhauMoi().isBlank()) {
            throw new ProfileBusinessException(400, "Vui lòng nhập đầy đủ mật khẩu cũ và mật khẩu mới");
        }

        String matKhauCu = request.getMatKhauCu().trim();
        String matKhauMoi = request.getMatKhauMoi().trim();

        if (matKhauMoi.length() < 6) {
            throw new ProfileBusinessException(400, "Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        if (matKhauCu.equals(matKhauMoi)) {
            throw new ProfileBusinessException(400, "Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        // Tìm NguoiDung theo CCCD hoặc userCode
        NguoiDung user = profileRepository.findUserByCccd(cccd)
                .or(() -> profileRepository.findStudentByMaSV(cccd).map(SinhVien::getNguoiDung))
                .or(() -> profileRepository.findStaffByMaNV(cccd).map(NhanVien::getNguoiDung))
                .orElseThrow(() -> new ProfileBusinessException(404, "Không tìm thấy thông tin người dùng"));

        // Xác thực mật khẩu cũ
        if (!PasswordUtil.verify(matKhauCu, user.getMatKhauHashed())) {
            throw new ProfileBusinessException(400, "Mật khẩu cũ không đúng");
        }

        // Băm mật khẩu mới bằng BCrypt
        String newHashedPassword = PasswordUtil.hash(matKhauMoi);
        boolean updated = profileRepository.updatePassword(user.getCccd(), newHashedPassword);
        if (!updated) {
            throw new ProfileBusinessException(500, "Cập nhật mật khẩu thất bại");
        }
        user.setRefreshToken(null);
    }

    @Override
    public ChangePasswordResponseDTO changePassword(String userCode, ChangePasswordRequestDTO request, boolean byUserCode) {
        changePassword(userCode, request);
        return new ChangePasswordResponseDTO("Đổi mật khẩu thành công");
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
}
