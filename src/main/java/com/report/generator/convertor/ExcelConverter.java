package com.report.generator.convertor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.report.generator.model.CellStyleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public class ExcelConverter {

    public Workbook getNewWorkbook(){
        return new XSSFWorkbook();
    }

    public void convertToExcelFile(Workbook workbook, String filepath) {

        try {
            FileOutputStream outputStream = new FileOutputStream(filepath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            log.error("Couldn't write to file path");
            throw new RuntimeException(e);
        }

    }

    public static void addSheet(Workbook workbook, String sheetName , List<Map<String, Object>> rows) {
        Sheet sheet = workbook.createSheet(sheetName);
        CellStyle headerStyle = CellStyleEnum.HEADER.getStyle(workbook);

        // Write the header row
        Row headerRow = sheet.createRow(0);
        //TODO: Handle NULL Rows and Null Data
        String[] header = rows.get(0).keySet().toArray(new String[0]);
        for (int i = 0; i < header.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(header[i]);
        }
        // Write the data rows with alternate row style
        setRows(rows, sheet , workbook);

        // Set the column width automatically to fit the contents of the cells
        for (int i = 0; i < header.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void setRows(List<Map<String, Object>> rows, Sheet sheet, Workbook workbook) {
        CellStyle coloredStyle = CellStyleEnum.COLORED.getStyle(workbook);
        CellStyle normalStyle = CellStyleEnum.NORMAL.getStyle(workbook);
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            Row dataRow = sheet.createRow(i + 1);
            int j = 0;
            for (Object value : row.values()) {
                Cell cell = dataRow.createCell(j++);
                // Set the background color of the row based on its index - Alternate rows
                if ((i + 1) % 2 == 0) {
                    cell.setCellStyle(coloredStyle);
                }else{
                    cell.setCellStyle(normalStyle);
                }
                cell.setCellValue((value ==null?"":value).toString());

            }
        }
    }
}
