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
### 3.1. Tổng quan cấu trúc module
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

### 3.2. File mới được bổ sung so với trước khi bắt đầu code và lý do cần thiết:
Trước khi bắt đầu code module Profile, khung dự án ban đầu chỉ có sẵn các file sườn cơ bản (`Controller`, `Service`, `Repository`, `ChangePasswordRequestDTO`, `ChangePasswordResponseDTO`, `UserProfileResponseDTO`).
Trong quá trình triển khai, file mới duy nhất được tạo thêm trong mã nguồn module là:
- **`ProfileBusinessException.java`** (`src/main/java/com/university/modules/profile/ProfileBusinessException.java`)
#### Chi tiết lý do sinh ra `ProfileBusinessException.java`:
1. **Quản lý mã trạng thái HTTP (Status Code) linh hoạt và chính xác:**
   - Exception này kế thừa `RuntimeException` và mang theo thuộc tính `statusCode` (ví dụ: `400 Bad Request`, `401 Unauthorized`, `404 Not Found`) cùng `message` chi tiết.
   - Giúp tầng Service (`ProfileServiceImpl`) khi phát hiện vi phạm nghiệp vụ (như nhập sai mật khẩu cũ, mật khẩu mới dưới 6 ký tự, mật khẩu mới trùng mật khẩu cũ, không tìm thấy người dùng) có thể ném ngoại lệ kèm đúng HTTP status code tương ứng.
   - Tầng Controller (`ProfileController`) bắt ngoại lệ này và đóng gói trực tiếp vào response JSON theo đúng envelope chuẩn `{ status, success: false, message, data: null, meta: null }` mà không cần các cấu trúc `if-else` phức tạp.
2. **Tuân thủ nghiêm ngặt ranh giới phân hệ (Module Boundaries) và quy tắc không sửa `core`:**
   - Trong toàn bộ dự án, mỗi phân hệ đều sở hữu exception nghiệp vụ độc lập riêng (như `StudentBusinessException` ở module Student, `NotificationBusinessException` ở module Notification).
   - Phân hệ dùng chung `core` (`com.university.core.common`) không cung cấp sẵn lớp exception dùng chung mang trường HTTP status code tùy biến. Theo quy định làm việc nhóm, tuyệt đối không chỉnh sửa mã nguồn chung trong `core` để tránh rủi ro xung đột hoặc ảnh hưởng đến các phân hệ khác.
   - Do đó, việc tạo riêng file `ProfileBusinessException.java` nội bộ bên trong `com.university.modules.profile` là giải pháp kiến trúc sạch (Clean Architecture), đảm bảo tính đóng gói độc lập 100% của module.
*(Lưu ý: Để tránh sinh thêm file thừa trong thư mục `responseDTO/`, hai DTO chi tiết `ThongTinHocVuDTO` và `ThongTinCanBoDTO` được đóng gói dưới dạng static inner class ngay bên trong file `UserProfileResponseDTO.java` có sẵn).*
---
