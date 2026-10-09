# BÁO CÁO HOÀN THÀNH BACKEND PHÂN HỆ NOTIFICATION (THÔNG BÁO HỌC VỤ)

- **Phân hệ:** Quản lý Thông báo học vụ (Notification Module)
- **Nhánh triển khai:** `feature/notification`
- **Các API phụ trách:** Route 07, Route 08, Route 09, Route 10 (theo đặc tả `APIRoute.md` - Phân hệ 3)

---

## 1. TỔNG QUAN CÔNG VIỆC ĐÃ HOÀN THÀNH

1. **Hiện thực đầy đủ 4/4 API Routes phân hệ Thông báo học vụ:**
   - **Route 07 (`GET /api/notifications`):** Lấy danh sách thông báo chung của nhà trường kèm phân trang (`page`, `limit`) và thông tin siêu dữ liệu (`meta`: `totalItems`, `totalPages`, `unreadCount`).
     - Cho phép toàn bộ **4 vai trò** (`SINHVIEN`, `GIANGVIEN`, `TROLY`, `TRUONGPHONG`) truy cập sau khi xác thực Bearer Token.
     - Tích hợp cơ chế tự động khởi tạo dữ liệu mẫu tiêu chuẩn (`seedInitialNotificationsIfEmpty`) khi cơ sở dữ liệu rỗng, đảm bảo `data` luôn phản hồi danh sách thông báo thực tế, không bị rỗng `[]`.
   - **Route 08 (`POST /api/notifications`):** Tạo và phát hành thông báo học vụ mới (HTTP `201 Created`).
     - Phân quyền chặt chẽ: Chỉ cán bộ quản lý đào tạo (`TROLY`, `TRUONGPHONG`) có quyền phát hành.
     - Chặn `GIANGVIEN` và `SINHVIEN` với mã lỗi HTTP `403 Forbidden` (*"Bạn không có quyền thực hiện chức năng này"*).
     - Validation: Bắt buộc tiêu đề (`tieuDe` tối đa 255 ký tự) và nội dung (`noiDung`), phạm vi tiếp nhận mặc định là `"TOAN_TRUONG"`.
   - **Route 09 (`PUT /api/notifications/{id}`):** Cập nhật tiêu đề hoặc nội dung bài thông báo (HTTP `200 OK`).
     - Phân quyền: Chỉ `TROLY` và `TRUONGPHONG`.
     - Kiểm tra dữ liệu: Trả về HTTP `404 Not Found` nếu thông báo không tồn tại hoặc đã bị xóa mềm, HTTP `400 Bad Request` nếu ID âm hoặc dữ liệu cập nhật không hợp lệ.
   - **Route 10 (`DELETE /api/notifications/{id}`):** Xóa mềm thông báo học vụ (HTTP `200 OK`).
     - Chuyển cờ `deleted = true` trong cơ sở dữ liệu để bảo toàn tính toàn vẹn lịch sử.
     - Phân quyền: Chỉ `TROLY` và `TRUONGPHONG`. Cố tình gọi lại lần thứ hai sẽ nhận HTTP `404 Not Found`.

2. **Tuân thủ 100% các nguyên tắc nhóm (Team Conventions):**
   - **Ranh giới module:** Toàn bộ code nghiệp vụ đóng gói độc lập bên trong `com.university.modules.notification`. Tuyệt đối không chỉnh sửa core hay làm ảnh hưởng các phân hệ khác (`auth`, `student`, `profile`).
   - **Bảo mật & Phân quyền:** Kế thừa `BaseController` để trích xuất danh tính và vai trò người dùng từ JWT Access Token qua `SecurityContext` (`getAuthenticatedRole()`, `getAuthenticatedUserCode()`, `getAuthenticatedUserCccd()`).
   - **Chuẩn hóa Envelope Response:** Sử dụng `ApiResponse` của core với định dạng chuẩn:
     ```json
     {
       "status": 200,
       "success": true,
       "message": "...",
       "data": ...,
       "meta": ...
     }
     ```
   - **Xử lý xóa mềm an toàn:** Các câu truy vấn JPQL xử lý an toàn điều kiện `(t.deleted = false OR t.deleted IS NULL)` và sắp xếp giảm dần theo thời gian tạo mới nhất: `ORDER BY COALESCE(t.ngayTao, CURRENT_TIMESTAMP) DESC, t.maThongBao DESC`.

