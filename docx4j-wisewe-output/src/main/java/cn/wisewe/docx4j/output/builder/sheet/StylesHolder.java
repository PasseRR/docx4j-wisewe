package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.builder.sheet.spi.OrderedStyleDefinition;
import cn.wisewe.docx4j.output.builder.sheet.spi.StyleDefinition;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.StreamSupport;

/**
 * 样式持有
 * @author xiehai
 * @date 2021/12/17 14:26
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
class StylesHolder implements StyleDefinition {
    static volatile StylesHolder instance = null;
    final Function<Workbook, Font> fontFunc;
    final Function<Workbook, Font> headFontFunc;
    final Function<Workbook, CellStyle> cellStyleFunc;
    final Function<Workbook, CellStyle> headCellStyleFunc;
    /**
     * 自定义样式方法 仅对当前线程有效
     */
    private static final ThreadLocal<EnumMap<CustomStyleType, Function<Workbook, CellStyle>>> CUSTOM_STYLE_FUNCS =
        ThreadLocal.withInitial(() -> new EnumMap<>(CustomStyleType.class));

    private StylesHolder(OrderedStyleDefinition orderedStyleDefinition) {
        this.fontFunc = orderedStyleDefinition::cellFont;
        this.headFontFunc = orderedStyleDefinition::headerFont;
        this.cellStyleFunc = orderedStyleDefinition::cellStyle;
        this.headCellStyleFunc = orderedStyleDefinition::headCellStyle;
    }

    /**
     * 获取默认spi实例
     * @return {@link OrderedStyleDefinition}
     */
    static StylesHolder instance() {
        if (instance == null) {
            synchronized (StylesHolder.class) {
                if (instance == null) {
                    instance =
                        new StylesHolder(
                            StreamSupport.stream(ServiceLoader.load(OrderedStyleDefinition.class).spliterator(), false)
                                // 取排序号最小的实现
                                .min(Comparator.comparing(OrderedStyleDefinition::order))
                                // 有提供的默认实现
                                .orElseThrow(() ->
                                    new SpreadSheetExportException("no OrderedStyleDefinition impl exists")
                                )
                        );
                }
            }
        }

        return instance;
    }

    @Override
    public Font cellFont(Workbook workbook) {
        return this.fontFunc.apply(workbook);
    }

    @Override
    public Font headerFont(Workbook workbook) {
        return this.headFontFunc.apply(workbook);
    }

    @Override
    public CellStyle cellStyle(Workbook workbook) {
        return
            Optional.ofNullable(CUSTOM_STYLE_FUNCS.get().get(CustomStyleType.DATA))
                .orElse(this.cellStyleFunc)
                .apply(workbook);
    }

    @Override
    public CellStyle headCellStyle(Workbook workbook) {
        return
            Optional.ofNullable(CUSTOM_STYLE_FUNCS.get().get(CustomStyleType.HEAD))
                .orElse(this.headCellStyleFunc)
                .apply(workbook);
    }

    /**
     * 斜线单元格样式
     * @param workbook     {@link Workbook}
     * @param isDiagonalUp 斜线方向是否是左下至右上
     * @return {@link CellStyle}
     */
    public CellStyle diagonalCellStyle(Workbook workbook, boolean isDiagonalUp) {
        return diagonalStyle(workbook, this.cellStyle(workbook), isDiagonalUp);
    }

    /**
     * 斜线单元格样式
     * @param workbook     {@link Workbook}
     * @param isDiagonalUp 斜线方向是否是左下至右上
     * @return {@link CellStyle}
     */
    public CellStyle diagonalHeadCellStyle(Workbook workbook, boolean isDiagonalUp) {
        return diagonalStyle(workbook, this.headCellStyle(workbook), isDiagonalUp);
    }

    /**
     * 设置线程变量
     * @param type     样式类型
     * @param function 样式生成方法
     */
    static void addCustom(CustomStyleType type, Function<Workbook, CellStyle> function) {
        CUSTOM_STYLE_FUNCS.get().put(type, function);
    }

    /**
     * 线程变量移除
     */
    static void removeCustom() {
        CUSTOM_STYLE_FUNCS.remove();
    }

    /**
     * 斜线样式
     * @param workbook     {@link Workbook}
     * @param baseStyle    基础样式
     * @param isDiagonalUp 斜线方向
     * @return {@link CellStyle}
     */
    static CellStyle diagonalStyle(Workbook workbook, CellStyle baseStyle, boolean isDiagonalUp) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.cloneStyleFrom(baseStyle);
        // 拆分单元格左对齐
        cellStyle.setAlignment(HorizontalAlignment.LEFT);

        StylesTable stylesTable;
        if (workbook instanceof SXSSFWorkbook) {
            stylesTable = ((SXSSFWorkbook) workbook).getXSSFWorkbook().getStylesSource();
        } else if (workbook instanceof XSSFWorkbook) {
            stylesTable = ((XSSFWorkbook) workbook).getStylesSource();
        } else {
            throw new SpreadSheetExportException("excel version not support diagonal cell");
        }

        CTXf coreXf = ((XSSFCellStyle) cellStyle).getCoreXf();

        CTBorder ctBorder;
        if (coreXf.getApplyBorder()) {
            long borderId = coreXf.getBorderId();
            XSSFCellBorder cellBorder = stylesTable.getBorderAt((int) borderId);
            ctBorder = (CTBorder) cellBorder.getCTBorder().copy();
        } else {
            ctBorder = CTBorder.Factory.newInstance();
        }
        CTBorderPr ctBorderPr = ctBorder.isSetDiagonal() ? ctBorder.getDiagonal() : ctBorder.addNewDiagonal();
        CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed(IndexedColors.BLACK.index);
        ctBorderPr.setColor(ctColor);
        ctBorderPr.setStyle(STBorderStyle.THIN);

        if (isDiagonalUp) {
            ctBorder.setDiagonalUp(true);
        } else {
            ctBorder.setDiagonalDown(true);
        }

        ctBorder.setDiagonal(ctBorderPr);
        int borderId = stylesTable.putBorder(new XSSFCellBorder(ctBorder));
        coreXf.setBorderId(borderId);
        coreXf.setApplyBorder(true);

        // 自动换行仅对数据支持
        cellStyle.setWrapText(true);

        return cellStyle;
    }
}
