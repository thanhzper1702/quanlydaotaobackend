package com.university.modules.profile;
import com.university.modules.profile.requestDTO.*;
import com.university.modules.profile.responseDTO.*;


public interface ProfileService {

    UserProfileResponseDTO getProfile(String userCode);

    ChangePasswordResponseDTO changePassword(String userCode, ChangePasswordRequestDTO request);
}