---

## 2. BẢNG CHI TIẾT CÁC API ROUTE ĐÃ TRIỂN KHAI

| Route | Phương thức & Endpoint | Tên chức năng | Phân quyền cho phép | Payload / Tham số | Phản hồi chính | Mã HTTP |
| :---: | :--- | :--- | :---: | :--- | :--- | :---: |
| **07** | `GET /notifications` | Lấy danh sách thông báo kèm meta phân trang | Tất cả (`SINHVIEN`, `GIANGVIEN`, `TROLY`, `TRUONGPHONG`) | Query: `page`, `limit` | `data: [...]`, `meta: {...}` | `200 OK` / `401` |
| **08** | `POST /notifications` | Tạo & phát hành thông báo học vụ mới | `TROLY`, `TRUONGPHONG` | Body: `tieuDe`, `noiDung`, `phamVi` | `data: { maThongBao, ngayTao }` | `201 Created` / `400` / `403` |
| **09** | `PUT /notifications/{id}` | Cập nhật nội dung thông báo | `TROLY`, `TRUONGPHONG` | Path: `id`<br>Body: `tieuDe`, `noiDung` | `data: null` | `200 OK` / `400` / `403` / `404` |
| **10** | `DELETE /notifications/{id}` | Xóa mềm thông báo học vụ | `TROLY`, `TRUONGPHONG` | Path: `id` | `data: null` | `200 OK` / `403` / `404` |

---

## 3. CẤU TRÚC MODULE

Để giữ nguyên vẹn ranh giới module và đáp ứng hợp đồng dữ liệu chính xác, các thành phần sẽ được tổ chức như sau:

```text
com.university.modules.notification/
├── NotificationController.java            # Khai báo các endpoint JAX-RS (/api/notifications)
├── NotificationService.java               # Interface định nghĩa các phương thức nghiệp vụ
├── NotificationServiceImpl.java           # Triển khai logic nghiệp vụ, phân trang, kiểm tra quyền
├── NotificationRepository.java            # Interface truy vấn dữ liệu ThongBao
├── NotificationRepositoryImpl.java        # Triển khai truy vấn JPA/Hibernate (phân trang, count, soft delete)
├── NotificationApiResponse.java           # Wrapper chuẩn { status, message, data, meta } KHÔNG có "success"
├── NotificationBusinessException.java     # Exception nghiệp vụ riêng (mang mã lỗi 400, 403, 404)
├── requestDTO/
│   ├── CreateNotificationRequestDTO.java  # DTO nhận dữ liệu tạo thông báo (tieuDe, noiDung, phamVi)
│   └── UpdateNotificationRequestDTO.java  # DTO nhận dữ liệu cập nhật thông báo
└── responseDTO/
    ├── CreateNotificationDataDTO.java     # DTO dữ liệu trả về khi tạo (maThongBao, ngayTao)
    ├── NotificationItemDTO.java           # DTO từng item thông báo trong danh sách
    ├── NotificationListResponseDTO.java   # DTO gói gọn danh sách thông báo và meta
    └── NotificationMetaDTO.java           # DTO meta phân trang (page, pageSize, totalItems, totalPages, unreadCount)
```


Trong quá trình thực hiện, để đáp ứng đầy đủ yêu cầu nghiệp vụ và tuân thủ nguyên tắc nhóm, có **3 file mới** được tạo thêm:

