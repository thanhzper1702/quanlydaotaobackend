package com.university.modules.auth.responseDTO;

public class AdminLoginResponseDTO {

    private String accessToken;
    private AdminUserDTO user;

    public AdminLoginResponseDTO() {
    }

    public AdminLoginResponseDTO(String accessToken, AdminUserDTO user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public AdminUserDTO getUser() {
        return user;
    }

    public void setUser(AdminUserDTO user) {
        this.user = user;
    }

    public static class AdminUserDTO {
        private String maNV;
        private String hoTen;
        private String role;
        private String anhThe;

        public AdminUserDTO() {
        }

        public AdminUserDTO(String maNV, String hoTen, String role, String anhThe) {
            this.maNV = maNV;
            this.hoTen = hoTen;
            this.role = role;
            this.anhThe = anhThe;
        }

        public String getMaNV() {
            return maNV;
        }

        public void setMaNV(String maNV) {
            this.maNV = maNV;
        }

        public String getHoTen() {
            return hoTen;
        }

        public void setHoTen(String hoTen) {
            this.hoTen = hoTen;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getAnhThe() {
            return anhThe;
        }

        public void setAnhThe(String anhThe) {
            this.anhThe = anhThe;
        }
    }
}
