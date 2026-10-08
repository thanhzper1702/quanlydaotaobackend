# Nhật ký hoàn thiện Backend Role Trưởng phòng đào tạo

## 1. Thông tin và phạm vi

- Dự án: `quanlydaotaobackend`.
- Role: `TRUONGPHONG`.
- Module được triển khai: `src/main/java/com/university/modules/headoffice`.
- File chuẩn hóa response được tham khảo: `chuan_hoa_goi_json.yaml`.
- Chuẩn response giữ nguyên: `ApiResponse` với các trường `status`, `success`, `message`, `data`, `meta` và `errors` khi cần.
- Không sửa code ngoài module `headoffice`.
- Không sửa `ApiResponse`, `PagedResponse`, `BaseController`, entity, util, filter, database hoặc các module chức năng khác.
- Nhánh Git phát hiện trong quá trình làm việc: `develop`.

## 2. Tình trạng ban đầu

Module Head Office ban đầu đã có controller, service, repository và nhiều DTO rời, nhưng phần lớn nghiệp vụ chưa được nối với nhau:

- Controller có route nhưng nhiều method trả về `null`.
- Service chưa gọi repository và chưa có validation đầy đủ.
- DTO request/response gần như rỗng hoặc bị chia thành quá nhiều file nhỏ.
- Chưa có response thống nhất theo chuẩn JSON của project.
- Chưa có xử lý đầy đủ cho phân trang, lỗi `400/404/409`, transaction và export.
- Chưa có nghiệp vụ giám sát lớp học phần, bảng điểm và khóa điểm.

Mục tiêu sau khi hoàn thiện là role `TRUONGPHONG` có thể quản lý học phần, học kỳ, tra cứu giảng viên/sinh viên, giám sát lớp học phần, xem bảng điểm, khóa điểm, xem thống kê và xuất Excel.

## 3. Chuẩn hóa JSON

File `chuan_hoa_goi_json.yaml` mô tả response envelope chuẩn của toàn hệ thống. Module Head Office sử dụng chuẩn này thông qua `ApiResponse` dùng chung.

Các response thành công có dạng tổng quát:

```json
{
  "status": 200,
  "success": true,
  "message": "Mô tả kết quả",
  "data": {}
}
```

Các response danh sách đặt thông tin phân trang trong `data`, gồm các trường:

```text
items
page
size
totalElements
totalPages
```

Các response lỗi có dạng:

```json
{
  "status": 400,
  "success": false,
  "message": "Mô tả lỗi",
  "data": null
}
```

Việc gom DTO không thay đổi tên field JSON hoặc cấu trúc response. Chỉ thay đổi cách tổ chức class Java.

## 4. Dọn dẹp và gom DTO

### 4.1. File request sau khi gom

#### `src/main/java/com/university/modules/headoffice/HeadOfficeRequestDTO.java`

Đây là file request duy nhất trong module. Các class request được định nghĩa dạng `public static class` bên trong `HeadOfficeRequestDTO`:

- `CreateCourseRequestDTO`
  - `maHP`.
  - `tenHP`.
  - `soTinChi`.
- `CreateSemesterRequestDTO`
  - `maHocKy`.
  - `tenHocKy`.
  - `thoiGianBD`.
  - `thoiGianKT`.
- `UpdateCourseRequestDTO`
  - `tenHP`.
  - `soTinChi`.
- `UpdateGradeLockRequestDTO`
  - `khoaDiem`.

Các request được sử dụng trong code bằng dạng:

```java
HeadOfficeRequestDTO.CreateCourseRequestDTO
```

hoặc import nested class:

```java
import com.university.modules.headoffice.HeadOfficeRequestDTO.*;
```

### 4.2. File response sau khi gom

#### `src/main/java/com/university/modules/headoffice/HeadOfficeResponseDTO.java`

Đây là file response duy nhất trong module. Các class response được định nghĩa bên trong `HeadOfficeResponseDTO`:

- `CreateCourseResponseDTO`.
- `UpdateCourseResponseDTO`.
- `CreateSemesterResponseDTO`.
- `HeadOfficeCourseResponseDTO`.
- `HeadOfficeSemesterResponseDTO`.
- `HeadOfficeLecturerResponseDTO`.
- `HeadOfficeStudentResponseDTO`.
- `HeadOfficeClassResponseDTO`.
- `HeadOfficeClassGradeResponseDTO`.
- `HeadOfficeStatisticsResponseDTO`.

