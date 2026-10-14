package com.university.modules.assistant;

import com.university.core.model.HocKy;
import com.university.core.model.HocPhan;
import com.university.core.model.LichHoc;
import com.university.core.model.LopHocPhan;
import com.university.core.model.TroLyDaoTao;
import com.university.core.model.enums.TenHocKy;
import com.university.core.model.enums.TrangThaiLop;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AssistantMapperTest {

    @Test
    void mapsSemesterToFriendlyContractPayload() {
        HocKy semester = new HocKy();
        semester.setMaHocKy("2025_2026_HK1");
        semester.setTenHocKy(TenHocKy.HOCKY1);
        semester.setThoiGianBD(LocalDate.of(2025, 9, 1));
        semester.setThoiGianKT(LocalDate.of(2026, 1, 15));

        AssistantResponses.SemesterDTO dto = AssistantMapper.toSemesterDTO(semester, true);

        assertEquals("Học kỳ 1 (2025 - 2026)", dto.getTenHocKy());
        assertTrue(dto.isEditable());
    }

    @Test
    void mapsContractTietBDToEntityTietBatDauAndTransientAuditFields() {
        HocKy semester = new HocKy();
        HocPhan course = new HocPhan();
        course.setMaHP("INT1405");
        TroLyDaoTao assistant = new TroLyDaoTao();
        AssistantRequests.CreateClassRequest request = new AssistantRequests.CreateClassRequest(
                " INT1405_01 ", " Lớp 01 ", "INT1405", 2, 1, 3, 60);

        LopHocPhan entity = AssistantMapper.toNewClassEntity(request, semester, course, assistant);

        assertEquals("INT1405_01", entity.getMaLHP());
        assertEquals(1, entity.getTietBatDau());
        assertEquals(TrangThaiLop.CHUA_MO, entity.getTrangThai());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void mapsDatabaseOpenStatusToContractAndBack() {
        assertEquals("DANG_MO", AssistantMapper.toContractClassStatus(TrangThaiLop.MODANGKY));
        assertEquals(TrangThaiLop.MODANGKY, AssistantMapper.toEntityClassStatus("dang_mo"));
        assertEquals(TrangThaiLop.DANGHOC, AssistantMapper.toEntityClassStatus("DANGHOC"));
    }

    @Test
    void mapsClassAndScheduleWithoutReturningEntities() {
        HocPhan course = new HocPhan();
        course.setMaHP("INT1405");
        course.setTenHP("Kỹ nghệ phần mềm");
        course.setSoTinChi(3);
        LopHocPhan courseClass = new LopHocPhan();
        courseClass.setMaLHP("INT1405_01");
        courseClass.setTenLHP("Lớp 01");
        courseClass.setMonHoc(course);
        courseClass.setThu(2);
        courseClass.setTietBatDau(1);
        courseClass.setSoTiet(3);
        courseClass.setTrangThai(TrangThaiLop.MODANGKY);

        AssistantResponses.ClassDTO classDTO = AssistantMapper.toClassDTO(courseClass, "A2-301");
        assertEquals("INT1405", classDTO.getMaHP());
        assertEquals("DANG_MO", classDTO.getTrangThai());
        assertEquals("A2-301", classDTO.getPhongHoc());

        LichHoc schedule = new LichHoc();
        schedule.setMaLichHoc("LH_01");
        schedule.setNgayHoc(LocalDate.of(2026, 1, 5));
        schedule.setPhongHoc("A2-301");
        assertEquals("LH_01", AssistantMapper.toScheduleDTO(schedule).getMaLichHoc());
    }
}