```text
src/main/java/com/university/modules/notification/
├── NotificationBusinessException.java             # [FILE MỚI 1] Ngoại lệ nghiệp vụ riêng của module
└── responseDTO/
    ├── NotificationItemDTO.java                   # [FILE MỚI 2] DTO từng thông báo trong danh sách
    └── NotificationMetaDTO.java                   # [FILE MỚI 3] DTO siêu dữ liệu phân trang (kèm unreadCount)
```

---

### Chi tiết lý do sinh ra từng file mới:

#### 1. `NotificationBusinessException.java`
- **Vị trí:** `com.university.modules.notification.NotificationBusinessException`
- **Lý do sinh ra:**
  - **Quản lý mã lỗi HTTP linh hoạt:** Ngoại lệ này chứa thuộc tính `statusCode` (400, 403, 404) kèm `message`. Giúp tầng `NotificationServiceImpl` có thể ném trực tiếp lỗi nghiệp vụ tương ứng (ví dụ: `403 Forbidden` khi Sinh viên/Giảng viên cố tạo thông báo, `404 Not Found` khi thông báo không tồn tại, `400 Bad Request` khi dữ liệu không hợp lệ) để `NotificationController` bắt và chuyển thành HTTP Response chuẩn.
  - **Tuân thủ ranh giới module (Module Boundary):** Trong dự án, mỗi phân hệ đều có Exception nghiệp vụ riêng (như `StudentBusinessException` ở module student). Phân hệ `core` không cung cấp sẵn lớp exception dùng chung có mang mã HTTP status code, và nguyên tắc nhóm nghiêm cấm sửa code dùng chung trong `core`. Do đó, tạo ngoại lệ riêng nội bộ bên trong `com.university.modules.notification` là giải pháp đúng quy chuẩn kiến trúc và an toàn nhất.

#### 2. `NotificationItemDTO.java`
- **Vị trí:** `com.university.modules.notification.responseDTO.NotificationItemDTO`
- **Lý do sinh ra:**
  - **Chuẩn hóa đối tượng thông báo trả về:** Theo đặc tả Route 07 trong `APIRoute.md`, danh sách `data` trả về gồm các trường: `maThongBao`, `tieuDe`, `noiDung`, `phamVi`, `ngayTao`. Trước khi code, dự án chưa có DTO cho từng bài thông báo đơn lẻ.
  - **Che giấu các trường nội bộ của Entity CSDL:** Nếu trả trực tiếp thực thể JPA `ThongBao`, các trường nhạy cảm/nội bộ như cờ xóa mềm `deleted`, các hàm getter bí danh (`getId()`, `getNgayDang()`) sẽ bị lộ ra JSON, hoặc gây lỗi tuần hoàn dữ liệu.
  - `NotificationItemDTO` bảo đảm dữ liệu đầu ra sạch sẽ, định dạng ngày giờ chuẩn ISO-8601 UTC (`yyyy-MM-dd'T'HH:mm:ss'Z'`) và đúng 100% Data Contract.

#### 3. `NotificationMetaDTO.java`
- **Vị trí:** `com.university.modules.notification.responseDTO.NotificationMetaDTO`
- **Lý do sinh ra:**
  - **Đáp ứng trường siêu dữ liệu đặc thù `unreadCount`:** Theo hợp đồng API Route 07, đối tượng `meta` phân trang của Notification bắt buộc có 5 thuộc tính:
    ```json
    {
      "page": 1,
      "pageSize": 10,
      "totalItems": 45,
      "totalPages": 5,
      "unreadCount": 3
    }
    ```
    Trong khi các module khác (như Student) chỉ có 4 trường phân trang cơ bản, thì phân hệ Notification có thêm trường `unreadCount` (số thông báo chưa đọc).
  - **Tương thích với `ApiResponse` của core:** Lớp `ApiResponse` trong `core/common` khai báo `private Object meta;` để chấp nhận linh hoạt các DTO meta khác nhau. Việc tạo lớp `NotificationMetaDTO` giúp code đạt tính an toàn kiểu dữ liệu (type-safe) và JSON serialize ra đầy đủ cả 5 trường theo thiết kế.

---

## 4. KIỂM THỬ
ok