Các item lồng bên trong response cũng được giữ nguyên:

- `HeadOfficeCourseResponseDTO.CourseItem`.
- `HeadOfficeSemesterResponseDTO.SemesterItem`.
- `HeadOfficeLecturerResponseDTO.LecturerItem`.
- `HeadOfficeStudentResponseDTO.StudentItem`.
- `HeadOfficeClassResponseDTO.ClassItem`.
- `HeadOfficeClassGradeResponseDTO.GradeItem`.


### 4.3. File cập nhật import sau khi gom DTO

#### `src/main/java/com/university/modules/headoffice/HeadOfficeController.java`

- Import request từ `HeadOfficeRequestDTO`.
- Import response từ `HeadOfficeResponseDTO`.
- Không còn import package `requestDTO.*` hoặc `responseDTO.*` cũ.

#### `src/main/java/com/university/modules/headoffice/HeadOfficeService.java`

- Sử dụng các nested request/response class từ hai file DTO mới.
- Giữ nguyên tên method và kiểu dữ liệu nghiệp vụ.

#### `src/main/java/com/university/modules/headoffice/HeadOfficeServiceImpl.java`

- Sử dụng các nested request/response class từ hai file DTO mới.
- Không thay đổi JSON field hoặc nghiệp vụ mapping.

## 5. Vị trí chi tiết các nghiệp vụ trong code

### 5.1. Controller

#### File: `src/main/java/com/university/modules/headoffice/HeadOfficeController.java`

- Khai báo resource tại class `HeadOfficeController`, `@Path("/head-office")`.
- Nhận JSON bằng `@Consumes(MediaType.APPLICATION_JSON)`.
- Trả JSON mặc định bằng `@Produces(MediaType.APPLICATION_JSON)`.
- Khởi tạo service tại constructor mặc định và hỗ trợ inject service qua constructor thứ hai.

#### Quản lý học phần

Các method nằm ở phần đầu class, sau constructor:

- `getCourses(...)`
  - `GET /api/head-office/courses`.
  - Nhận `page`, `limit`.
  - Gọi `headOfficeService.getCourses(page, limit)`.
- `createCourse(...)`
  - `POST /api/head-office/courses`.
  - Trả HTTP `201` khi tạo thành công.
- `updateCourse(...)`
  - `PUT /api/head-office/courses/{maHP}`.
  - Gọi service cập nhật tên và số tín chỉ.
- `deleteCourse(...)`
  - `DELETE /api/head-office/courses/{maHP}`.
  - Gọi xóa mềm có điều kiện.

#### Quản lý học kỳ

- `getSemesters(...)`
  - `GET /api/head-office/semesters`.
- `createSemester(...)`
  - `POST /api/head-office/semesters`.
  - Trả HTTP `201` khi tạo thành công.

#### Giám sát lớp học phần và bảng điểm

- `getClasses(...)`
  - `GET /api/head-office/semesters/{maHocKy}/classes`.
  - Nhận phân trang và lọc theo mã học kỳ.
- `getClassGrades(...)`
  - `GET /api/head-office/classes/{maLHP}/students`.
  - Trả danh sách sinh viên và bảng điểm của lớp.
- `exportClassGrades(...)`
  - `GET /api/head-office/classes/{maLHP}/students/export`.
  - Trả file `headoffice-class-grades.xlsx`.
- `updateGradeLock(...)`
  - `PATCH /api/head-office/classes/{maLHP}/grade-lock`.
  - Nhận JSON `{ "khoaDiem": true }` hoặc `false`.

#### Tra cứu giảng viên và sinh viên

- `getLecturers(...)`
  - `GET /api/head-office/lecturers`.
  - Lọc theo `khoa` và phân trang.
- `exportLecturers(...)`
  - `GET /api/head-office/lecturers/export`.
  - Trả file `headoffice-lecturers.xlsx`.
- `getStudents(...)`
  - `GET /api/head-office/students`.
  - Lọc theo `nienKhoa`, `khoa` và phân trang.
- `exportStudents(...)`
  - `GET /api/head-office/students/export`.
  - Trả file `headoffice-students.xlsx`.

#### Thống kê

- `getStatistics(...)`
  - `GET /api/head-office/statistics`.
  - Có thể truyền `maHocKy` để thống kê theo học kỳ.
