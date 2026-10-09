package com.university.modules.auth;

/**
 * Gom nhóm toàn bộ Response DTO của phân hệ Auth.
 */
public final class AuthResponseDTO {

    private AuthResponseDTO() {
    }

    public static class LoginResponseDTO {
        private String accessToken;
        private StudentUserDTO user;

        public LoginResponseDTO() {
        }

        public LoginResponseDTO(String accessToken, StudentUserDTO user) {
            this.accessToken = accessToken;
            this.user = user;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public StudentUserDTO getUser() {
            return user;
        }

        public void setUser(StudentUserDTO user) {
            this.user = user;
        }

        public static class StudentUserDTO {
            private String maSV;
            private String hoTen;
            private String role;
            private String anhThe;

            public StudentUserDTO() {
            }

            public StudentUserDTO(String maSV, String hoTen, String role, String anhThe) {
                this.maSV = maSV;
                this.hoTen = hoTen;
                this.role = role;
                this.anhThe = anhThe;
            }

            public String getMaSV() {
                return maSV;
            }

            public void setMaSV(String maSV) {
                this.maSV = maSV;
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

    public static class AdminLoginResponseDTO {
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

    public static class LogoutResponseDTO {
        public LogoutResponseDTO() {
        }
    }

    public static class RefreshTokenResponseDTO {
        private String accessToken;

        public RefreshTokenResponseDTO() {
        }

        public RefreshTokenResponseDTO(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
