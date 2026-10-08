package com.university.modules.profile;

import com.university.modules.profile.requestDTO.ChangePasswordRequestDTO;
import com.university.modules.profile.responseDTO.ChangePasswordResponseDTO;
import com.university.modules.profile.responseDTO.UserProfileResponseDTO;

public interface ProfileService {

    UserProfileResponseDTO getProfile(String cccd, String userCode, String role);

    UserProfileResponseDTO getProfile(String userCode);

    void changePassword(String cccd, ChangePasswordRequestDTO request);

    ChangePasswordResponseDTO changePassword(String userCode, ChangePasswordRequestDTO request, boolean byUserCode);
}