- `exportStatistics(...)`
  - `GET /api/head-office/statistics/export`.
  - Trả file `headoffice-statistics.xlsx`.

#### Xử lý lỗi tập trung

- Method private `execute(...)` nằm cuối `HeadOfficeController`.
- `IllegalArgumentException` chuyển thành `400`.
- `NoSuchElementException` chuyển thành `404`.
- `IllegalStateException` chuyển thành `409`.
- Các response lỗi được ép `Content-Type: application/json`, kể cả khi lỗi xảy ra tại endpoint export Excel.
- Việc ép JSON này đã sửa lỗi runtime trước đó khi export bảng điểm của lớp không tồn tại bị trả `500` thay vì `404`.

### 5.2. Service contract

#### File: `src/main/java/com/university/modules/headoffice/HeadOfficeService.java`

Các nhóm method chính:

- Học phần:
  - `getCourses`.
  - `createCourse`.
  - `updateCourse`.
  - `deleteCourse`.
- Học kỳ:
  - `getSemesters`.
  - `createSemester`.
- Lớp và bảng điểm:
  - `getClasses`.
  - `getClassGrades`.
  - `updateGradeLock`.
- Directory:
  - `getLecturers`.
  - `getStudents`.
- Thống kê:
  - `getStatistics`.
- Export:
  - `exportStatisticsExcel`.
  - `exportLecturersExcel`.
  - `exportStudentsExcel`.
  - `exportClassGradesExcel`.

### 5.3. Service implementation

#### File: `src/main/java/com/university/modules/headoffice/HeadOfficeServiceImpl.java`

#### Constructor và dependency

- Trường `repository` giữ `HeadOfficeRepository`.
- Constructor mặc định tạo `HeadOfficeRepositoryImpl`.
- Constructor nhận repository hỗ trợ unit test và dependency injection thủ công.

#### Học phần

- `getCourses`:
  - Gọi `validatePage`.
  - Đếm tổng số học phần.
  - Lấy danh sách theo trang.
  - Mapping sang `HeadOfficeCourseResponseDTO`.
- `createCourse`:
  - Kiểm tra request rỗng.
  - Kiểm tra mã, tên và số tín chỉ.
  - Phát hiện mã học phần trùng.
  - Tạo entity `HocPhan` và lưu qua repository.
- `updateCourse`:
  - Kiểm tra mã học phần và request.
  - Tìm entity hiện tại.
  - Cập nhật tên và số tín chỉ.
- `deleteCourse`:
  - Kiểm tra học phần tồn tại.
  - Gọi `softDeleteCourseIfUnused`.
  - Từ chối xóa nếu đã được lớp học phần hoặc yêu cầu mở lớp tham chiếu.

#### Học kỳ

- `getSemesters` mapping entity `HocKy` sang response.
- `createSemester`:
  - Kiểm tra field bắt buộc.
  - Kiểm tra mã trùng.
  - Parse ngày theo `yyyy-MM-dd`.
  - Kiểm tra ngày kết thúc không trước ngày bắt đầu.
  - Kiểm tra enum `HOCKY1`, `HOCKY2`, `HOCKYHE`.

#### Lớp và bảng điểm

- `getClasses`:
  - Kiểm tra học kỳ tồn tại.
  - Phân trang lớp học phần.
  - Mapping môn học, giảng viên, sĩ số, trạng thái và khóa điểm.
- `getClassGrades`:
  - Kiểm tra lớp học phần tồn tại.
  - Mapping sinh viên, ảnh thẻ, điểm, kết quả, số buổi vắng và trạng thái.
- `updateGradeLock`:
  - Kiểm tra lớp tồn tại.
  - Gọi repository update trong transaction.
  - Trả trạng thái khóa điểm mới.

#### Directory

- `getLecturers`:
  - Validate phân trang.
  - Lọc theo khoa.
  - Mapping mã nhân viên, họ tên, học vị, khoa và email.
- `getStudents`:
  - Validate phân trang.
  - Lọc theo niên khóa và khoa.
  - Mapping mã sinh viên, họ tên, giới tính, niên khóa, khoa, chuyên ngành và email.

#### Thống kê

- `getStatistics`:
  - Tổng sinh viên.
  - Tổng giảng viên.
  - Tổng học phần.
  - Tổng lớp toàn trường hoặc theo học kỳ.
  - Số kết quả rớt theo học kỳ.
  - Số trường hợp đình chỉ thi theo học kỳ.

