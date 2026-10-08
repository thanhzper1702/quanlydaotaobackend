# BÁO CÁO HOÀN THÀNH BACKEND PHÂN HỆ PROFILE (PROFILE MODULE)

- **Phân hệ:** Hồ sơ cá nhân & Đổi mật khẩu (Profile Module)
- **Nhánh triển khai:** `feature/profile`
- **Các API phụ trách:** Route 05 và Route 06 (theo đặc tả `APIRoute.md`)
- **Ngày hoàn thành:** 08/10/2026
- **Trạng thái:** Hoàn thành 100% (Đã kiểm thử đạt 11/11 test cases)

---

## 1. TỔNG QUAN CÔNG VIỆC ĐÃ HOÀN THÀNH

1. **Hiện thực đầy đủ 2/2 API Routes phân hệ Profile:**
   - **Route 05 (`GET /api/profile/me`):** Xem thông tin cá nhân của người dùng hiện tại, tự động điều chỉnh linh hoạt theo 4 vai trò (`SINHVIEN`, `GIANGVIEN`, `TROLY`, `TRUONGPHONG`).
   - **Route 06 (`PUT /api/profile/change-password`):** Đổi mật khẩu tài khoản bảo mật bằng BCrypt với đầy đủ các điều kiện xác thực và validation.
2. **Tuân thủ 100% các nguyên tắc nhóm:**
   - Hoàn thành độc lập trong phạm vi gói `com.university.modules.profile`.
   - Tuyệt đối không thay đổi mã nguồn ngoài phạm vi được phân công.
   - Kế thừa `BaseController` để lấy thông tin xác thực (`getAuthenticatedUserCccd()`, `getAuthenticatedUserCode()`, `getAuthenticatedRole()`).
   - Sử dụng `ApiResponse` để đóng gói dữ liệu trả về theo đúng JSON envelope chuẩn: `{ "status", "success", "message", "data", "meta" }`.
   - Sử dụng `PasswordUtil` để hash và verify mật khẩu bằng thuật toán BCrypt.
3. **Cấu trúc dữ liệu & Xử lý nghiệp vụ:**
   - **Tính toán học vụ sinh viên:** Tự động truy vấn lịch sử học phần từ `HoSoHocPhan`, tính toán chính xác tổng số tín chỉ tích lũy (`soTinChiTichLuy`) và điểm trung bình tích lũy hệ 4 (`gpaHienTai`).
   - **Phân hóa thông tin cán bộ:** Cung cấp thông tin nghiệp vụ chi tiết cho từng loại cán bộ (học vị, khoa cho Giảng viên; ngày bắt đầu cho Trợ lý; nhiệm kỳ cho Trưởng phòng).
   - **Ẩn trường null:** Sử dụng `@JsonInclude(JsonInclude.Include.NON_NULL)` giúp payload JSON của sinh viên sạch sẽ, không chứa các trường của cán bộ và ngược lại.
   - **Validation đổi mật khẩu chặt chẽ:** Kiểm tra mật khẩu cũ chính xác, mật khẩu mới tối thiểu 6 ký tự, mật khẩu mới không trùng mật khẩu cũ.
4. **Kiểm thử tự động:**
   - Xây dựng bộ test suite `ProfileServiceAndControllerTest` với **11/11 test cases PASSED (100%)**.

---

## 2. BẢNG CHI TIẾT CÁC API ROUTE ĐÃ TRIỂN KHAI

| Route | Phương thức & Endpoint | Tên chức năng | Xác thực | Payload / Param | Phản hồi chính | Mã HTTP |
| :---: | :--- | :--- | :---: | :--- | :--- | :---: |
| **05** | `GET /profile/me` | Xem thông tin cá nhân người dùng hiện tại | Bearer Token | Không | `UserProfileResponseDTO` | `200 OK` / `401` |
| **06** | `PUT /profile/change-password` | Đổi mật khẩu tài khoản | Bearer Token | `ChangePasswordRequestDTO` | `data: null` | `200 OK` / `400` / `401` |

---

## 3. CẤU TRÚC MÃ NGUỒN MODULE PROFILE

```text
com.university.modules.profile/
├── ProfileController.java          # REST API Controller kế thừa BaseController
├── ProfileService.java             # Interface nghiệp vụ profile & đổi mật khẩu
├── ProfileServiceImpl.java         # Hiện thực logic nghiệp vụ, tính GPA, đổi pass
├── ProfileRepository.java          # Interface truy vấn dữ liệu JPA
├── ProfileRepositoryImpl.java      # Hiện thực truy vấn PostgreSQL
├── ProfileBusinessException.java   # Exception chuẩn hóa kèm mã HTTP status code
├── requestDTO/
│   └── ChangePasswordRequestDTO.java  # DTO nhận matKhauCu, matKhauMoi
└── responseDTO/
    ├── ChangePasswordResponseDTO.java # DTO kết quả đổi mật khẩu
    └── UserProfileResponseDTO.java    # DTO hồ sơ cá nhân linh hoạt cho 4 roles
```

---

## 4. KẾT QUẢ KIỂM THỬ TỰ ĐỘNG (UNIT TESTS)

Bộ kiểm thử `ProfileServiceAndControllerTest` đạt tỷ lệ **11/11 PASSED (100%)**:

1. `testGetStudentProfileSuccess`: Lấy hồ sơ sinh viên kèm tính toán GPA hệ 4 (`3.43`) và số tín chỉ tích lũy (`7`) đạt chuẩn.
2. `testGetLecturerProfileSuccess`: Lấy hồ sơ giảng viên hiển thị học vị ("Tiến sĩ") và khoa ("CNTT 1").
3. `testGetAssistantProfileSuccess`: Lấy hồ sơ trợ lý hiển thị ngày bắt đầu làm việc.
4. `testGetHeadOfficeProfileSuccess`: Lấy hồ sơ trưởng phòng hiển thị nhiệm kỳ ("2021-2026").
5. `testChangePasswordSuccess`: Đổi mật khẩu thành công, kiểm tra mật khẩu đã được mã hóa BCrypt trong CSDL.
6. `testChangePasswordWrongOldPassword`: Báo lỗi HTTP `400` khi nhập sai mật khẩu cũ.
7. `testChangePasswordEmptyFields`: Báo lỗi HTTP `400` khi bỏ trống mật khẩu cũ hoặc mới.
8. `testChangePasswordTooShort`: Báo lỗi HTTP `400` khi mật khẩu mới dưới 6 ký tự.
9. `testChangePasswordSameAsOld`: Báo lỗi HTTP `400` khi mật khẩu mới trùng mật khẩu cũ.
10. `testUnauthenticatedAccess`: Báo lỗi HTTP `401 Unauthorized` khi người dùng chưa đăng nhập.
11. `testJsonPayloadMatchesApiRouteContract`: Xác nhận chuỗi JSON Response chuẩn 100% với hợp đồng trong `APIRoute.md`.
