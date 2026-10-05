package com.university.core.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Utility for exporting and generating Microsoft Excel (.xlsx) workbooks using Apache POI.
 */
public final class ExcelUtil {

    private ExcelUtil() {
    }

    /**
     * Generates a standard Excel workbook from header titles and row data.
     *
     * @param sheetName Name of the sheet
     * @param headers   List of column header labels
     * @param rowsData  List of row items, each represented as a list of strings
     * @return byte[] containing the .xlsx binary data
     * @throws IOException If writing fails
     */
    public static byte[] createWorkbook(String sheetName, List<String> headers, List<List<String>> rowsData) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Sheet1");

            // Header styling
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < headers.size(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers.get(col));
                cell.setCellStyle(headerCellStyle);
            }

            // Create Data Rows
            int rowIdx = 1;
            for (List<String> rowData : rowsData) {
                Row row = sheet.createRow(rowIdx++);
                for (int col = 0; col < rowData.size(); col++) {
                    row.createCell(col).setCellValue(rowData.get(col) != null ? rowData.get(col) : "");
                }
            }

            // Resize columns
            for (int col = 0; col < headers.size(); col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
