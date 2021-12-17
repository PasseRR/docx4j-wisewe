package cn.wisewe.docx4j.output.builder.sheet.spi;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 样式定义
 * @author xiehai
 * @date 2021/12/17 14:11
 */
public interface StyleDefinition {
    /**
     * 默认字体
     * @param workbook {@link Workbook}
     * @return {@link Font}
     */
    default Font cellFont(Workbook workbook) {
        return workbook.createFont();
    }
    
    /**
     * 表头字体
     * @param workbook {@link Workbook}
     * @return {@link Font}
     */
    default Font headerFont(Workbook workbook) {
        Font font = this.cellFont(workbook);
        font.setBold(true);

        return font;
    }

    /**
     * 默认单元格样式
     * @param workbook {@link Workbook}
     * @return {@link CellStyle}
     */
    default CellStyle cellStyle(Workbook workbook) {
        // 单元格居中
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(this.cellFont(workbook));
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 设置边框样式
        StyleDefinition.setCellBorderStyle(cellStyle);

        // 自动换行仅对数据支持
        cellStyle.setWrapText(true);

        return cellStyle;
    }

    /**
     * 表头样式
     * @param workbook {@link Workbook}
     * @return {@link CellStyle}
     */
    default CellStyle headCellStyle(Workbook workbook) {
        // 单元格居中 表头背景色
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(this.headerFont(workbook));
        // 设置表头背景色
        cellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置表头边线及边线颜色
        // 设置边框样式
        StyleDefinition.setCellBorderStyle(cellStyle);
        // 内容对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellStyle;
    }

    /**
     * 单元格边框样式
     * @param cellStyle   单元格样式
     * @param borderStyle 边线样式
     * @param color       边线颜色
     */
    static void setCellBorderStyle(CellStyle cellStyle, BorderStyle borderStyle, short color) {
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setTopBorderColor(color);
        cellStyle.setBottomBorderColor(color);
        cellStyle.setLeftBorderColor(color);
        cellStyle.setRightBorderColor(color);
    }

    /**
     * 设置默认边框颜色
     * @param cellStyle {@link CellStyle}
     */
    static void setCellBorderStyle(CellStyle cellStyle) {
        // 默认为细黑的边线
        StyleDefinition.setCellBorderStyle(cellStyle, BorderStyle.THIN, IndexedColors.BLACK.index);
    }
}
