package com.university.modules.auth;

import com.university.core.model.GiangVien;
import com.university.core.model.NguoiDung;
import com.university.core.model.NhanVien;
import com.university.core.model.SinhVien;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.TruongPhongDaoTao;
import com.university.core.repository.GenericRepository;

import java.util.Optional;

public interface AuthRepository extends GenericRepository<NguoiDung, String> {

    Optional<NguoiDung> findByEmail(String email);

    Optional<NguoiDung> findByCccd(String cccd);

    Optional<NguoiDung> findByRefreshToken(String refreshToken);

    boolean updateRefreshToken(String cccd, String refreshToken);

    boolean clearRefreshToken(String cccd);

    boolean updatePassword(String cccd, String newHashedPassword);

    Optional<SinhVien> findStudentByCccd(String cccd);

    Optional<NhanVien> findStaffByCccd(String cccd);

    Optional<GiangVien> findLecturerByCccd(String cccd);

    Optional<TroLyDaoTao> findAssistantByCccd(String cccd);

    Optional<TruongPhongDaoTao> findHeadOfficeByCccd(String cccd);
}