#### Export

- `createExcel` là helper dùng chung trong service để gọi `ExcelUtil`.
- `exportStatisticsExcel` xuất thống kê.
- `exportLecturersExcel` xuất danh sách giảng viên.
- `exportStudentsExcel` xuất danh sách sinh viên.
- `exportClassGradesExcel` xuất bảng điểm theo lớp.
- Các giá trị null được chuyển thành chuỗi rỗng bằng helper `safe`.

### 5.4. Repository contract

#### File: `src/main/java/com/university/modules/headoffice/HeadOfficeRepository.java`

Các query contract được chia theo nhóm:

- Học phần: `findCourses`, `countCourses`, `findCourseById`, `saveCourse`, `softDeleteCourseIfUnused`.
- Học kỳ: `findAllSemesters`, `findSemesterById`, `saveSemester`.
- Lớp học phần: `findClasses`, `countClasses`, `findClassById`.
- Bảng điểm: `findClassGrades`.
- Giảng viên: `findLecturers`, `countLecturers`.
- Sinh viên: `findStudents`, `countStudents`.
- Thống kê: các method `countTotal...`, `countFailedGrades`, `countSuspendedExamRecords`.
- Khóa điểm: `updateGradeLock`.

### 5.5. Repository implementation

#### File: `src/main/java/com/university/modules/headoffice/HeadOfficeRepositoryImpl.java`

Vị trí nghiệp vụ chính:

- `findCourses`, `countCourses`, `findCourseById`:
  - Chỉ lấy bản ghi `deleted = false`.
  - Sắp xếp học phần theo mã.
- `saveCourse` và `saveSemester`:
  - Dùng `executeInTransaction`.
  - Persist nếu chưa tồn tại, merge nếu đã có entity.
- `softDeleteCourseIfUnused`:
  - Dùng bulk update.
  - Có `NOT EXISTS` trên `LopHocPhan` và `YeuCauMoLop`.
  - Không xóa vật lý dữ liệu.
- `findClasses`:
  - Join fetch môn học, học kỳ và giảng viên.
  - Hỗ trợ phân trang.
- `findClassGrades`:
  - Join fetch sinh viên và người dùng.
  - Sắp xếp theo mã sinh viên.
- `findLecturers` và `findStudents`:
  - Xây dựng query động theo filter.
  - Join fetch thông tin người dùng để mapping không bị lazy-loading.
- `countFailedGrades`:
  - Lọc `KetQua.F` theo học kỳ.
- `countSuspendedExamRecords`:
  - Lọc `TrangThaiHocPhan.DINHCHITHI` theo học kỳ.
- `updateGradeLock`:
  - Update `LopHocPhan.khoaDiem` trong transaction.

## 6. Build và test tự động

Môi trường:

```text
Java: OpenJDK 17.0.20.1
Maven: Apache Maven 3.10.0
OS: Windows 11
```

Đã chạy thành công:

```powershell
mvn -q -DskipTests compile
mvn -q clean test
mvn clean package
```

Diagnostics module `headoffice`: không có lỗi.

Test hiện có chạy thành công, gồm:

- Auth controller.
- Auth service.
- Security filter.
- JPA refresh token integration.
- JPA metamodel.
- Kết nối PostgreSQL.
- Kiểm tra sau khi gom DTO.

Các warning không làm test thất bại:

- JWT test token không hợp lệ được log warning.
- Log4j2 không tìm thấy implementation và dùng SimpleLogger.

## 7. Kiểm thử runtime trên Tomcat 11 với cổng 8080 và 8005

### 7.1. Server sử dụng

Tomcat 11.0.25

WAR được build và deploy:

```text
target/academic-backend.war
```

Cấu hình runtime dùng:

```text
HTTP: 8080
Shutdown: 8005
```

### 7.2. Kết quả kiểm thử

