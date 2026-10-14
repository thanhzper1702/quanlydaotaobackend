# HƯỚNG DẪN DEPLOY BACKEND LÊN RENDER (DOCKER)

Hệ thống Backend được phát triển trên nền tảng **Jakarta EE 10 / Servlet 6.0 / JAX-RS (Jersey) / JPA (Hibernate)** và chạy trên máy chủ ứng dụng **Apache Tomcat 10.1 (Java 17)**. 

Toàn bộ cấu hình đóng gói Docker và kịch bản deploy tự động đã được thiết lập sẵn sàng.

---

## 1. CÁC TẬP TIN CẤU HÌNH ĐÃ TẠO

| Tên file | Mục đích |
| :--- | :--- |
| `Dockerfile` | Multi-stage build: Stage 1 build WAR bằng Maven 3.9 + OpenJDK 17; Stage 2 triển khai lên Apache Tomcat 10.1 dưới dạng `ROOT.war`. |
| `entrypoint.sh` | Kịch bản khởi động container tự động đọc biến môi trường `$PORT` do Render cấp phát động và cấu hình vào `server.xml`. |
| `.dockerignore` | Loại bỏ các thư mục `target/`, `.git/`, `.idea/`, `.env` để tối ưu dung lượng và tăng tốc build trên đám mây. |
| `render.yaml` | Bản thiết kế hạ tầng mẫu (Render Blueprint) giúp triển khai tự động 1-click không cần cấu hình thủ công. |

---

## 2. CÁC BƯỚC TRIỂN KHAI LÊN RENDER

### CÁCH 1: TRIỂN KHAI BẰNG RENDER BLUEPRINT (KHUYÊN DÙNG - TỰ ĐỘNG)

1. **Đẩy mã nguồn lên GitHub:**
   ```bash
   git add .
   git commit -m "Add Dockerfile and Render deployment configuration"
   git push origin main
   ```

2. **Truy cập Render Dashboard:**
   - Đăng nhập vào [dashboard.render.com](https://dashboard.render.com).
   - Nhấn nút **New +** ở góc trên bên phải -> Chọn **Blueprint**.

3. **Kết nối Repository:**
   - Chọn kho lưu trữ GitHub của bạn.
   - Render sẽ tự động nhận diện file `render.yaml` và liệt kê dịch vụ `quanlydaotaobackend`.

4. **Khai báo biến môi trường (Environment Variables):**
   Render sẽ yêu cầu nhập các giá trị còn thiếu:
   - `DB_URL`: JDBC URL kết nối Neon PostgreSQL (Ví dụ: `jdbc:postgresql://ep-xxxx.neon.tech/QuanLyDaoTaoDB?sslmode=require`)
   - `DB_USER`: Username database (Ví dụ: `neondb_owner`)
   - `DB_PASSWORD`: Password database của bạn
   *(Các biến `JWT_SECRET`, `COOKIE_SECURE`, `DB_DRIVER` đã được `render.yaml` tự động sinh hoặc gán giá trị mặc định).*

5. Nhấn **Apply** để Render bắt đầu build Docker image và khởi chạy.

---

### CÁCH 2: TRIỂN KHAI THỦ CÔNG (MANUAL WEB SERVICE)

Nếu bạn muốn tạo thủ công trực tiếp từ giao diện Render:

1. Vào **Dashboard** -> **New +** -> **Web Service**.
2. Chọn kho lưu trữ Git chứa backend.
3. Cấu hình các thông số cơ bản:
   - **Name:** `quanlydaotaobackend` (hoặc tên bạn chọn).
   - **Region:** `Singapore` (tối ưu tốc độ truy cập từ Việt Nam).
   - **Root Directory:** Nếu kho lưu trữ chứa cả thư mục cha `project`, điền `quanlydaotaobackend`. Nếu repository chỉ chứa riêng backend, để trống.
   - **Language/Runtime:** Chọn **Docker**.
   - **Instance Type:** Chọn gói **Free** (512 MB RAM, 0.1 CPU).
4. Mở rộng phần **Advanced**:
   - **Health Check Path:** Điền `/api/common/current-semester`.
5. Trong mục **Environment Variables**, thêm các biến sau:
   - `DB_URL`: `jdbc:postgresql://<neon-host>:5432/<db_name>?sslmode=require`
   - `DB_USER`: `<db_username>`
   - `DB_PASSWORD`: `<db_password>`
   - `DB_DRIVER`: `org.postgresql.Driver`
   - `COOKIE_SECURE`: `true`
   - `JWT_SECRET`: `<một_chuỗi_bí_mật_ngẫu_nhiên_bất_kỳ>`
   - `HIBERNATE_SHOW_SQL`: `false`
   - `DB_POOL_MAX_SIZE`: `5`
   - `DB_POOL_MIN_IDLE`: `1`
6. Nhấn **Create Web Service**.

---

## 3. CẬP NHẬT ĐỊA CHỈ API Ở FRONTEND

Sau khi Render deploy thành công, bạn sẽ nhận được một đường link HTTPS công khai, ví dụ:
`https://quanlydaotaobackend.onrender.com`

File cấu hình [frontend_cnpm/js/config.js](file:///d:/CNPM/project/frontend_cnpm/js/config.js) đã được thiết lập để tự động trỏ:
- Khi chạy cục bộ trên máy tính (`localhost`): Trỏ về `http://localhost:8080/api`.
- Khi triển khai online: Trỏ về URL backend trên Render.

Bạn chỉ cần mở [frontend_cnpm/js/config.js](file:///d:/CNPM/project/frontend_cnpm/js/config.js) và cập nhật đường dẫn chính xác theo domain của Render:
```javascript
const APP_CONFIG = {
  API_BASE_URL: (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
    ? 'http://localhost:8080/api'
    : 'https://<tên-app-của-bạn>.onrender.com/api',
  STORAGE_TOKEN_KEY: 'auth_access_token',
  STORAGE_USER_KEY: 'auth_current_user',
  STORAGE_ROLE_KEY: 'auth_user_role'
};
```

---

## 4. KIỂM TRA SAU KHI DEPLOY THÀNH CÔNG

Bạn có thể kiểm tra trực tiếp qua trình duyệt hoặc công cụ Curl:
- **Kiểm tra sức khỏe hệ thống:**
  `GET https://<tên-app-của-bạn>.onrender.com/api/common/current-semester`
  *(Kết quả trả về JSON HTTP 200 kèm thông tin học kỳ hiện hành chứng tỏ backend và cơ sở dữ liệu đã kết nối hoàn hảo).*
- **Kiểm tra đăng nhập:**
  `POST https://<tên-app-của-bạn>.onrender.com/api/auth/login`
