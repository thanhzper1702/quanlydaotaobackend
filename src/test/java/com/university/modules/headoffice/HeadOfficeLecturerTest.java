package com.university.modules.headoffice;

import com.university.core.model.GiangVien;
import com.university.core.model.NguoiDung;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HeadOfficeLecturerTest {

    @Test
    void testGetLecturersWithKhoaAndKeyword() {
        AtomicReference<String> capturedKhoa = new AtomicReference<>();
        AtomicReference<String> capturedKeyword = new AtomicReference<>();

        GiangVien gv = new GiangVien();
        gv.setMaNV("GV001");
        gv.setKhoa("Công nghệ thông tin");
        gv.setHocVi("TIENSI");

        NguoiDung user = new NguoiDung();
        user.setCccd("001088000001");
        user.setHoTen("Trần Thị Bình");
        user.setEmail("binh.tran@example.com");
        user.setSoDienThoai("0903123456");
        gv.setNguoiDung(user);

        HeadOfficeRepository repo = (HeadOfficeRepository) Proxy.newProxyInstance(
                HeadOfficeRepository.class.getClassLoader(),
                new Class[]{HeadOfficeRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "countLecturers" -> {
                        capturedKhoa.set((String) args[0]);
                        if (args.length > 1) capturedKeyword.set((String) args[1]);
                        yield 1L;
                    }
                    case "findLecturers" -> {
                        capturedKhoa.set((String) args[0]);
                        if (args.length == 4) capturedKeyword.set((String) args[1]);
                        yield List.of(gv);
                    }
                    case "countClassesByLecturer" -> 3L;
                    case "toString" -> "HeadOfficeRepoMock";
                    default -> throw new UnsupportedOperationException(method.getName());
                });

        HeadOfficeServiceImpl service = new HeadOfficeServiceImpl(repo);
        var result = service.getLecturers("CNTT", "Bình", 1, 10);

        assertEquals("CNTT", capturedKhoa.get());
        assertEquals("Bình", capturedKeyword.get());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getItems().size());

        var item = result.getItems().get(0);
        assertEquals("GV001", item.getMaNV());
        assertEquals("Trần Thị Bình", item.getHoTen());
        assertEquals("0903123456", item.getSoDienThoai());
        assertEquals(3, item.getSoLopPhuTrach());
    }

    @Test
    void testControllerGetLecturersPassesKeyword() {
        AtomicReference<String> capturedKeyword = new AtomicReference<>();
        HeadOfficeService service = (HeadOfficeService) Proxy.newProxyInstance(
                HeadOfficeService.class.getClassLoader(),
                new Class[]{HeadOfficeService.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getLecturers" -> {
                        if (args.length == 4) {
                            capturedKeyword.set((String) args[1]);
                        }
                        yield new HeadOfficeResponseDTO.HeadOfficeLecturerResponseDTO(List.of(), 1, 10, 0);
                    }
                    default -> null;
                });

        HeadOfficeController controller = new HeadOfficeController(service);
        var response = controller.getLecturers("CNTT", "Bình", 1, 10);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Bình", capturedKeyword.get());
    }
}