- Tomcat 11.0.25 khởi động thành công.
- `academic-backend.war` deploy thành công.
- JPA `EntityManagerFactory` khởi tạo thành công.
- Kết nối Neon PostgreSQL thành công.
- Đăng nhập tài khoản `TRUONGPHONG`: `200`.
- GET courses: `200`.
- GET semesters: `200`.
- GET lecturers: `200`.
- GET students: `200`.
- GET statistics: `200`.
- Export lecturers: `200`, đúng MIME Excel.
- Export students: `200`, đúng MIME Excel.
- Export statistics: `200`, đúng MIME Excel.
- Tạo học phần: `201`.
- Cập nhật học phần: `200`.
- Xóa mềm học phần: `200`.
- Request page không hợp lệ: `400`.
- Không có token: `401`.
- Student truy cập Head Office: `403`.
- Lớp không tồn tại: `404`.
- Export bảng điểm lớp không tồn tại: sau khi sửa xử lý lỗi, trả `404`.

### 7.3. Lỗi runtime đã phát hiện và sửa

Trong lần kiểm thử đầu tiên, endpoint:

```text
GET /api/head-office/classes/{maLHP}/students/export
```

trả `500` khi lớp không tồn tại.

Nguyên nhân:

- Endpoint khai báo response Excel.
- Khi service ném lỗi không tìm thấy lớp, controller cần trả JSON lỗi.
- JAX-RS không chọn được response type phù hợp và sinh `500`.

Cách sửa:

- Tại method private `execute` trong `HeadOfficeController.java`, các nhánh lỗi `400`, `404`, `409` được thêm `.type(MediaType.APPLICATION_JSON)`.
- Sau khi build và deploy lại trên Tomcat 11 cổng `8080/8005`, endpoint trả đúng `404`.

### 7.4. Dữ liệu database khi test

Database có dữ liệu giảng viên và sinh viên mẫu.

Tại thời điểm kiểm thử chưa có học kỳ, lớp học phần và bảng điểm mẫu, vì vậy:

- Danh sách học phần/học kỳ có thể trả rỗng.
- Route lớp/bảng điểm thành công với dữ liệu thực chưa thể kiểm tra đầy đủ.
- Route lớp không tồn tại đã được kiểm tra và trả đúng `404`.

## 8. URL kiểm thử trên cổng 8080

Login cán bộ:

```text
POST http://localhost:8080/academic-backend/api/auth/admin/login
```

Danh sách học phần:

```text
GET http://localhost:8080/academic-backend/api/head-office/courses?page=1&limit=10
```

Danh sách học kỳ:

```text
GET http://localhost:8080/academic-backend/api/head-office/semesters
```

Danh sách lớp theo học kỳ:

```text
GET http://localhost:8080/academic-backend/api/head-office/semesters/{maHocKy}/classes?page=1&limit=10
```

Danh sách sinh viên/bảng điểm:

```text
GET http://localhost:8080/academic-backend/api/head-office/classes/{maLHP}/students
```

Khóa/mở khóa điểm:

```text
PATCH http://localhost:8080/academic-backend/api/head-office/classes/{maLHP}/grade-lock
```

Danh sách giảng viên:

```text
GET http://localhost:8080/academic-backend/api/head-office/lecturers?page=1&limit=10
```

Danh sách sinh viên:

```text
GET http://localhost:8080/academic-backend/api/head-office/students?page=1&limit=10
```

Thống kê:

```text
GET http://localhost:8080/academic-backend/api/head-office/statistics
```

Tất cả route Head Office sau login cần header:

```http
Authorization: Bearer <accessToken>
```

## 9. Trạng thái cuối

Role `TRUONGPHONG` đã có phần nghiệp vụ cốt lõi:

- Quản lý học phần.
- Quản lý học kỳ.
- Tra cứu giảng viên.
- Tra cứu sinh viên.
- Giám sát lớp học phần.
- Tra cứu bảng điểm.
- Khóa/mở khóa điểm.
- Thống kê đào tạo.
- Xóa mềm học phần.
- Export Excel.
- Authorization và xử lý lỗi HTTP.
- DTO được gom gọn thành một file request và một file response.

Các việc còn có thể bổ sung:

- Cập nhật tài liệu API chính thức cho các route mới.
- Thêm test riêng cho module `headoffice` nếu quy định nhóm cho phép sửa thư mục test.
- Bổ sung thống kê theo khoa, giới tính và workload giảng viên.
- Tạo dữ liệu học kỳ, lớp học phần và bảng điểm mẫu để kiểm thử các nhánh thành công còn lại.
- Chuyển sang feature branch đúng của người phụ trách và commit/push theo quy trình nhóm.

Tomcat 11 đã được dừng sạch sau khi kiểm thử. Cổng kiểm thử được ghi nhận trong log này là `8080` và `8005`.
