package com.university.modules.profile;

import com.university.core.model.GiangVien;
import com.university.core.model.NguoiDung;
import com.university.core.model.NhanVien;
import com.university.core.model.SinhVien;
import com.university.core.repository.GenericRepository;
import java.util.Optional;

public interface ProfileRepository extends GenericRepository<NguoiDung, String> {
    Optional<NguoiDung> findUserByCccd(String cccd);
    Optional<SinhVien> findStudentByCccd(String cccd);
    Optional<GiangVien> findLecturerByCccd(String cccd);
    Optional<NhanVien> findStaffByCccd(String cccd);
    boolean updateContactInfo(String cccd, String soDienThoai, String diaChi);
}
