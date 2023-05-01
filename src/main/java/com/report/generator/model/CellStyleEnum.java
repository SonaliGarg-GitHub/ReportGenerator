package com.report.generator.model;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.Map;

public enum CellStyleEnum {


    HEADER {
        @Override
        public CellStyle getStyle(Workbook workbook) {
            Font font = workbook.createFont();
            font.setBold(true);

            CellStyle cellStyle = workbook.createCellStyle();

            setLightColor(cellStyle);
            setAllBorders(cellStyle);
            cellStyle.setFont(font);

            return cellStyle;
        }
    },
    NORMAL {
        @Override
        public CellStyle getStyle(Workbook workbook) {
            CellStyle cellStyle = workbook.createCellStyle();
            setAllBorders(cellStyle);
            return cellStyle;
        }
    },
    COLORED {
        @Override
        public CellStyle getStyle(Workbook workbook) {
            CellStyle cellStyle = workbook.createCellStyle();
            setLightColor(cellStyle);
            setAllBorders(cellStyle);
            return cellStyle;
        }

    };

    private static void setLightColor(CellStyle cellStyle) {
        byte[] lightBlueRGB = new byte[]{(byte) 200, (byte) 220, (byte) 255};
        XSSFColor lightBlueColor = new XSSFColor(lightBlueRGB, null);
        cellStyle.setFillForegroundColor(lightBlueColor);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    private static void setAllBorders(CellStyle cellStyle) {
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
    }

    public abstract CellStyle getStyle(Workbook workbook);

    private static final Map<String, CellStyle> styles = new HashMap<>();

    static {
        // Initialize the styles map
        Workbook workbook = new XSSFWorkbook();
        styles.put(CellStyleEnum.HEADER.name(), CellStyleEnum.HEADER.getStyle(workbook));
        styles.put(CellStyleEnum.NORMAL.name(), CellStyleEnum.NORMAL.getStyle(workbook));
        styles.put(CellStyleEnum.COLORED.name(), CellStyleEnum.COLORED.getStyle(workbook));
    }

    public static CellStyle getStyle(String type) {
        return styles.get(type);
    }

}
