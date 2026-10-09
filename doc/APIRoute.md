# TÀI LIỆU ĐẶC TẢ CHI TIẾT CÁC API ROUTE (OpenAPI 3.0)
## Hệ thống Quản lý Đào tạo & Đăng ký Học phần (Nhóm 8)

> [!NOTE]
> **Thông tin phiên bản & Cấu hình môi trường:**
> - **Phiên bản API:** `1.0.0`
> - **Tổng số API Endpoints:** `43 routes` phân chia theo `8 phân hệ chức năng`.
> - **Quy chuẩn Response Envelope:** Tất cả API tuân theo cấu trúc chuẩn: `{ status, message, data, [meta] }`
> - **SwaggerHub Auto Mocking:** `https://virtserver.swaggerhub.com/student-6c9-5f3/QuanLyDaoTao/1.0.0`
> - **Development Server (Local):** `http://localhost:8080/api/v1`

## MỤC LỤC DANH SÁCH API ROUTES

### 1. Xác thực & Tài khoản (Auth)
*Đăng nhập, phân quyền, cấp token và phiên làm việc*

- **01.** [POST `/auth/login`](#api-1-post-auth-login) : Đăng nhập dành cho Sinh viên (Cổng /login)
- **02.** [POST `/auth/admin/login`](#api-2-post-auth-admin-login) : Đăng nhập dành cho Cán bộ / Quản trị viên (Cổng /admin/login)
- **03.** [POST `/auth/logout`](#api-3-post-auth-logout) : Đăng xuất người dùng
- **04.** [POST `/auth/refresh-token`](#api-4-post-auth-refresh-token) : Cấp mới Access Token bằng Refresh Token

### 2. Hồ sơ cá nhân (Profile)
*Thông tin người dùng hiện tại và đổi mật khẩu*

- **05.** [GET `/profile/me`](#api-5-get-profile-me) : Xem thông tin cá nhân của người dùng hiện tại
- **06.** [PUT `/profile/change-password`](#api-6-put-profile-change-password) : Đổi mật khẩu tài khoản

### 3. Thông báo học vụ (Notifications)
*Xem và phát hành thông báo cấp trường / theo lớp*

- **07.** [GET `/notifications`](#api-7-get-notifications) : Lấy danh sách thông báo chung và theo lớp (Có meta phân trang)
- **08.** [POST `/notifications`](#api-8-post-notifications) : Tạo và phát hành thông báo học vụ mới
- **09.** [PUT `/notifications/{id}`](#api-9-put-notifications-id) : Cập nhật nội dung thông báo
- **10.** [DELETE `/notifications/{id}`](#api-10-delete-notifications-id) : Xóa mềm thông báo học vụ

### 4. Phân hệ Sinh viên - Điểm & TKB (Student Transcript & Schedule)
*Tra cứu điểm tích lũy và ma trận thời khóa biểu tuần*

- **11.** [GET `/student/grades`](#api-11-get-student-grades) : Xem bảng điểm chi tiết theo từng học kỳ (Có meta thống kê)
- **12.** [GET `/student/schedule`](#api-12-get-student-schedule) : Lấy thời khóa biểu sinh viên dạng ma trận tuần (Có meta học kỳ)

### 5. Phân hệ Sinh viên - Đăng ký học phần (Student Registration)
*Tra cứu lớp mở, đăng ký / hủy lớp và gửi yêu cầu mở lớp*

- **13.** [GET `/student/registration/open-classes`](#api-13-get-student-registration-open-classes) : Lấy danh sách lớp học phần đang mở đăng ký (Có meta phân trang)
- **14.** [GET `/student/registration/registered-classes`](#api-14-get-student-registration-registered-classes) : Lấy danh sách lớp học phần sinh viên đã ghi danh thành công
- **15.** [POST `/student/registration/enroll`](#api-15-post-student-registration-enroll) : Đăng ký một lớp học phần mới (Kiểm tra xung đột & Sĩ số)
- **16.** [DELETE `/student/registration/cancel/{maHoSo}`](#api-16-delete-student-registration-cancel-maHoSo) : Hủy đăng ký một lớp học phần
- **17.** [POST `/student/registration/open-request`](#api-17-post-student-registration-open-request) : Gửi phiếu kiến nghị mở thêm lớp học phần

### 6. Phân hệ Giảng viên (Lecturer)
*Xem lớp dạy, nhập điểm, xuất file và điểm danh buổi học*

- **18.** [GET `/lecturer/grades`](#api-18-get-lecturer-grades) : Lấy danh sách các lớp học phần được phân công giảng dạy (Có meta)
- **19.** [GET `/lecturer/grades/{maLHP}`](#api-19-get-lecturer-grades-maLHP) : Lấy bảng điểm sinh viên của một lớp học phần (Có meta thống kê)
- **20.** [PUT `/lecturer/grades/{maLHP}`](#api-20-put-lecturer-grades-maLHP) : Cập nhật / Lưu điểm quá trình và cuối kỳ (Trigger tự động tính tổng kết)
- **21.** [GET `/lecturer/grades/{maLHP}/export`](#api-21-get-lecturer-grades-maLHP-export) : Xuất file Excel bảng điểm của lớp học phần
- **22.** [GET `/lecturer/teaching-schedule`](#api-22-get-lecturer-teaching-schedule) : Lấy lịch giảng dạy tuần thực tế theo học kỳ (Có meta)
- **23.** [GET `/lecturer/attendance/{maLichHoc}`](#api-23-get-lecturer-attendance-maLichHoc) : Lấy danh sách điểm danh của một buổi học chi tiết (Có meta)
- **24.** [POST `/lecturer/attendance/{maLichHoc}`](#api-24-post-lecturer-attendance-maLichHoc) : Lưu kết quả điểm danh buổi học (Tự động cập nhật số buổi vắng & Đình chỉ)

### 7. Phân hệ Trợ lý Đào tạo (Academic Assistant)
*Quản lý học kỳ, lớp học phần, xếp lịch và duyệt yêu cầu*

- **25.** [GET `/assistant/semesters`](#api-25-get-assistant-semesters) : Lấy danh sách học kỳ và trạng thái cho phép chỉnh sửa (Có meta)
- **26.** [GET `/assistant/semesters/{maHocKy}/classes`](#api-26-get-assistant-semesters-maHocKy-classes) : Xem danh sách Lớp học phần trong học kỳ (Có meta phân trang)
- **27.** [POST `/assistant/semesters/{maHocKy}/classes`](#api-27-post-assistant-semesters-maHocKy-classes) : Tạo lớp học phần mới (Tự động sinh lịch học chi tiết & gán phòng)
- **28.** [PUT `/assistant/classes/{maLHP}`](#api-28-put-assistant-classes-maLHP) : Sửa thông tin lớp học phần (Tự động cập nhật lại lịch học)
- **29.** [DELETE `/assistant/classes/{maLHP}`](#api-29-delete-assistant-classes-maLHP) : Xóa lớp học phần (Chỉ xóa được khi chưa có SV đăng ký)
- **30.** [PATCH `/assistant/classes/{maLHP}/assign-lecturer`](#api-30-patch-assistant-classes-maLHP-assign-lecturer) : Phân công giảng viên cho lớp học phần (Kiểm tra xung đột lịch dạy)
- **31.** [POST `/assistant/classes/open-registration-period`](#api-31-post-assistant-classes-open-registration-period) : Cài đặt đợt mở đăng ký cho một hoặc nhiều lớp học phần
- **32.** [GET `/assistant/classes/{maLHP}/students`](#api-32-get-assistant-classes-maLHP-students) : Xem danh sách sinh viên ghi danh và xuất file in (Có meta phân trang)
- **33.** [GET `/assistant/schedules/{maLHP}`](#api-33-get-assistant-schedules-maLHP) : Lấy danh sách tất cả các buổi học chi tiết trong kỳ của lớp (Có meta)
- **34.** [PUT `/assistant/schedules/{maLHP}`](#api-34-put-assistant-schedules-maLHP) : Điều chỉnh dời ngày, đổi phòng học chi tiết (Kiểm tra trùng phòng)
- **35.** [DELETE `/assistant/schedules/{maLH}`](#api-35-delete-schedules-maLH) : Xóa một lịch học cụ thể
- **36.** [GET `/assistant/class-requests`](#api-36-get-assistant-class-requests) : Lấy danh sách phiếu kiến nghị mở lớp từ sinh viên (Có meta phân trang)
- **37.** [PATCH `/assistant/class-requests/{maYeuCau}/status`](#api-37-patch-assistant-class-requests-maYeuCau-status) : Tiếp nhận hoặc Duyệt / Từ chối yêu cầu mở lớp

### 8. Phân hệ Trưởng phòng Đào tạo (Head Office)
*Quản lý học phần gốc, giám sát nhân sự, sinh viên và thống kê*

- **38.** [GET `/head-office/courses`](#api-38-get-head-office-courses) : Quản lý danh mục học phần gốc toàn trường (Có meta phân trang)
- **39.** [POST `/head-office/courses`](#api-39-post-head-office-courses) : Thêm mới một học phần gốc vào chương trình khung
- **40.** [PUT `/head-office/courses`](#api-40-get-head-office-semesters) : sửa thông tin học phần
- **41.** [GET `/head-office/semesters`](#api-41-get-head-office-semesters) : Lấy toàn bộ danh sách các học kỳ toàn trường
- **42.** [POST `/head-office/semesters`](#api-42-post-head-office-semesters) : Tạo mới một học kỳ đào tạo
- **43.** [GET `/head-office/lecturers`](#api-43-get-head-office-lecturers) : Tra cứu danh sách giảng viên toàn trường (Có meta phân trang)
- **44.** [GET `/head-office/students`](#api-44-get-head-office-students) : Quản lý tra cứu danh sách sinh viên toàn trường (Có meta phân trang)
- **45.** [GET `/head-office/statistics`](#api-45-get-head-office-statistics) : Thống kê số liệu sinh viên và học vụ toàn trường (Có meta báo cáo)

---

## CHI TIẾT CÁC API ROUTE

<a id="api-1-post-auth-login"></a>
### 01. [POST] `/auth/login`
**Tên chức năng:** Đăng nhập dành cho Sinh viên (Cổng /login)

- **Phân hệ (Tag):** `1. Xác thực & Tài khoản (Auth)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/auth/login`
- **Mô tả nghiệp vụ:** Xác thực tài khoản email SV, cấp JWT Access Token và Refresh Token trong HttpOnly Cookie.

#### 1. Dữ liệu đầu vào (Input)

**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `email` | `string` | **Bắt buộc** | Địa chỉ email định danh tài khoản |
| `password` | `string` | **Bắt buộc** | Mật khẩu xác thực tài khoản |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "email": "sinhvien01@ptit.edu.vn",
  "password": "Password@123"
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Đăng nhập thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJCMjFEQ0NOMDAxIiwicm9sZSI6IlNJTkhWSUVOIiwiZXhwIjoxNjk1MTI4NDAwfQ.kYx_sample_token...",
    "user": {
      "maSV": "B21DCCN001",
      "hoTen": "Nguyễn Văn An",
      "role": "SINHVIEN",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg"
    }
  }
}
```

##### Mã phản hồi HTTP `401` - Sai thông tin đăng nhập

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 401,
  "message": "Sai thông tin đăng nhập",
  "data": null
}
```

---

<a id="api-2-post-auth-admin-login"></a>
### 02. [POST] `/auth/admin/login`
**Tên chức năng:** Đăng nhập dành cho Cán bộ / Quản trị viên (Cổng /admin/login)

- **Phân hệ (Tag):** `1. Xác thực & Tài khoản (Auth)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/auth/admin/login`
- **Mô tả nghiệp vụ:** Xác thực tài khoản Giảng viên, Trợ lý đào tạo hoặc Trưởng phòng đào tạo.

#### 1. Dữ liệu đầu vào (Input)

**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `email` | `string` | **Bắt buộc** | Địa chỉ email định danh tài khoản |
| `password` | `string` | **Bắt buộc** | Mật khẩu xác thực tài khoản |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "email": "canbo.daotao@ptit.edu.vn",
  "password": "Password@123"
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Đăng nhập cán bộ thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Đăng nhập cán bộ thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJCMjFEQ0NOMDAxIiwicm9sZSI6IlNJTkhWSUVOIiwiZXhwIjoxNjk1MTI4NDAwfQ.kYx_sample_token...",
    "user": {
      "maNV": "GV012",
      "hoTen": "TS. Trần Thị Bình",
      "role": "GIANGVIEN",
      "anhThe": "https://ptit.edu.vn/storage/avatars/GV012.jpg"
    }
  }
}
```

---

<a id="api-3-post-auth-logout"></a>
### 03. [POST] `/auth/logout`
**Tên chức năng:** Đăng xuất người dùng

- **Phân hệ (Tag):** `1. Xác thực & Tài khoản (Auth)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/auth/logout`
- **Mô tả nghiệp vụ:** Thu hồi Access Token và xóa Refresh Token trong HttpOnly Cookie.

#### 1. Dữ liệu đầu vào (Input)

*Endpoint không yêu cầu tham số đường dẫn, truy vấn hoặc Request Body.*

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Đăng xuất thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Đăng xuất thành công",
  "data": null
}
```

---

<a id="api-4-post-auth-refresh-token"></a>
### 04. [POST] `/auth/refresh-token`
**Tên chức năng:** Cấp mới Access Token bằng Refresh Token

- **Phân hệ (Tag):** `1. Xác thực & Tài khoản (Auth)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/auth/refresh-token`
- **Mô tả nghiệp vụ:** Đọc Refresh Token từ HttpOnly Cookie để cấp Access Token mới có hạn 15-30 phút.

#### 1. Dữ liệu đầu vào (Input)

*Endpoint không yêu cầu tham số đường dẫn, truy vấn hoặc Request Body.*

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Cấp token mới thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Cấp token mới thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJCMjFEQ0NOMDAxIiwicm9sZSI6IlNJTkhWSUVOIiwiZXhwIjoxNjk1MTI4NDAwfQ.kYx_sample_token..."
  }
}
```

---

<a id="api-5-get-profile-me"></a>
### 05. [GET] `/profile/me`
**Tên chức năng:** Xem thông tin cá nhân của người dùng hiện tại

- **Phân hệ (Tag):** `2. Hồ sơ cá nhân (Profile)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/profile/me`

#### 1. Dữ liệu đầu vào (Input)

*Endpoint không yêu cầu tham số đường dẫn, truy vấn hoặc Request Body.*

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Thông tin người dùng chi tiết

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Thông tin người dùng chi tiết",
  "data": {
    "maDinhDanh": "B21DCCN001",
    "hoTen": "Nguyễn Văn An",
    "ngaySinh": "2003-05-15",
    "gioiTinh": "Nam",
    "email": "sinhvien01@ptit.edu.vn",
    "soDienThoai": "0912345678",
    "role": "SINHVIEN",
    "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg",
    "thongTinHocVu": {
      "nienKhoa": "2021-2025",
      "khoa": "Công nghệ thông tin 1",
      "chuyenNganh": "Kỹ nghệ phần mềm",
      "soTinChiTichLuy": 95,
      "gpaHienTai": 3.45
    }
  }
}
```

---

<a id="api-6-put-profile-change-password"></a>
### 06. [PUT] `/profile/change-password`
**Tên chức năng:** Đổi mật khẩu tài khoản

- **Phân hệ (Tag):** `2. Hồ sơ cá nhân (Profile)`
- **Phương thức HTTP:** `PUT`
- **Đường dẫn endpoint:** `/profile/change-password`

#### 1. Dữ liệu đầu vào (Input)

**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `matKhauCu` | `string` | **Bắt buộc** | Mật khẩu hiện tại đang sử dụng để xác thực trước khi đổi |
| `matKhauMoi` | `string` | **Bắt buộc** | Mật khẩu mới cần thiết lập thay thế |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "matKhauCu": "OldPass@123",
  "matKhauMoi": "NewPass@456"
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Đổi mật khẩu thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Đổi mật khẩu thành công",
  "data": null
}
```

##### Mã phản hồi HTTP `400` - Mật khẩu cũ không đúng

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 400,
  "message": "Mật khẩu cũ không đúng",
  "data": null
}
```

---

<a id="api-7-get-notifications"></a>
### 07. [GET] `/notifications`
**Tên chức năng:** Lấy danh sách thông báo chung  (Có meta phân trang)

- **Phân hệ (Tag):** `3. Thông báo học vụ (Notifications)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/notifications`

#### 1. Dữ liệu đầu vào (Input)

**Tham số truy vấn (Query Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mặc định | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `page` | `integer` | Tùy chọn | `1` | Số thứ tự trang cần lấy trong phân trang (mặc định: 1) |
| `limit` | `integer` | Tùy chọn | `10` | Số lượng bản ghi tối đa trên một trang (mặc định: 10) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách thông báo kèm meta phân trang

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách thông báo kèm meta phân trang",
  "data": [
    {
      "maThongBao": 101,
      "tieuDe": "Thông báo đăng ký học phần Học kỳ 1 năm học 2025 - 2026",
      "noiDung": "Sinh viên khóa 2021, 2022 thực hiện đăng ký học phần qua cổng thông tin từ ngày 15/08/2025.",
      "phamVi": "TOAN_TRUONG",
      "ngayTao": "2025-08-01T08:30:00Z"
    },
    {
      "maThongBao": 102,
      "tieuDe": "Thông báo đăng ký thi học kỳ chính thức",
      "noiDung": "Sinh viên khóa 2021, 2022 thực hiện đăng ký học phần qua cổng thông tin từ ngày 15/08/2025.",
      "phamVi": "TOAN_TRUONG",
      "ngayTao": "2025-08-01T08:30:00Z"
    }
  ],
  "meta": {
    "page": 1,
    "pageSize": 10,
    "totalItems": 45,
    "totalPages": 5,
    "unreadCount": 3
  }
}
```

---

<a id="api-8-post-notifications"></a>
### 08. [POST] `/notifications`
**Tên chức năng:** Tạo và phát hành thông báo học vụ mới

- **Phân hệ (Tag):** `3. Thông báo học vụ (Notifications)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/notifications`

#### 1. Dữ liệu đầu vào (Input)

**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `tieuDe` | `string` | **Bắt buộc** | Tiêu đề của bài thông báo |
| `noiDung` | `string` | **Bắt buộc** | Nội dung văn bản chi tiết của thông báo |
| `phamVi` | `string` | Tùy chọn | Phạm vi đối tượng tiếp nhận thông báo (`TOAN_TRUONG`, `KHOA`, `LOP`) |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "tieuDe": "Thông báo đăng ký học phần Học kỳ 1 năm học 2025 - 2026",
  "noiDung": "Sinh viên khóa 2021, 2022 thực hiện đăng ký học phần qua cổng thông tin từ ngày 15/08/2025.",
  "phamVi": "TOAN_TRUONG"
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `201` - Phát hành thông báo thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 201,
  "message": "Phát hành thông báo thành công",
  "data": {
    "maThongBao": 101,
    "ngayTao": "2025-08-01T08:30:00Z"
  }
}
```

---

<a id="api-9-put-notifications-id"></a>
### 09. [PUT] `/notifications/{id}`
**Tên chức năng:** Cập nhật nội dung thông báo

- **Phân hệ (Tag):** `3. Thông báo học vụ (Notifications)`
- **Phương thức HTTP:** `PUT`
- **Đường dẫn endpoint:** `/notifications/{id}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `id` | `integer` | **Bắt buộc** | Mã định danh thông báo học vụ cần thao tác |


**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `tieuDe` | `string` | Tùy chọn | Tiêu đề của bài thông báo |
| `noiDung` | `string` | Tùy chọn | Nội dung văn bản chi tiết của thông báo |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "tieuDe": "Thông báo đăng ký học phần Học kỳ 1 năm học 2025 - 2026",
  "noiDung": "Sinh viên khóa 2021, 2022 thực hiện đăng ký học phần qua cổng thông tin từ ngày 15/08/2025."
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Cập nhật thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Cập nhật thành công",
  "data": null
}
```

---

<a id="api-10-delete-notifications-id"></a>
### 10. [DELETE] `/notifications/{id}`
**Tên chức năng:** Xóa mềm thông báo học vụ

- **Phân hệ (Tag):** `3. Thông báo học vụ (Notifications)`
- **Phương thức HTTP:** `DELETE`
- **Đường dẫn endpoint:** `/notifications/{id}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `id` | `integer` | **Bắt buộc** | Mã định danh thông báo học vụ cần thao tác |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Xóa thông báo thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Xóa thông báo thành công",
  "data": null
}
```

---

<a id="api-11-get-student-grades"></a>
### 11. [GET] `/student/grades`
**Tên chức năng:** Xem bảng điểm chi tiết theo từng học kỳ (Có meta thống kê)

- **Phân hệ (Tag):** `4. Phân hệ Sinh viên - Điểm & TKB (Student Transcript & Schedule)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/student/grades`

#### 1. Dữ liệu đầu vào (Input)

*Endpoint không yêu cầu tham số đường dẫn, truy vấn hoặc Request Body.*

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Bảng điểm sinh viên kèm meta học vụ tích lũy

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Bảng điểm sinh viên kèm meta học vụ tích lũy",
  "data": [
    {
      "maHocKy": "2025_2026_HK1",
      "tenHocKy": "Học kỳ 1 (2025 - 2026)",
      "diemTBHK": 3.52,
      "soTinChiDatHK": 18,
      "xepLoaiHK": "Giỏi",
      "danhSachMonHoc": [
        {
          "maHP": "INT1405",
          "tenHP": "Kỹ nghệ phần mềm",
          "soTinChi": 3,
          "diemQuaTrinh": 8.5,
          "diemCuoiKy": 8.0,
          "diemTongKet": 8.2,
          "ketQua": "Đạt"
        },
        {
          "maHP": "INT1416",
          "tenHP": "Kiến trúc phần mềm",
          "soTinChi": 3,
          "diemQuaTrinh": 9.0,
          "diemCuoiKy": 8.5,
          "diemTongKet": 8.7,
          "ketQua": "Đạt"
        }
      ]
    },
    {
      "maHocKy": "2024_2025_HK2",
      "tenHocKy": "Học kỳ 2 (2024 - 2025)",
      "diemTBHK": 3.52,
      "soTinChiDatHK": 18,
      "xepLoaiHK": "Giỏi",
      "danhSachMonHoc": [
        {
          "maHP": "INT1405",
          "tenHP": "Kỹ nghệ phần mềm",
          "soTinChi": 3,
          "diemQuaTrinh": 8.5,
          "diemCuoiKy": 8.0,
          "diemTongKet": 8.2,
          "ketQua": "Đạt"
        },
        {
          "maHP": "INT1416",
          "tenHP": "Kiến trúc phần mềm",
          "soTinChi": 3,
          "diemQuaTrinh": 9.0,
          "diemCuoiKy": 8.5,
          "diemTongKet": 8.7,
          "ketQua": "Đạt"
        }
      ]
    }
  ],
  "meta": {
    "gpaHe4ToanKhoa": 3.45,
    "gpaHe10ToanKhoa": 8.12,
    "tongTinChiTichLuy": 95,
    "xepLoaiToanKhoa": "Giỏi",
    "soHocKyDaHoc": 5
  }
}
```

---

<a id="api-12-get-student-schedule"></a>
### 12. [GET] `/student/schedule`
**Tên chức năng:** Lấy thời khóa biểu sinh viên dạng ma trận tuần (Có meta học kỳ)

- **Phân hệ (Tag):** `4. Phân hệ Sinh viên - Điểm & TKB (Student Transcript & Schedule)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/student/schedule`

#### 1. Dữ liệu đầu vào (Input)

**Tham số truy vấn (Query Parameters):**

None

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Thời khóa biểu sinh viên cho ma trận tuần

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Thời khóa biểu sinh viên cho ma trận tuần",
  "data": [
    {
      "maLHP": "INT1405_01",
      "tenHP": "Kỹ nghệ phần mềm",
      "tenGiangVien": "TS. Trần Thị Bình",
      "thu": 2,
      "tietBD": 1,
      "soTiet": 3,
      "phongHoc": "A2-301"
    },
    {
      "maLHP": "INT1416_02",
      "tenHP": "Kiến trúc phần mềm",
      "tenGiangVien": "TS. Trần Thị Bình",
      "thu": 2,
      "tietBD": 1,
      "soTiet": 3,
      "phongHoc": "A2-301",
      "tenLHP": "Kiến trúc phần mềm - Lớp 02"
    }
  ],
  "meta": {
    "maHocKy": "2025_2026_HK1",
    "ngayBatDauHocKy": "2025-09-01",
    "ngayKetThucHocKy": "2026-01-15",
    "tongSoMonHoc": 6,
    "tongSoTietTrongTuan": 20
  }
}
```

---

<a id="api-13-get-student-registration-open-classes"></a>
### 13. [GET] `/student/registration/open-classes`
**Tên chức năng:** Lấy danh sách lớp học phần đang mở đăng ký (Có meta phân trang)

- **Phân hệ (Tag):** `5. Phân hệ Sinh viên - Đăng ký học phần (Student Registration)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/student/registration/open-classes`

#### 1. Dữ liệu đầu vào (Input)

**Tham số truy vấn (Query Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mặc định | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `maHP` | `string` | Tùy chọn | - | Mã học phần gốc cần tra cứu/lọc (ví dụ: `INT1405`) |
| `keyword` | `string` | Tùy chọn | - | Từ khóa tìm kiếm theo tên hoặc mã môn học |
| `page` | `integer` | Tùy chọn | `1` | Số thứ tự trang cần lấy trong phân trang (mặc định: 1) |
| `limit` | `integer` | Tùy chọn | `10` | Số lượng bản ghi tối đa trên một trang (mặc định: 10) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách lớp mở kèm meta phân trang

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách lớp mở kèm meta phân trang",
  "data": [
    {
      "maLHP": "INT1405_01",
      "tenLHP": "Kỹ nghệ phần mềm - Lớp 01",
      "maHP": "INT1405",
      "tenHP": "Kỹ nghệ phần mềm",
      "soTinChi": 3,
      "tenGiangVien": "TS. Trần Thị Bình",
      "thu": 2,
      "tietBatDau": 1,
      "soTiet": 3,
      "phongHoc": "A2-301",
      "siSoHienTai": 58,
      "siSoToiDa": 60,
      "thoiGianBDDK": "2025-08-15T08:00:00Z",
      "thoiGianKTDK": "2025-08-25T17:00:00Z",
      "trangThai": "DANG_MO"
    },
    {
      "maLHP": "INT1416_02",
      "tenLHP": "Kiến trúc phần mềm - Lớp 02",
      "maHP": "INT1416",
      "tenHP": "Kiến trúc phần mềm",
      "soTinChi": 3,
      "tenGiangVien": "TS. Trần Thị Bình",
      "thu": 2,
      "tietBatDau": 1,
      "soTiet": 3,
      "phongHoc": "A2-301",
      "siSoHienTai": 58,
      "siSoToiDa": 60,
      "thoiGianBDDK": "2025-08-15T08:00:00Z",
      "thoiGianKTDK": "2025-08-25T17:00:00Z",
      "trangThai": "DANG_MO"
    }
  ],
  "meta": {
    "page": 1,
    "pageSize": 10,
    "totalItems": 45,
    "totalPages": 5
  }
}
```

---

<a id="api-14-get-student-registration-registered-classes"></a>
### 14. [GET] `/student/registration/registered-classes`
**Tên chức năng:** Lấy danh sách lớp học phần sinh viên đã ghi danh thành công

- **Phân hệ (Tag):** `5. Phân hệ Sinh viên - Đăng ký học phần (Student Registration)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/student/registration/registered-classes`

#### 1. Dữ liệu đầu vào (Input)

*Endpoint không yêu cầu tham số đường dẫn, truy vấn hoặc Request Body.*

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách lớp đã ghi danh kèm meta tổng tín chỉ

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách lớp đã ghi danh kèm meta tổng tín chỉ",
  "data": [
    {
      "maHoSo": 10582,
      "maLHP": "INT1405_01",
      "tenHP": "Kỹ nghệ phần mềm",
      "soTinChi": 3,
      "thu": 2,
      "tietBatDau": 1,
      "soTiet": 3,
      "tenGiangVien": "TS. Trần Thị Bình",
      "phongHoc": "A2-301"
    },
    {
      "maHoSo": 10582,
      "maLHP": "INT1416_02",
      "tenHP": "Kiến trúc phần mềm",
      "soTinChi": 3,
      "thu": 2,
      "tietBatDau": 1,
      "soTiet": 3,
      "tenGiangVien": "TS. Trần Thị Bình",
      "phongHoc": "A2-301",
      "tenLHP": "Kiến trúc phần mềm - Lớp 02"
    }
  ],
  "meta": {
    "tongSoLopDaDangKy": 6,
    "tongSoTinChiDaDangKy": 18,
    "hanMucTinChiToiDa": 24
  }
}
```

---

<a id="api-15-post-student-registration-enroll"></a>
### 15. [POST] `/student/registration/enroll`
**Tên chức năng:** Đăng ký một lớp học phần mới (Kiểm tra xung đột & Sĩ số)

- **Phân hệ (Tag):** `5. Phân hệ Sinh viên - Đăng ký học phần (Student Registration)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/student/registration/enroll`

#### 1. Dữ liệu đầu vào (Input)

**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần mở trong kỳ |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "maLHP": "INT1405_01"
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `201` - Đăng ký thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 201,
  "message": "Đăng ký thành công",
  "data": {
    "maHoSo": 10582,
    "maLHP": "INT1405_01",
    "trangThai": "DANG_MO",
    "ngayDangKy": "2025-08-15T09:15:30Z"
  }
}
```

##### Mã phản hồi HTTP `400` - Lớp đã đầy sĩ số hoặc chưa tới giờ mở cổng

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 400,
  "message": "Lớp đã đầy sĩ số hoặc chưa tới giờ mở cổng",
  "data": null
}
```

##### Mã phản hồi HTTP `409` - Trùng thời khóa biểu với lớp khác

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 409,
  "message": "Trùng thời khóa biểu với lớp khác",
  "data": null
}
```

---

<a id="api-16-delete-student-registration-cancel-maHoSo"></a>
### 16. [DELETE] `/student/registration/cancel/{maHoSo}`
**Tên chức năng:** Hủy đăng ký một lớp học phần

- **Phân hệ (Tag):** `5. Phân hệ Sinh viên - Đăng ký học phần (Student Registration)`
- **Phương thức HTTP:** `DELETE`
- **Đường dẫn endpoint:** `/student/registration/cancel/{maHoSo}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maHoSo` | `integer` | **Bắt buộc** | Mã định danh hồ sơ ghi danh của sinh viên |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Hủy lớp thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Hủy lớp thành công",
  "data": {
    "maHoSo": 10582,
    "trangThai": "DANG_MO"
  }
}
```

---

<a id="api-17-post-student-registration-open-request"></a>
### 17. [POST] `/student/registration/open-request`
**Tên chức năng:** Gửi phiếu kiến nghị mở thêm lớp học phần

- **Phân hệ (Tag):** `5. Phân hệ Sinh viên - Đăng ký học phần (Student Registration)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/student/registration/open-request`

#### 1. Dữ liệu đầu vào (Input)

**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `maHP` | `string` | **Bắt buộc** | Mã định danh học phần gốc theo chương trình đào tạo |
| `lyDo` | `string` | **Bắt buộc** | Lý do chi tiết giải trình việc xin mở thêm lớp |
| `lichHocMongMuon` | `string` | Tùy chọn | Khung thời gian / các thứ và tiết mong muốn tổ chức học |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "maHP": "INT1405",
  "lyDo": "Cần hoàn thành tín chỉ bắt buộc để kịp tiến độ tốt nghiệp",
  "lichHocMongMuon": "Thứ 4 (Tiết 1-3) hoặc Thứ 6 (Tiết 7-9)"
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `201` - Gửi kiến nghị thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 201,
  "message": "Gửi kiến nghị thành công",
  "data": {
    "maYeuCau": "YC-2025-0012",
    "trangThai": "DANG_MO"
  }
}
```

---

<a id="api-18-get-lecturer-grades"></a>
### 18. [GET] `/lecturer/grades`
**Tên chức năng:** Lấy danh sách các lớp học phần được phân công giảng dạy (Có meta)

- **Phân hệ (Tag):** `6. Phân hệ Giảng viên (Lecturer)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/lecturer/grades`

#### 1. Dữ liệu đầu vào (Input)

*Endpoint không yêu cầu tham số đường dẫn, truy vấn hoặc Request Body.*

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách lớp giảng dạy

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách lớp giảng dạy",
  "data": [
    {
      "maLHP": "INT1405_01",
      "tenHP": "Kỹ nghệ phần mềm",
      "siSo": 58,
      "khoaDiem": false,
      "trangThai": "DANG_MO"
    },
    {
      "maLHP": "INT1416_02",
      "tenHP": "Kiến trúc phần mềm",
      "siSo": 58,
      "khoaDiem": false,
      "trangThai": "DAGUI",
      "tenLHP": "Kiến trúc phần mềm - Lớp 02"
    }
  ],
  "meta": {
    "tongSoLopPhuTrach": 4,
    "tongSoSinhVien": 240
  }
}
```

---

<a id="api-19-get-lecturer-grades-maLHP"></a>
### 19. [GET] `/lecturer/grades/{maLHP}`
**Tên chức năng:** Lấy bảng điểm sinh viên của một lớp học phần (Có meta thống kê)

- **Phân hệ (Tag):** `6. Phân hệ Giảng viên (Lecturer)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/lecturer/grades/{maLHP}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần (ví dụ: `INT1405_01`) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách sinh viên và bảng điểm của lớp

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách sinh viên và bảng điểm của lớp",
  "data": [
    {
      "maSV": "B21DCCN001",
      "hoTen": "Nguyễn Văn An",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg",
      "diemQuaTrinh": 8.5,
      "diemCuoiKy": 8.0,
      "diemTongKet": 8.2,
      "ketQua": "Đạt",
      "soBuoiVang": 1,
      "trangThai": "DANG_MO"
    },
    {
      "maSV": "B21DCCN002",
      "hoTen": "Lê Thị Cẩm Nhung",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg",
      "diemQuaTrinh": 8.5,
      "diemCuoiKy": 8.0,
      "diemTongKet": 8.2,
      "ketQua": "Đạt",
      "soBuoiVang": 1,
      "trangThai": "DANG_MO"
    }
  ],
  "meta": {
    "maLHP": "INT1405_01",
    "siSoHienTai": 58,
    "soSinhVienDaNhapDiem": 58,
    "soSinhVienChuaNhapDiem": 2,
    "khoaDiem": false
  }
}
```

---

<a id="api-20-put-lecturer-grades-maLHP"></a>
### 20. [PUT] `/lecturer/grades/{maLHP}`
**Tên chức năng:** Cập nhật / Lưu điểm quá trình và cuối kỳ (Trigger tự động tính tổng kết)

- **Phân hệ (Tag):** `6. Phân hệ Giảng viên (Lecturer)`
- **Phương thức HTTP:** `PUT`
- **Đường dẫn endpoint:** `/lecturer/grades/{maLHP}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần (ví dụ: `INT1405_01`) |


**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `bangDiem` | `array` | **Bắt buộc** | - |
| `bangDiem[].maSV` | `string` | **Bắt buộc** | Mã sinh viên định danh |
| `bangDiem[].diemQuaTrinh` | `number` | Tùy chọn | Điểm đánh giá quá trình/chuyên cần/kiểm tra (hệ số 0.3 - 0.4) |
| `bangDiem[].diemCuoiKy` | `number` | Tùy chọn | Điểm thi kết thúc học phần (hệ số 0.6 - 0.7) |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "bangDiem": [
    {
      "maSV": "B21DCCN001",
      "diemQuaTrinh": 8.5,
      "diemCuoiKy": 8.0
    },
    {
      "maSV": "B21DCCN002",
      "diemQuaTrinh": 7.5,
      "diemCuoiKy": 9.0
    }
  ]
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Lưu điểm thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Lưu điểm thành công",
  "data": null
}
```

---

<a id="api-21-get-lecturer-grades-maLHP-export"></a>
### 21. [GET] `/lecturer/grades/{maLHP}/export`
**Tên chức năng:** Xuất file Excel bảng điểm của lớp học phần

- **Phân hệ (Tag):** `6. Phân hệ Giảng viên (Lecturer)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/lecturer/grades/{maLHP}/export`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần (ví dụ: `INT1405_01`) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Tải về file bảng điểm định dạng .xlsx

- **Định dạng Content-Type:** `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`

> [!TIP]
> Endpoint trả về luồng dữ liệu nhị phân (Binary Stream) của file Excel `.xlsx`. Client tiếp nhận bằng cách tải trực tiếp tệp về máy tính người dùng.
```http
HTTP/1.1 200 OK
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="BangDiem_INT1405_01.xlsx"
```

---

<a id="api-22-get-lecturer-teaching-schedule"></a>
### 22. [GET] `/lecturer/teaching-schedule`
**Tên chức năng:** Lấy lịch giảng dạy tuần thực tế theo học kỳ (Có meta)

- **Phân hệ (Tag):** `6. Phân hệ Giảng viên (Lecturer)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/lecturer/teaching-schedule`

#### 1. Dữ liệu đầu vào (Input)

*Endpoint không yêu cầu tham số đường dẫn, truy vấn hoặc Request Body.*

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Ma trận lịch giảng dạy tuần của giảng viên

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Ma trận lịch giảng dạy tuần của giảng viên",
  "data": [
    {
      "maLichHoc": "LH_20250915_T13",
      "maLHP": "INT1405_01",
      "tenHP": "Kỹ nghệ phần mềm",
      "ngayHoc": "2025-09-15",
      "thu": 2,
      "tietBatDau": 1,
      "soTiet": 3,
      "phongHoc": "A2-301"
    },
    {
      "maLichHoc": "LH_20250917_T46",
      "maLHP": "INT1416_02",
      "tenHP": "Kiến trúc phần mềm",
      "ngayHoc": "2025-09-17",
      "thu": 2,
      "tietBatDau": 1,
      "soTiet": 3,
      "phongHoc": "A2-301",
      "tenLHP": "Kiến trúc phần mềm - Lớp 02"
    }
  ],
  "meta": {
    "tuanHienTai": 3,
    "tongSoBuoiDayTrongTuan": 4,
    "tongSoTietDayTrongTuan": 12
  }
}
```

---

<a id="api-23-get-lecturer-attendance-maLichHoc"></a>
### 23. [GET] `/lecturer/attendance/{maLichHoc}`
**Tên chức năng:** Lấy danh sách điểm danh của một buổi học chi tiết (Có meta)

- **Phân hệ (Tag):** `6. Phân hệ Giảng viên (Lecturer)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/lecturer/attendance/{maLichHoc}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLichHoc` | `string` | **Bắt buộc** | Mã định danh buổi học chi tiết trong thời khóa biểu (ví dụ: `LH_20250915_T13`) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách điểm danh của buổi học

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách điểm danh của buổi học",
  "data": [
    {
      "maSV": "B21DCCN001",
      "hoTen": "Nguyễn Văn An",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg",
      "coMat": true,
      "ghiChu": "Có mặt đúng giờ"
    },
    {
      "maSV": "B21DCCN002",
      "hoTen": "Lê Thị Cẩm Nhung",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg",
      "coMat": true,
      "ghiChu": "Có mặt đúng giờ"
    }
  ],
  "meta": {
    "maLichHoc": "LH_20250915_T13",
    "siSoLop": 60,
    "soCoMat": 58,
    "soVangMat": 2
  }
}
```

---

<a id="api-24-post-lecturer-attendance-maLichHoc"></a>
### 24. [POST] `/lecturer/attendance/{maLichHoc}`
**Tên chức năng:** Lưu kết quả điểm danh buổi học (Tự động cập nhật số buổi vắng & Đình chỉ)

- **Phân hệ (Tag):** `6. Phân hệ Giảng viên (Lecturer)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/lecturer/attendance/{maLichHoc}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLichHoc` | `string` | **Bắt buộc** | Mã định danh buổi học chi tiết trong thời khóa biểu (ví dụ: `LH_20250915_T13`) |


**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `danhSach` | `array` | **Bắt buộc** | - |
| `danhSach[].maSV` | `string` | **Bắt buộc** | Mã sinh viên định danh |
| `danhSach[].coMat` | `boolean` | **Bắt buộc** | Trạng thái điểm danh (`true`: có mặt, `false`: vắng mặt) |
| `danhSach[].ghiChu` | `string` | Tùy chọn | Ghi chú điểm danh của giảng viên về tình trạng sinh viên |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "danhSach": [
    {
      "maSV": "B21DCCN001",
      "coMat": true,
      "ghiChu": "Có mặt đúng giờ"
    },
    {
      "maSV": "B21DCCN002",
      "coMat": false,
      "ghiChu": "Vắng không phép"
    }
  ]
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Điểm danh hoàn tất

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Điểm danh hoàn tất",
  "data": null
}
```

---

<a id="api-25-get-assistant-semesters"></a>
### 25. [GET] `/assistant/semesters`
**Tên chức năng:** Lấy danh sách học kỳ và trạng thái cho phép chỉnh sửa (Có meta)

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/assistant/semesters`

#### 1. Dữ liệu đầu vào (Input)

*Endpoint không yêu cầu tham số đường dẫn, truy vấn hoặc Request Body.*

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách học kỳ kèm cờ isEditable

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách học kỳ kèm cờ isEditable",
  "data": [
    {
      "maHocKy": "2025_2026_HK1",
      "tenHocKy": "Học kỳ 1 (2025 - 2026)",
      "thoiGianBD": "2025-09-01",
      "thoiGianKT": "2026-01-15",
      "isEditable": true
    },
    {
      "maHocKy": "2024_2025_HK2",
      "tenHocKy": "Học kỳ 2 (2024 - 2025)",
      "thoiGianBD": "2025-09-01",
      "thoiGianKT": "2026-01-15",
      "isEditable": true
    }
  ],
  "meta": {
    "tongSoHocKy": 8,
    "hocKyHienTai": "2025_2026_HK1"
  }
}
```

---

<a id="api-26-get-assistant-semesters-maHocKy-classes"></a>
### 26. [GET] `/assistant/semesters/{maHocKy}/classes`
**Tên chức năng:** Xem danh sách Lớp học phần trong học kỳ (Có meta phân trang)

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/assistant/semesters/{maHocKy}/classes`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maHocKy` | `string` | **Bắt buộc** | Mã định danh học kỳ (ví dụ: `2025_2026_HK1`) |


**Tham số truy vấn (Query Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mặc định | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `page` | `integer` | Tùy chọn | `1` | Số thứ tự trang cần lấy trong phân trang (mặc định: 1) |
| `limit` | `integer` | Tùy chọn | `10` | Số lượng bản ghi tối đa trên một trang (mặc định: 10) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách LHP trong kỳ kèm meta

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách LHP trong kỳ kèm meta",
  "data": [
    {
      "maLHP": "INT1405_01",
      "tenLHP": "Kỹ nghệ phần mềm - Lớp 01",
      "maHP": "INT1405",
      "tenHP": "Kỹ nghệ phần mềm",
      "soTinChi": 3,
      "tenGiangVien": "TS. Trần Thị Bình",
      "thu": 2,
      "tietBatDau": 1,
      "soTiet": 3,
      "phongHoc": "A2-301",
      "siSoHienTai": 58,
      "siSoToiDa": 60,
      "thoiGianBDDK": "2025-08-15T08:00:00Z",
      "thoiGianKTDK": "2025-08-25T17:00:00Z",
      "trangThai": "DANG_MO"
    },
    {
      "maLHP": "INT1416_02",
      "tenLHP": "Kiến trúc phần mềm - Lớp 02",
      "maHP": "INT1416",
      "tenHP": "Kiến trúc phần mềm",
      "soTinChi": 3,
      "tenGiangVien": "TS. Trần Thị Bình",
      "thu": 2,
      "tietBatDau": 1,
      "soTiet": 3,
      "phongHoc": "A2-301",
      "siSoHienTai": 58,
      "siSoToiDa": 60,
      "thoiGianBDDK": "2025-08-15T08:00:00Z",
      "thoiGianKTDK": "2025-08-25T17:00:00Z",
      "trangThai": "DANG_MO"
    }
  ],
  "meta": {
    "page": 1,
    "pageSize": 10,
    "totalItems": 45,
    "totalPages": 5,
    "maHocKy": "2025_2026_HK1"
  }
}
```

---

<a id="api-27-post-assistant-semesters-maHocKy-classes"></a>
### 27. [POST] `/assistant/semesters/{maHocKy}/classes`
**Tên chức năng:** Tạo lớp học phần mới (Tự động sinh lịch học chi tiết & gán phòng)

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/assistant/semesters/{maHocKy}/classes`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maHocKy` | `string` | **Bắt buộc** | Mã định danh học kỳ (ví dụ: `2025_2026_HK1`) |


**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần mở trong kỳ |
| `tenLHP` | `string` | **Bắt buộc** | Tên định danh lớp học phần cụ thể |
| `maHP` | `string` | **Bắt buộc** | Mã định danh học phần gốc theo chương trình đào tạo |
| `thu` | `integer` | **Bắt buộc** | Thứ trong tuần diễn ra buổi học (2: Thứ Hai -> 7: Thứ Bảy, 8: CN) |
| `tietBD` | `integer` | **Bắt buộc** | Tiết bắt đầu của ca học (từ tiết 1 đến tiết 12) |
| `soTiet` | `integer` | **Bắt buộc** | Tổng số tiết học liên tiếp của buổi học |
| `siSoToiDa` | `integer` | **Bắt buộc** | Sĩ số tối đa cho phép tiếp nhận đăng ký của lớp |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "maLHP": "INT1405_01",
  "tenLHP": "Kỹ nghệ phần mềm - Lớp 01",
  "maHP": "INT1405",
  "thu": 2,
  "tietBD": 1,
  "soTiet": 3,
  "siSoToiDa": 60
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `201` - Tạo lớp thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 201,
  "message": "Tạo lớp thành công",
  "data": {
    "maLHP": "INT1405_01",
    "soBuoiHocDaSinh": 15
  }
}
```

---

<a id="api-28-put-assistant-classes-maLHP"></a>
### 28. [PUT] `/assistant/classes/{maLHP}`
**Tên chức năng:** Sửa thông tin lớp học phần (Tự động cập nhật lại lịch học)

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `PUT`
- **Đường dẫn endpoint:** `/assistant/classes/{maLHP}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần (ví dụ: `INT1405_01`) |


**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `tenLHP` | `string` | Tùy chọn | Tên định danh lớp học phần cụ thể |
| `siSoToiDa` | `integer` | Tùy chọn | Sĩ số tối đa cho phép tiếp nhận đăng ký của lớp |
| `thu` | `integer` | Tùy chọn | Thứ trong tuần diễn ra buổi học (2: Thứ Hai -> 7: Thứ Bảy, 8: CN) |
| `tietBD` | `integer` | Tùy chọn | Tiết bắt đầu của ca học (từ tiết 1 đến tiết 12) |
| `soTiet` | `integer` | Tùy chọn | Tổng số tiết học liên tiếp của buổi học |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "tenLHP": "Kỹ nghệ phần mềm - Lớp 01",
  "siSoToiDa": 60,
  "thu": 2,
  "tietBD": 1,
  "soTiet": 3
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Cập nhật thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Cập nhật thành công",
  "data": null
}
```

---

<a id="api-29-delete-assistant-classes-maLHP"></a>
### 29. [DELETE] `/assistant/classes/{maLHP}`
**Tên chức năng:** Xóa lớp học phần (Chỉ xóa được khi chưa có SV đăng ký)

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `DELETE`
- **Đường dẫn endpoint:** `/assistant/classes/{maLHP}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần (ví dụ: `INT1405_01`) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Xóa thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Xóa thành công",
  "data": null
}
```

---

<a id="api-30-patch-assistant-classes-maLHP-assign-lecturer"></a>
### 30. [PATCH] `/assistant/classes/{maLHP}/assign-lecturer`
**Tên chức năng:** Phân công giảng viên cho lớp học phần (Kiểm tra xung đột lịch dạy)

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `PATCH`
- **Đường dẫn endpoint:** `/assistant/classes/{maLHP}/assign-lecturer`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần (ví dụ: `INT1405_01`) |


**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `maGV` | `string` | **Bắt buộc** | Mã định danh của giảng viên được phân công |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "maGV": "GV012"
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Phân công thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Phân công thành công",
  "data": null
}
```

##### Mã phản hồi HTTP `409` - Giảng viên bị trùng lịch dạy vào thời gian này

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 409,
  "message": "Giảng viên bị trùng lịch dạy vào thời gian này",
  "data": null
}
```

---

<a id="api-31-post-assistant-classes-open-registration-period"></a>
### 31. [POST] `/assistant/classes/open-registration-period`
**Tên chức năng:** Cài đặt đợt mở đăng ký cho một hoặc nhiều lớp học phần

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/assistant/classes/open-registration-period`
- **Mô tả nghiệp vụ:** Ràng buộc mỗi lớp học phần chỉ được cấu hình mở đúng 1 đợt duy nhất và bắt buộc đã có giảng viên.

#### 1. Dữ liệu đầu vào (Input)

**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `dsMaLHP` | `array` | **Bắt buộc** | Danh sách các mã lớp học phần cần cấu hình mở đăng ký |
| `thoiGianBD` | `string` (`date-time`) | **Bắt buộc** | Thời gian bắt đầu (ngày hoặc thời điểm) |
| `thoiGianKT` | `string` (`date-time`) | **Bắt buộc** | Thời gian kết thúc (ngày hoặc thời điểm) |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "dsMaLHP": [
    "INT1405_01",
    "INT1408_02"
  ],
  "thoiGianBD": "2025-09-01",
  "thoiGianKT": "2026-01-15"
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Cài đặt thời gian mở đăng ký thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Cài đặt thời gian mở đăng ký thành công",
  "data": null
}
```

---

<a id="api-32-get-assistant-classes-maLHP-students"></a>
### 32. [GET] `/assistant/classes/{maLHP}/students`
**Tên chức năng:** Xem danh sách sinh viên ghi danh và xuất file in (Có meta phân trang)

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/assistant/classes/{maLHP}/students`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần (ví dụ: `INT1405_01`) |


**Tham số truy vấn (Query Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mặc định | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `page` | `integer` | Tùy chọn | `1` | Số thứ tự trang cần lấy khi `export=false` |
| `limit` | `integer` | Tùy chọn | `10` | Số bản ghi tối đa trên một trang khi `export=false` |
| `export` | `boolean` | Tùy chọn | `False` | Cờ yêu cầu xuất danh sách phục vụ in ấn/báo cáo (`true`/`false`, mặc định `false`) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách sinh viên của lớp học phần

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách sinh viên của lớp học phần",
  "data": [
    {
      "maSV": "B21DCCN001",
      "hoTen": "Nguyễn Văn An",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg",
      "ngayDangKy": "2025-08-15T09:15:30Z",
      "trangThai": "DANG_MO"
    },
    {
      "maSV": "B21DCCN002",
      "hoTen": "Lê Thị Cẩm Nhung",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg",
      "ngayDangKy": "2025-08-15T09:15:30Z",
      "trangThai": "DANG_MO"
    }
  ],
  "meta": {
    "maLHP": "INT1405_01",
    "siSoHienTai": 58,
    "siSoToiDa": 60,
    "page": 1,
    "pageSize": 10,
    "totalItems": 58,
    "totalPages": 6
  }
}
```

Khi `export=true`, API bỏ qua `page` và `limit`, xuất toàn bộ danh sách sinh viên dưới dạng file Excel `.xlsx`.

---

<a id="api-33-get-assistant-schedules-maLHP"></a>
### 33. [GET] `/assistant/schedules/{maLHP}`
**Tên chức năng:** Lấy danh sách tất cả các buổi học chi tiết trong kỳ của lớp (Có meta)

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/assistant/schedules/{maLHP}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần (ví dụ: `INT1405_01`) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Chi tiết các buổi học

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Chi tiết các buổi học",
  "data": [
    {
      "maLichHoc": "LH_20250915_T13",
      "ngayHoc": "2025-09-15",
      "phongHoc": "A2-301"
    },
    {
      "maLichHoc": "LH_20250917_T46",
      "ngayHoc": "2025-09-17",
      "phongHoc": "A2-301"
    }
  ],
  "meta": {
    "tongSoBuoiHoc": 15
  }
}
```

---

<a id="api-34-put-assistant-schedules-maLHP"></a>
### 34. [PUT] `/assistant/schedules/{maLHP}`
**Tên chức năng:** Điều chỉnh dời ngày, đổi phòng học chi tiết (Kiểm tra trùng phòng)

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `PUT`
- **Đường dẫn endpoint:** `/assistant/schedules/{maLHP}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | **Bắt buộc** | Mã định danh lớp học phần (ví dụ: `INT1405_01`) |


**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `maLichHoc` | `string` | **Bắt buộc** | Mã định danh của buổi học cụ thể trên hệ thống |
| `ngayHoc` | `string` (`date`) | **Bắt buộc** | Ngày diễn ra buổi học thực tế (định dạng YYYY-MM-DD) |
| `phongHoc` | `string` | **Bắt buộc** | Mã phòng học diễn ra buổi giảng dạy |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "maLichHoc": "LH_20250915_T13",
  "ngayHoc": "2025-09-15",
  "phongHoc": "A2-301"
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Đổi lịch/phòng thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Đổi lịch/phòng thành công",
  "data": null
}
```

##### Mã phản hồi HTTP `409` - Phòng học đã bị lớp khác đăng ký vào khung giờ này

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 409,
  "message": "Phòng học đã bị lớp khác đăng ký vào khung giờ này",
  "data": null
}
```

---
<a id="api-35-delete-schedules-maLH"></a>
### 35. [DELETE] `/assistant/schedules/{maLH}`
**Tên chức năng:** Xóa một lịch học cụ thể ra khỏi thời khóa biểu

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `DELETE`
- **Đường dẫn endpoint:** `/assistant/schedules/{maLH}`

#### 1. Dữ liệu đầu vào (Input)

**Tham số truy vấn (path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maLH` | `string` | **Bắt buộc** | Mã định danh của một buổi học cụ thể (`LichHoc.maLichHoc`) cần xóa |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Xóa lịch học thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "xóa lịch học thành công",
  "data": null
}
```

---
<a id="api-36-get-assistant-class-requests"></a>
### 36. [GET] `/assistant/class-requests`
**Tên chức năng:** Lấy danh sách phiếu kiến nghị mở lớp từ sinh viên (Có meta phân trang)

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/assistant/class-requests`

#### 1. Dữ liệu đầu vào (Input)

**Tham số truy vấn (Query Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mặc định | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `trangThai` | `string` | Tùy chọn | - | Lọc theo trạng thái hồ sơ/yêu cầu (`DAGUI`, `DATIEPNHAN`, `DADUYET`, `DATUCHOI`) [Giá trị hợp lệ: `DAGUI`, `DATIEPNHAN`, `DADUYET`, `DATUCHOI`] |
| `page` | `integer` | Tùy chọn | `1` | Số thứ tự trang cần lấy trong phân trang (mặc định: 1) |
| `limit` | `integer` | Tùy chọn | `10` | Số lượng bản ghi tối đa trên một trang (mặc định: 10) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách đề xuất mở lớp kèm meta

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách đề xuất mở lớp kèm meta",
  "data": [
    {
      "maYeuCau": "YC-2025-0012",
      "maSV": "B21DCCN001",
      "maHP": "INT1405",
      "tenHP": "Kỹ nghệ phần mềm",
      "lyDo": "Cần hoàn thành tín chỉ bắt buộc để kịp tiến độ tốt nghiệp",
      "lichHocMongMuon": "Thứ 4 (Tiết 1-3) hoặc Thứ 6 (Tiết 7-9)",
      "trangThai": "DATIEPNHAN",
      "ngayTao": "2025-08-01T08:30:00Z"
    },
    {
      "maYeuCau": "YC-2025-0012",
      "maSV": "B21DCCN002",
      "maHP": "INT1416",
      "tenHP": "Kiến trúc phần mềm",
      "lyDo": "Cần hoàn thành tín chỉ bắt buộc để kịp tiến độ tốt nghiệp",
      "lichHocMongMuon": "Thứ 4 (Tiết 1-3) hoặc Thứ 6 (Tiết 7-9)",
      "trangThai": "DADUYET",
      "ngayTao": "2025-08-01T08:30:00Z",
      "hoTen": "Lê Thị Cẩm Nhung"
    }
  ],
  "meta": {
    "page": 1,
    "pageSize": 10,
    "totalItems": 45,
    "totalPages": 5
  }
}
```

---

<a id="api-36-patch-assistant-class-requests-maYeuCau-status"></a>
### 37. [PATCH] `/assistant/class-requests/{maYeuCau}/status`
**Tên chức năng:** Tiếp nhận hoặc Duyệt / Từ chối yêu cầu mở lớp

- **Phân hệ (Tag):** `7. Phân hệ Trợ lý Đào tạo (Academic Assistant)`
- **Phương thức HTTP:** `PATCH`
- **Đường dẫn endpoint:** `/assistant/class-requests/{maYeuCau}/status`

#### 1. Dữ liệu đầu vào (Input)

**Tham số trên đường dẫn (Path Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mô tả chức năng |
| :--- | :--- | :---: | :--- |
| `maYeuCau` | `string` | **Bắt buộc** | Mã định danh phiếu kiến nghị mở lớp của sinh viên (ví dụ: `YC-2025-0012`) |


**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `trangThai` | `string` | **Bắt buộc** | Trạng thái xử lý mới [Giá trị hợp lệ: `DATIEPNHAN`, `DADUYET`, `DATUCHOI`] |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "trangThai": "DADUYET"
}
```

Luồng chuyển trạng thái hợp lệ:

```text
DAGUI -> DATIEPNHAN
DATIEPNHAN -> DADUYET
DAGUI hoặc DATIEPNHAN -> DATUCHOI
```

API không nhận hoặc lưu nội dung phản hồi tùy ý. Thông báo response được hệ thống tạo mặc định theo trạng thái mới.

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Cập nhật trạng thái thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Cập nhật trạng thái thành công",
  "data": null
}
```

---

<a id="api-37-get-head-office-courses"></a>
### 38. [GET] `/head-office/courses`
**Tên chức năng:** Quản lý danh mục học phần gốc toàn trường (Có meta phân trang)

- **Phân hệ (Tag):** `8. Phân hệ Trưởng phòng Đào tạo (Head Office)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/head-office/courses`

#### 1. Dữ liệu đầu vào (Input)

**Tham số truy vấn (Query Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mặc định | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `page` | `integer` | Tùy chọn | `1` | Số thứ tự trang cần lấy trong phân trang (mặc định: 1) |
| `limit` | `integer` | Tùy chọn | `10` | Số lượng bản ghi tối đa trên một trang (mặc định: 10) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh mục học phần gốc

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh mục học phần gốc",
  "data": [
    {
      "maHP": "INT1405",
      "tenHP": "Kỹ nghệ phần mềm",
      "soTinChi": 3
    },
    {
      "maHP": "INT1416",
      "tenHP": "Kiến trúc phần mềm",
      "soTinChi": 3
    }
  ],
  "meta": {
    "page": 1,
    "pageSize": 10,
    "totalItems": 45,
    "totalPages": 5
  }
}
```

---

<a id="api-38-post-head-office-courses"></a>
### 39. [POST] `/head-office/courses`
**Tên chức năng:** Thêm mới một học phần gốc vào chương trình khung

- **Phân hệ (Tag):** `8. Phân hệ Trưởng phòng Đào tạo (Head Office)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/head-office/courses`

#### 1. Dữ liệu đầu vào (Input)

**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `maHP` | `string` | **Bắt buộc** | Mã định danh học phần gốc theo chương trình đào tạo |
| `tenHP` | `string` | **Bắt buộc** | Tên đầy đủ của học phần đào tạo |
| `soTinChi` | `integer` | **Bắt buộc** | Số lượng tín chỉ quy định của học phần |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "maHP": "INT1405",
  "tenHP": "Kỹ nghệ phần mềm",
  "soTinChi": 3
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `201` - Tạo học phần thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 201,
  "message": "Tạo học phần thành công",
  "data": null
}
```

---
<a id="api-40-put-head-office-courses"></a>
### 40. [PUT] `/head-office/courses/{maHP}`
**Tên chức năng:** Cập nhật thông tin một học phần gốc

- **Phân hệ (Tag):** `8. Phân hệ Trưởng phòng Đào tạo (Head Office)`
- **Phương thức HTTP:** `PUT`
- **Đường dẫn endpoint:** `/head-office/courses/{maHP}`

#### 1. Dữ liệu đầu vào (Input)

path param (maHP : string)
{
  "tenHP": "Kỹ nghệ phần mềm",
  "soTinChi": 3
}
#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `201` - chỉnh sửa học phần thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 201,
  "message": "chỉnh sửa học phần thành công",
  "data": null
}
```

---

<a id="api-41-get-head-office-semesters"></a>
### 41. [GET] `/head-office/semesters`
**Tên chức năng:** Lấy toàn bộ danh sách các học kỳ toàn trường

- **Phân hệ (Tag):** `8. Phân hệ Trưởng phòng Đào tạo (Head Office)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/head-office/semesters`

#### 1. Dữ liệu đầu vào (Input)

*Endpoint không yêu cầu tham số đường dẫn, truy vấn hoặc Request Body.*

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Toàn bộ danh sách học kỳ

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Toàn bộ danh sách học kỳ",
  "data": [
    {
      "maHocKy": "2025_2026_HK1",
      "tenHocKy": "Học kỳ 1 (2025 - 2026)",
      "thoiGianBD": "2025-09-01",
      "thoiGianKT": "2026-01-15"
    },
    {
      "maHocKy": "2024_2025_HK2",
      "tenHocKy": "Học kỳ 2 (2024 - 2025)",
      "thoiGianBD": "2025-09-01",
      "thoiGianKT": "2026-01-15"
    }
  ],
  "meta": {
    "tongSoHocKy": 8
  }
}
```

---

<a id="api-42-post-head-office-semesters"></a>
### 42. [POST] `/head-office/semesters`
**Tên chức năng:** Tạo mới một học kỳ đào tạo

- **Phân hệ (Tag):** `8. Phân hệ Trưởng phòng Đào tạo (Head Office)`
- **Phương thức HTTP:** `POST`
- **Đường dẫn endpoint:** `/head-office/semesters`

#### 1. Dữ liệu đầu vào (Input)

**Dữ liệu gửi lên (Request Body Payload):**

- **Định dạng Content-Type:** `application/json`

*Bảng mô tả chi tiết các thuộc tính Request Body:*
| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Ý nghĩa / Ràng buộc |
| :--- | :--- | :---: | :--- |
| `maHocKy` | `string` | **Bắt buộc** | Mã định danh học kỳ đào tạo |
| `tenHocKy` | `string` | **Bắt buộc** | Tên hiển thị đầy đủ của học kỳ [Giá trị hợp lệ: `HOCKY1`, `HOCKY2`, `HOCKYHE`] |
| `thoiGianBD` | `string` (`date`) | **Bắt buộc** | Thời gian bắt đầu (ngày hoặc thời điểm) |
| `thoiGianKT` | `string` (`date`) | **Bắt buộc** | Thời gian kết thúc (ngày hoặc thời điểm) |

*Ví dụ JSON Request Body gửi lên:*
```json
{
  "maHocKy": "2025_2026_HK1",
  "tenHocKy": "Học kỳ 1 (2025 - 2026)",
  "thoiGianBD": "2025-09-01",
  "thoiGianKT": "2026-01-15"
}
```

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `201` - Tạo học kỳ thành công

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 201,
  "message": "Tạo học kỳ thành công",
  "data": null
}
```

---

<a id="api-43-get-head-office-lecturers"></a>
### 43. [GET] `/head-office/lecturers`
**Tên chức năng:** Tra cứu danh sách giảng viên toàn trường (Có meta phân trang)

- **Phân hệ (Tag):** `8. Phân hệ Trưởng phòng Đào tạo (Head Office)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/head-office/lecturers`

#### 1. Dữ liệu đầu vào (Input)

**Tham số truy vấn (Query Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mặc định | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `khoa` | `string` | Tùy chọn | - | Tên khoa/viện đào tạo cần lọc (ví dụ: `Công nghệ thông tin 1`) |
| `page` | `integer` | Tùy chọn | `1` | Số thứ tự trang cần lấy trong phân trang (mặc định: 1) |
| `limit` | `integer` | Tùy chọn | `10` | Số lượng bản ghi tối đa trên một trang (mặc định: 10) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách giảng viên kèm meta

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách giảng viên kèm meta",
  "data": [
    {
      "maNV": "GV012",
      "hoTen": "Nguyễn Văn An",
      "hocVi": "Tiến sĩ",
      "khoa": "Công nghệ thông tin 1",
      "email": "sinhvien01@ptit.edu.vn",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg",
      "soLopPhuTrach": 3
    },
    {
      "maNV": "GV015",
      "hoTen": "PGS.TS Hoàng Văn Quý",
      "hocVi": "Tiến sĩ",
      "khoa": "Công nghệ thông tin 1",
      "email": "sinhvien01@ptit.edu.vn",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg",
      "soLopPhuTrach": 3
    }
  ],
  "meta": {
    "page": 1,
    "pageSize": 10,
    "totalItems": 45,
    "totalPages": 5
  }
}
```

---

<a id="api-44-get-head-office-students"></a>
### 44. [GET] `/head-office/students`
**Tên chức năng:** Quản lý tra cứu danh sách sinh viên toàn trường (Có meta phân trang)

- **Phân hệ (Tag):** `8. Phân hệ Trưởng phòng Đào tạo (Head Office)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/head-office/students`

#### 1. Dữ liệu đầu vào (Input)

**Tham số truy vấn (Query Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mặc định | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `nienKhoa` | `string` | Tùy chọn | - | Niên khóa đào tạo của sinh viên (ví dụ: `2021-2025`) |
| `khoa` | `string` | Tùy chọn | - | Tên khoa/viện đào tạo cần lọc (ví dụ: `Công nghệ thông tin 1`) |
| `page` | `integer` | Tùy chọn | `1` | Số thứ tự trang cần lấy trong phân trang (mặc định: 1) |
| `limit` | `integer` | Tùy chọn | `10` | Số lượng bản ghi tối đa trên một trang (mặc định: 10) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Danh sách sinh viên kèm meta

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Danh sách sinh viên kèm meta",
  "data": [
    {
      "maSV": "B21DCCN001",
      "hoTen": "Nguyễn Văn An",
      "gioiTinh": "Nam",
      "nienKhoa": "2021-2025",
      "khoa": "Công nghệ thông tin 1",
      "chuyenNganh": "Kỹ nghệ phần mềm",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg"
    },
    {
      "maSV": "B21DCCN002",
      "hoTen": "Lê Thị Cẩm Nhung",
      "gioiTinh": "Nam",
      "nienKhoa": "2021-2025",
      "khoa": "Công nghệ thông tin 1",
      "chuyenNganh": "Kỹ nghệ phần mềm",
      "anhThe": "https://ptit.edu.vn/storage/avatars/B21DCCN001.jpg"
    }
  ],
  "meta": {
    "page": 1,
    "pageSize": 10,
    "totalItems": 45,
    "totalPages": 5
  }
}
```

---

<a id="api-45-get-head-office-statistics"></a>
### 45. [GET] `/head-office/statistics`
**Tên chức năng:** Thống kê số liệu sinh viên và học vụ toàn trường (Có meta báo cáo)

- **Phân hệ (Tag):** `8. Phân hệ Trưởng phòng Đào tạo (Head Office)`
- **Phương thức HTTP:** `GET`
- **Đường dẫn endpoint:** `/head-office/statistics`

#### 1. Dữ liệu đầu vào (Input)

**Tham số truy vấn (Query Parameters):**

| Tham số | Kiểu dữ liệu | Bắt buộc | Mặc định | Mô tả chức năng |
| :--- | :--- | :---: | :--- | :--- |
| `maHocKy` | `string` | Tùy chọn | - | Mã định danh học kỳ (ví dụ: `2025_2026_HK1`) |

#### 2. Dữ liệu đầu ra (Output)

##### Mã phản hồi HTTP `200` - Dữ liệu thống kê tổng hợp toàn trường

- **Định dạng Content-Type:** `application/json`

*Ví dụ JSON Response trả về:*
```json
{
  "status": 200,
  "message": "Dữ liệu thống kê tổng hợp toàn trường",
  "data": {
    "tongSinhVienToanTruong": 12500,
    "sinhVienTheoKhoa": {
      "Khoa CNTT 1": 3500,
      "Khoa Điện tử 1": 2800,
      "Khoa Viễn thông 1": 2200,
      "Khoa ATTT": 1800,
      "Khoa QTKD 1": 2200
    },
    "sinhVienTheoGioiTinh": {
      "Nam": 8200,
      "Nu": 4300
    },
    "xepLoaiHocLuc": {
      "XuatSac": 420,
      "Gioi": 2150,
      "Kha": 6800,
      "TrungBinh": 2850,
      "Yeu": 280
    },
    "soLuongDinhChiThi": 35,
    "soLuongRotMon": 310
  },
  "meta": {
    "maHocKy": "2025_2026_HK1",
    "ngayXuatThongKe": "2026-10-05T08:00:00Z",
    "nguoiTaoThongKe": "Trưởng phòng Đào tạo - PGS.TS Trần Văn Hùng"
  }
}
```

---

## PHỤ LỤC: CÁC SCHEMA DÙNG CHUNG (Reusable Components)

### Component Schema: `StandardMessage`

| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Chú thích |
| :--- | :--- | :---: | :--- |
| `status` | `integer` | Tùy chọn | - |
| `message` | `string` | Tùy chọn | - |
| `data` | `string` | Tùy chọn | - |

*Cấu trúc JSON mẫu:*
```json
{
  "status": 200,
  "message": "Thao tác thành công",
  "data": null
}
```

### Component Schema: `StandardError`

| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Chú thích |
| :--- | :--- | :---: | :--- |
| `status` | `integer` | Tùy chọn | - |
| `message` | `string` | Tùy chọn | - |
| `data` | `string` | Tùy chọn | - |

*Cấu trúc JSON mẫu:*
```json
{
  "status": 200,
  "message": "Thao tác thành công",
  "data": null
}
```

### Component Schema: `LopHocPhanItem`

| Thuộc tính | Kiểu dữ liệu | Bắt buộc | Chú thích |
| :--- | :--- | :---: | :--- |
| `maLHP` | `string` | Tùy chọn | Mã định danh lớp học phần mở trong kỳ |
| `tenLHP` | `string` | Tùy chọn | Tên định danh lớp học phần cụ thể |
| `maHP` | `string` | Tùy chọn | Mã định danh học phần gốc theo chương trình đào tạo |
| `tenHP` | `string` | Tùy chọn | Tên đầy đủ của học phần đào tạo |
| `soTinChi` | `integer` | Tùy chọn | Số lượng tín chỉ quy định của học phần |
| `tenGiangVien` | `string` | Tùy chọn | Họ tên giảng viên phụ trách giảng dạy lớp |
| `thu` | `integer` | Tùy chọn | Thứ trong tuần diễn ra buổi học (2: Thứ Hai -> 7: Thứ Bảy, 8: CN) |
| `tietBatDau` | `integer` | Tùy chọn | Tiết bắt đầu của ca học |
| `soTiet` | `integer` | Tùy chọn | Tổng số tiết học liên tiếp của buổi học |
| `phongHoc` | `string` | Tùy chọn | Mã phòng học diễn ra buổi giảng dạy |
| `siSoHienTai` | `integer` | Tùy chọn | Số lượng sinh viên đã đăng ký vào lớp hiện tại |
| `siSoToiDa` | `integer` | Tùy chọn | Sĩ số tối đa cho phép tiếp nhận đăng ký của lớp |
| `thoiGianBDDK` | `string` (`date-time`) | Tùy chọn | Thời điểm bắt đầu mở cổng đăng ký học phần |
| `thoiGianKTDK` | `string` (`date-time`) | Tùy chọn | Thời điểm đóng cổng đăng ký học phần |
| `trangThai` | `string` | Tùy chọn | Trạng thái hoạt động / xử lý của bản ghi |

*Cấu trúc JSON mẫu:*
```json
{
  "maLHP": "INT1405_01",
  "tenLHP": "Kỹ nghệ phần mềm - Lớp 01",
  "maHP": "INT1405",
  "tenHP": "Kỹ nghệ phần mềm",
  "soTinChi": 3,
  "tenGiangVien": "TS. Trần Thị Bình",
  "thu": 2,
  "tietBatDau": 1,
  "soTiet": 3,
  "phongHoc": "A2-301",
  "siSoHienTai": 58,
  "siSoToiDa": 60,
  "thoiGianBDDK": "2025-08-15T08:00:00Z",
  "thoiGianKTDK": "2025-08-25T17:00:00Z",
  "trangThai": "DANG_MO"
}
```
