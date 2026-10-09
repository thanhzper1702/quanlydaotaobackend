package com.university.modules.assistant;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssistantExcelExporterTest {

    @Test
    void createsReadableWorkbookWithAllStudentsAndExpectedHeaders() throws Exception {
        List<AssistantResponses.StudentDTO> students = List.of(
                new AssistantResponses.StudentDTO(
                        "B21DCCN001", "Nguyễn Văn An", "https://example/1.jpg",
                        LocalDateTime.of(2026, 1, 2, 9, 15, 30), "DANG_MO"),
                new AssistantResponses.StudentDTO(
                        "B21DCCN002", "Lê Thị Bình", null,
                        LocalDateTime.of(2026, 1, 3, 10, 0), "DANG_MO"));

        AssistantResponses.FilePayload file =
                new AssistantExcelExporter().exportStudents("INT1405_01", students);

        assertEquals(AssistantExcelExporter.XLSX_CONTENT_TYPE, file.getContentType());
        assertEquals("students-INT1405_01.xlsx", file.getFileName());
        assertTrue(file.getContent().length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file.getContent()))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals(3, sheet.getPhysicalNumberOfRows());
            Row header = sheet.getRow(0);
            assertEquals("STT", header.getCell(0).getStringCellValue());
            assertEquals("Mã sinh viên", header.getCell(1).getStringCellValue());
            assertEquals("B21DCCN001", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals("2026-01-02 09:15:30", sheet.getRow(1).getCell(4).getStringCellValue());
            assertEquals("B21DCCN002", sheet.getRow(2).getCell(1).getStringCellValue());
        }
    }

    @Test
    void sanitizesFileNameAgainstPathsAndHeaderInjection() {
        String fileName = AssistantExcelExporter.buildFileName("../INT 1405\r\nmalicious");

        assertFalse(fileName.contains("/"));
        assertFalse(fileName.contains("\\"));
        assertFalse(fileName.contains("\r"));
        assertFalse(fileName.contains("\n"));
        assertTrue(fileName.endsWith(".xlsx"));
    }
}
