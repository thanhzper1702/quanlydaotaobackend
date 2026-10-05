package com.university.modules.auth;

import com.university.core.model.NguoiDung;
import com.university.core.repository.GenericRepository;
import java.util.Optional;

public interface AuthRepository extends GenericRepository<NguoiDung, String> {
    Optional<NguoiDung> findByEmail(String email);
    Optional<NguoiDung> findByCccd(String cccd);
    boolean updatePassword(String cccd, String newHashedPassword);
}
