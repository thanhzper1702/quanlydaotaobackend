package com.university.modules.profile;

import com.university.core.model.GiangVien;
import com.university.core.model.HoSoHocPhan;
import com.university.core.model.NguoiDung;
import com.university.core.model.NhanVien;
import com.university.core.model.SinhVien;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.TruongPhongDaoTao;
import com.university.core.repository.GenericRepository;
import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends GenericRepository<NguoiDung, String> {

    Optional<NguoiDung> findUserByCccd(String cccd);

    Optional<NguoiDung> findUserByEmail(String email);

    Optional<SinhVien> findStudentByCccd(String cccd);

    Optional<SinhVien> findStudentByMaSV(String maSV);

    Optional<GiangVien> findLecturerByCccd(String cccd);

    Optional<GiangVien> findLecturerByMaNV(String maNV);

    Optional<TroLyDaoTao> findAssistantByCccd(String cccd);

    Optional<TroLyDaoTao> findAssistantByMaNV(String maNV);

    Optional<TruongPhongDaoTao> findHeadOfficeByCccd(String cccd);

    Optional<TruongPhongDaoTao> findHeadOfficeByMaNV(String maNV);

    Optional<NhanVien> findStaffByCccd(String cccd);

    Optional<NhanVien> findStaffByMaNV(String maNV);

    List<HoSoHocPhan> findStudentGradeRecords(String maSV);

    default Optional<String> findStudentPriority(String maSV) { return Optional.empty(); }

    boolean updatePassword(String cccd, String newHashedPassword);

    boolean updateContactInfo(String cccd, String soDienThoai, String diaChi);
}
