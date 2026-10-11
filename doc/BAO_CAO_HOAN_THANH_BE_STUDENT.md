# BÁO CÁO HOÀN THÀNH BACKEND PHÂN HỆ SINH VIÊN (STUDENT MODULE)

- **Phân hệ:** Sinh viên (Student Module)
- **Nhánh triển khai:** `feature/student`
- **Các API phụ trách:** Route 11 đến Route 17 (theo đặc tả `APIRoute.md`)
- **Ngày hoàn thành:** 08/10/2026
- **Trạng thái:** Hoàn thành 100% (Đã kiểm thử đạt 14/14 test cases)

---

## 1. TỔNG QUAN CÔNG VIỆC ĐÃ HOÀN THÀNH

1. **Hiện thực đầy đủ 7/7 API Routes phân hệ Sinh viên:**
   - Hoàn thiện tầng Controller (`StudentController.java`).
   - Hoàn thiện tầng Service Interface & Implementation (`StudentService.java`, `StudentServiceImpl.java`).
   - Hoàn thiện tầng Repository truy vấn dữ liệu JPA/Hibernate thực tế qua Database (`StudentRepository.java`, `StudentRepositoryImpl.java`).
2. **Xử lý toàn diện Logic nghiệp vụ (Business Logic):**
   - Kiểm tra hạn mức tín chỉ tối đa trong kỳ (tối đa 24 tín chỉ).
   - Kiểm tra thời gian mở cổng đăng ký (`thoiGianBDDK`, `thoiGianKTDK`).
   - Kiểm tra sĩ số lớp (`siSoHienTai` < `siSoToiDa`).
   - Tự động phát hiện và ngăn chặn **trùng thời khóa biểu (lịch học)** với lớp đã đăng ký (trả về mã HTTP `409 Conflict`).
   - Hủy đăng ký lớp học phần và hoàn trả sĩ số lớp an toàn.
   - Gửi yêu cầu/kiến nghị mở thêm lớp học phần mới.
3. **Refactor cấu trúc DTO theo chuẩn thiết kế gọn gàng của team:**
   - Xóa bỏ các thư mục rải rác `requestDTO/` và `responseDTO/`.
   - Gom toàn bộ Request DTOs vào **1 file duy nhất:** `StudentRequest.java`.
   - Gom toàn bộ Response DTOs vào **1 file duy nhất:** `StudentResponse.java`.
   - Giữ nguyên tương thích ngược các class alias (`*DTO`).

---

## 2. BẢNG CHI TIẾT 7 API PHÂN HỆ SINH VIÊN

| Route | Method & Endpoint | Mô tả chức năng | Request Body / Param | Response Model | HTTP Code |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **11** | `GET /student/grades` | Xem bảng điểm chi tiết theo kỳ & meta tích lũy | `@QueryParam maHocKy` | `StudentResponse.StudentGradeResponse` | `200 OK` |
| **12** | `GET /student/schedule` | Lấy thời khóa biểu dạng ma trận tuần | `@QueryParam maHocKy` | `StudentResponse.StudentScheduleResponse` | `200 OK` |
| **13** | `GET /student/registration/open-classes` | Danh sách lớp mở đăng ký (có phân trang) | `@QueryParam keyword, page, limit` | `StudentResponse.OpenedEnrollingClass` | `200 OK` |
| **14** | `GET /student/registration/registered-classes` | Danh sách lớp đã ghi danh & tổng tín chỉ | Không (lấy theo user đăng nhập) | `StudentResponse.RegisteredClassResponse` | `200 OK` |
| **15** | `POST /student/registration/enroll` | Đăng ký lớp học phần (Check trùng lịch & sĩ số) | `StudentRequest.EnrollCourseRequest` | `StudentResponse.EnrollCourseResponse` | `201 Created` / `409` |
| **16** | `DELETE /student/registration/cancel/{maHoSo}` | Hủy đăng ký lớp học phần | `@PathParam maHoSo` | `StudentResponse.CancelEnrollmentResponse` | `200 OK` |
| **17** | `POST /student/registration/open-request` | Gửi đơn kiến nghị mở lớp học phần | `StudentRequest.OpenClassRequestRequest` | `StudentResponse.OpenClassRequestResponse` | `201 Created` |

---

## 3. CẤU TRÚC GÓI MÃ NGUỒN SAU REFACTOR

```text
com.university.modules.student/
├── StudentController.java         # Điều hướng REST API endpoints
├── StudentService.java            # Khai báo các interface nghiệp vụ
├── StudentServiceImpl.java        # Xử lý logic nghiệp vụ và ràng buộc dữ liệu
├── StudentRepository.java         # Khai báo interface truy vấn dữ liệu
├── StudentRepositoryImpl.java     # Truy vấn Hibernate/JPA vào PostgreSQL
├── StudentBusinessException.java  # Xử lý exception kèm HTTP status code
├── StudentRequest.java            # File gom toàn bộ Request DTO
└── StudentResponse.java           # File gom toàn bộ Response DTO
```

---

## 4. KẾT QUẢ KIỂM THỬ TỰ ĐỘNG (UNIT & INTEGRATION TESTS)

Đã xây dựng và thực thi 2 bộ test suite tự động với JUnit 5:

1. **`StudentServiceAndControllerTest`**: **7/7 PASSED (100%)**
   - `testGetGrades`: Đạt chuẩn bảng điểm và GPA.
   - `testGetSchedule`: Đạt chuẩn ma trận tuần thời khóa biểu.
   - `testGetOpenClasses`: Đạt chuẩn tra cứu và phân trang lớp mở.
   - `testGetRegisteredClasses`: Đạt chuẩn danh sách lớp đã ghi danh.
   - `testEnrollCourseSuccessAndConflict`: Đạt chuẩn đăng ký thành công (201) và chặn trùng lịch học (409 Conflict).
   - `testCancelEnrollment`: Đạt chuẩn hủy lớp và cập nhật sĩ số.
   - `testRequestOpenCourse`: Đạt chuẩn gửi phiếu đề xuất mở lớp.

2. **`StudentJsonPayloadTest`**: **7/7 PASSED (100%)**
   - Kiểm tra chuỗi JSON Envelope chuẩn: `{ "status", "success", "message", "data", "meta" }` khớp 100% với định dạng trong `chuan_hoa_goi_json` và Swagger API.

**Tổng kết:** **14/14 test cases PASSED (Tỷ lệ 100%)**.
Biên dịch dự án sạch sẽ: **0 Errors, 0 Warnings**.
