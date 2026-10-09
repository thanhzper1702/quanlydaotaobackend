package com.academic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.core.common.ApiResponse;
import com.university.core.model.ThongBao;
import com.university.modules.notification.*;
import com.university.modules.notification.NotificationRequestDTO.CreateNotificationRequestDTO;
import com.university.modules.notification.NotificationRequestDTO.UpdateNotificationRequestDTO;
import com.university.modules.notification.NotificationResponseDTO.CreateNotificationResponseDTO;
import com.university.modules.notification.NotificationResponseDTO.NotificationMetaDTO;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationServiceAndControllerTest {

    private FakeNotificationRepository fakeRepository;
    private NotificationServiceImpl notificationService;
    private TestNotificationController controller;
    private ObjectMapper objectMapper;

    static class TestNotificationController extends NotificationController {
        private String mockCccd;
        private String mockUserCode;
        private String mockRole;

        public TestNotificationController(NotificationService notificationService) {
            super(notificationService);
        }

        public void setMockContext(String cccd, String userCode, String role) {
            this.mockCccd = cccd;
            this.mockUserCode = userCode;
            this.mockRole = role;
        }

        @Override
        protected String getAuthenticatedUserCccd() {
            return mockCccd;
        }

        @Override
        protected String getAuthenticatedUserCode() {
            return mockUserCode;
        }

        @Override
        protected String getAuthenticatedRole() {
            return mockRole;
        }
    }

    static class FakeNotificationRepository implements NotificationRepository {
        Map<Long, ThongBao> storage = new LinkedHashMap<>();
        AtomicLong idSeq = new AtomicLong(100);

        @Override
        public List<ThongBao> findActiveNotifications() {
            return storage.values().stream()
                    .filter(t -> !t.isDeleted())
                    .sorted(Comparator.comparing(ThongBao::getNgayTao, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }

        @Override
        public List<ThongBao> findNotificationsByScope(String phamVi) {
            return storage.values().stream()
                    .filter(t -> !t.isDeleted())
                    .filter(t -> "ALL".equalsIgnoreCase(t.getPhamVi()) || phamVi.equalsIgnoreCase(t.getPhamVi()))
                    .sorted(Comparator.comparing(ThongBao::getNgayTao, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }

        @Override
        public List<ThongBao> findActiveNotificationsPaginated(int page, int limit) {
            List<ThongBao> active = findActiveNotifications();
            int fromIndex = Math.max(0, (page - 1) * limit);
            if (fromIndex >= active.size()) {
                return Collections.emptyList();
            }
            int toIndex = Math.min(fromIndex + limit, active.size());
            return active.subList(fromIndex, toIndex);
        }

        @Override
        public long countActiveNotifications() {
            return storage.values().stream().filter(t -> !t.isDeleted()).count();
        }

        @Override
        public Optional<ThongBao> findActiveById(Long id) {
            ThongBao tb = storage.get(id);
            if (tb != null && !tb.isDeleted()) {
                return Optional.of(tb);
            }
            return Optional.empty();
        }

        @Override
        public void softDelete(Long id) {
            ThongBao tb = storage.get(id);
            if (tb != null) {
                tb.setDeleted(true);
            }
        }

        @Override
        public Optional<ThongBao> findById(Long id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<ThongBao> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public ThongBao save(ThongBao entity) {
            if (entity.getMaThongBao() == null) {
                entity.setMaThongBao(idSeq.incrementAndGet());
            }
            storage.put(entity.getMaThongBao(), entity);
            return entity;
        }

        @Override
        public ThongBao update(ThongBao entity) {
            storage.put(entity.getMaThongBao(), entity);
            return entity;
        }

        @Override
        public void delete(ThongBao entity) {
            if (entity.getMaThongBao() != null) {
                storage.remove(entity.getMaThongBao());
            }
        }

        @Override
        public void deleteById(Long id) {
            storage.remove(id);
        }

        @Override
        public long count() {
            return storage.size();
        }

        @Override
        public void seedInitialNotificationsIfEmpty() {
            if (countActiveNotifications() == 0) {
                ThongBao tb1 = new ThongBao();
                tb1.setMaThongBao(idSeq.incrementAndGet());
                tb1.setTieuDe("Thông báo đăng ký học phần Học kỳ 1");
                tb1.setNoiDung("Sinh viên khóa 2021, 2022 thực hiện đăng ký...");
                tb1.setPhamVi("TOAN_TRUONG");
                tb1.setDeleted(false);
                tb1.setNgayTao(LocalDateTime.now().minusHours(2));
                storage.put(tb1.getMaThongBao(), tb1);
            }
        }
    }

    @BeforeEach
    void setUp() {
        fakeRepository = new FakeNotificationRepository();

        ThongBao tb1 = new ThongBao();
        tb1.setMaThongBao(101L);
        tb1.setTieuDe("Thông báo đăng ký học phần Học kỳ 1");
        tb1.setNoiDung("Sinh viên khóa 2021, 2022 thực hiện đăng ký...");
        tb1.setPhamVi("TOAN_TRUONG");
        tb1.setDeleted(false);
        tb1.setNgayTao(LocalDateTime.of(2025, 8, 1, 8, 30, 0));
        fakeRepository.storage.put(101L, tb1);

        ThongBao tb2 = new ThongBao();
        tb2.setMaThongBao(102L);
        tb2.setTieuDe("Thông báo thi học kỳ chính thức");
        tb2.setNoiDung("Lịch thi chính thức sẽ được công bố...");
        tb2.setPhamVi("TOAN_TRUONG");
        tb2.setDeleted(false);
        tb2.setNgayTao(LocalDateTime.of(2025, 8, 2, 9, 0, 0));
        fakeRepository.storage.put(102L, tb2);

        notificationService = new NotificationServiceImpl(fakeRepository);
        controller = new TestNotificationController(notificationService);
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Route 07: GET /notifications - Thành công, có data và meta phân trang")
    void testGetNotifications_Success() throws Exception {
        controller.setMockContext("001099000001", "SV001", "SINHVIEN");

        Response response = controller.getNotifications(1, 10);
        assertEquals(200, response.getStatus());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(200, body.getStatus());
        assertTrue(body.isSuccess());
        assertEquals("Danh sách thông báo kèm meta phân trang", body.getMessage());
        assertNotNull(body.getData());
        assertNotNull(body.getMeta());

        NotificationMetaDTO meta = (NotificationMetaDTO) body.getMeta();
        assertEquals(1, meta.getPage());
        assertEquals(10, meta.getPageSize());
        assertEquals(2, meta.getTotalItems());
        assertEquals(1, meta.getTotalPages());

        // Kiểm tra serialization JSON có đầy đủ trường chuẩn của core ApiResponse
        String json = objectMapper.writeValueAsString(body);
        JsonNode rootNode = objectMapper.readTree(json);
        assertTrue(rootNode.has("success"));
        assertTrue(rootNode.get("success").asBoolean());
        assertTrue(rootNode.has("status"));
        assertTrue(rootNode.has("message"));
        assertTrue(rootNode.has("data"));
        assertTrue(rootNode.has("meta"));
    }

    @Test
    @DisplayName("Route 07: GET /notifications - 401 Unauthorized khi chưa đăng nhập")
    void testGetNotifications_Unauthorized() {
        controller.setMockContext(null, null, null);

        Response response = controller.getNotifications(1, 10);
        assertEquals(401, response.getStatus());
    }

    @Test
    @DisplayName("Route 08: POST /notifications - Trợ lý đào tạo (TROLY) tạo thông báo thành công (201)")
    void testCreateNotification_TroLy_Success() throws Exception {
        controller.setMockContext("001088000001", "NV001", "TROLY");

        CreateNotificationRequestDTO request = new CreateNotificationRequestDTO(
                "Thông báo học bổng khuyến khích học tập",
                "Danh sách sinh viên nhận học bổng kỳ 2 đã được cập nhật.",
                "TOAN_TRUONG"
        );

        Response response = controller.createNotification(request);
        assertEquals(201, response.getStatus());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(201, body.getStatus());
        assertTrue(body.isSuccess());
        assertEquals("Phát hành thông báo thành công", body.getMessage());
        assertNotNull(body.getData());

        CreateNotificationResponseDTO data = (CreateNotificationResponseDTO) body.getData();
        assertNotNull(data.getMaThongBao());
        assertNotNull(data.getNgayTao());

        // Kiểm tra serialization JSON
        String json = objectMapper.writeValueAsString(body);
        JsonNode rootNode = objectMapper.readTree(json);
        assertTrue(rootNode.has("success"));
        assertFalse(rootNode.has("meta"));
    }

    @Test
    @DisplayName("Route 08: POST /notifications - Trưởng phòng (TRUONGPHONG) tạo thông báo thành công (201)")
    void testCreateNotification_TruongPhong_Success() {
        controller.setMockContext("001088000002", "NV002", "TRUONGPHONG");

        CreateNotificationRequestDTO request = new CreateNotificationRequestDTO(
                "Thông báo nghỉ lễ",
                "Toàn trường nghỉ lễ theo quy định.",
                "TOAN_TRUONG"
        );

        Response response = controller.createNotification(request);
        assertEquals(201, response.getStatus());
    }

    @Test
    @DisplayName("Route 08: POST /notifications - 403 Forbidden khi Sinh viên cố tạo thông báo")
    void testCreateNotification_SinhVien_Forbidden() {
        controller.setMockContext("001099000001", "SV001", "SINHVIEN");

        CreateNotificationRequestDTO request = new CreateNotificationRequestDTO(
                "Thông báo giả mạo",
                "Nội dung bất kỳ",
                "TOAN_TRUONG"
        );

        Response response = controller.createNotification(request);
        assertEquals(403, response.getStatus());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(403, body.getStatus());
        assertFalse(body.isSuccess());
        assertEquals("Bạn không có quyền thực hiện chức năng này", body.getMessage());
    }

    @Test
    @DisplayName("Route 08: POST /notifications - 403 Forbidden khi Giảng viên cố tạo thông báo")
    void testCreateNotification_GiangVien_Forbidden() {
        controller.setMockContext("001077000001", "GV001", "GIANGVIEN");

        CreateNotificationRequestDTO request = new CreateNotificationRequestDTO(
                "Thông báo từ giảng viên",
                "Nội dung...",
                "TOAN_TRUONG"
        );

        Response response = controller.createNotification(request);
        assertEquals(403, response.getStatus());
    }

    @Test
    @DisplayName("Route 08: POST /notifications - 400 Bad Request khi thiếu tiêu đề")
    void testCreateNotification_MissingTieuDe() {
        controller.setMockContext("001088000001", "NV001", "TROLY");

        CreateNotificationRequestDTO request = new CreateNotificationRequestDTO(
                "",
                "Nội dung có nhưng tiêu đề rỗng",
                "TOAN_TRUONG"
        );

        Response response = controller.createNotification(request);
        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("Route 08: POST /notifications - 400 Bad Request khi thiếu nội dung")
    void testCreateNotification_MissingNoiDung() {
        controller.setMockContext("001088000001", "NV001", "TROLY");

        CreateNotificationRequestDTO request = new CreateNotificationRequestDTO(
                "Có tiêu đề",
                "   ",
                "TOAN_TRUONG"
        );

        Response response = controller.createNotification(request);
        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("Route 09: PUT /notifications/{id} - Trợ lý cập nhật thông báo thành công (200)")
    void testUpdateNotification_Success() throws Exception {
        controller.setMockContext("001088000001", "NV001", "TROLY");

        UpdateNotificationRequestDTO request = new UpdateNotificationRequestDTO(
                "Tiêu đề đã được cập nhật",
                "Nội dung mới chi tiết hơn"
        );

        Response response = controller.updateNotification(101L, request);
        assertEquals(200, response.getStatus());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(200, body.getStatus());
        assertTrue(body.isSuccess());
        assertEquals("Cập nhật thành công", body.getMessage());
        assertNull(body.getData());

        // Kiểm tra bản ghi trong repository đã đổi
        ThongBao updated = fakeRepository.storage.get(101L);
        assertEquals("Tiêu đề đã được cập nhật", updated.getTieuDe());
        assertEquals("Nội dung mới chi tiết hơn", updated.getNoiDung());

        // Kiểm tra JSON serialization
        String json = objectMapper.writeValueAsString(body);
        JsonNode rootNode = objectMapper.readTree(json);
        assertTrue(rootNode.has("success"));
        assertTrue(rootNode.get("data").isNull());
    }

    @Test
    @DisplayName("Route 09: PUT /notifications/{id} - 403 Forbidden khi Sinh viên cố cập nhật")
    void testUpdateNotification_SinhVien_Forbidden() {
        controller.setMockContext("001099000001", "SV001", "SINHVIEN");

        UpdateNotificationRequestDTO request = new UpdateNotificationRequestDTO("Test", "Test");
        Response response = controller.updateNotification(101L, request);
        assertEquals(403, response.getStatus());
    }

    @Test
    @DisplayName("Route 09: PUT /notifications/{id} - 404 Not Found khi không tìm thấy thông báo")
    void testUpdateNotification_NotFound() {
        controller.setMockContext("001088000001", "NV001", "TROLY");

        UpdateNotificationRequestDTO request = new UpdateNotificationRequestDTO("Test", "Test");
        Response response = controller.updateNotification(9999L, request);
        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("Route 09: PUT /notifications/{id} - 400 Bad Request khi ID âm hoặc không hợp lệ")
    void testUpdateNotification_InvalidId() {
        controller.setMockContext("001088000001", "NV001", "TROLY");

        UpdateNotificationRequestDTO request = new UpdateNotificationRequestDTO("Test", "Test");
        Response response = controller.updateNotification(-5L, request);
        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("Route 10: DELETE /notifications/{id} - Xóa mềm thông báo thành công (200)")
    void testDeleteNotification_Success() throws Exception {
        controller.setMockContext("001088000001", "NV001", "TROLY");

        Response response = controller.deleteNotification(101L);
        assertEquals(200, response.getStatus());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(200, body.getStatus());
        assertTrue(body.isSuccess());
        assertEquals("Xóa thông báo thành công", body.getMessage());
        assertNull(body.getData());

        // Kiểm tra soft delete
        ThongBao tb = fakeRepository.storage.get(101L);
        assertTrue(tb.isDeleted(), "Bản ghi phải được đánh dấu deleted = true");

        // Gọi lại lần nữa phải trả về 404 vì đã bị xóa
        Response secondDelete = controller.deleteNotification(101L);
        assertEquals(404, secondDelete.getStatus());

        // Kiểm tra JSON serialization
        String json = objectMapper.writeValueAsString(body);
        JsonNode rootNode = objectMapper.readTree(json);
        assertTrue(rootNode.has("success"));
        assertTrue(rootNode.get("data").isNull());
    }

    @Test
    @DisplayName("Route 10: DELETE /notifications/{id} - 403 Forbidden khi Giảng viên cố xóa")
    void testDeleteNotification_GiangVien_Forbidden() {
        controller.setMockContext("001077000001", "GV001", "GIANGVIEN");

        Response response = controller.deleteNotification(101L);
        assertEquals(403, response.getStatus());
    }

    @Test
    @DisplayName("Route 10: DELETE /notifications/{id} - 404 Not Found khi xóa ID không tồn tại")
    void testDeleteNotification_NotFound() {
        controller.setMockContext("001088000001", "NV001", "TROLY");

        Response response = controller.deleteNotification(9999L);
        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("Route 07: GET /notifications - Role Trợ lý (TROLY) nhận danh sách thông báo có data (không bị rỗng)")
    void testGetNotifications_RoleTroLy_ReturnsData() {
        controller.setMockContext("001088000001", "NV001", "TROLY");

        Response response = controller.getNotifications(1, 10);
        assertEquals(200, response.getStatus());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(200, body.getStatus());
        assertTrue(body.isSuccess());

        @SuppressWarnings("unchecked")
        List<?> data = (List<?>) body.getData();
        assertNotNull(data);
        assertFalse(data.isEmpty(), "Data thông báo của trợ lý không được rỗng []");
        assertEquals(2, data.size());
    }

    @Test
    @DisplayName("Route 07: GET /notifications - Khi DB trống hoàn toàn, hệ thống tự động khởi tạo dữ liệu mẫu và trả về data")
    void testGetNotifications_WhenEmpty_AutoSeedsData() {
        fakeRepository.storage.clear(); // DB trống hoàn toàn
        controller.setMockContext("001088000001", "NV001", "TROLY");

        Response response = controller.getNotifications(1, 10);
        assertEquals(200, response.getStatus());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertEquals(200, body.getStatus());

        @SuppressWarnings("unchecked")
        List<?> data = (List<?>) body.getData();
        assertNotNull(data);
        assertFalse(data.isEmpty(), "Khi DB trống, phải tự động seed thông báo mẫu để không trả về data []");
    }
}
