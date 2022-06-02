package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.builder.sheet.spi.DefaultStyleDefinitionSpi;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

/**
 * 样式持有
 * @author xiehai
 * @date 2021/12/17 14:26
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
class StylesHolder implements StyleDefinition {
    static volatile StylesHolder instance = null;
    final StyleDefinition styleDefinition;
    /**
     * 字体实例缓存 仅当前线程 避免重复创建{@link Font}
     */
    private static final ThreadLocal<EnumMap<FontType, Font>> FONTS =
        ThreadLocal.withInitial(() -> new EnumMap<>(FontType.class));
    /**
     * 单元格样式实例缓存 仅当前现场 避免重复创建{@link CellStyle}
     */
    private static final ThreadLocal<Map<StyleType, CellStyle>> CELL_STYLES = ThreadLocal.withInitial(HashMap::new);
    /**
     * 自定义样式方法 仅对当前线程有效
     */
    private static final ThreadLocal<Map<StyleType, Function<Workbook, CellStyle>>> CUSTOM_STYLE_FUNCS =
        ThreadLocal.withInitial(HashMap::new);

    private StylesHolder(StyleDefinition styleDefinition) {
        this.styleDefinition = styleDefinition;
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
                                .orElseGet(DefaultStyleDefinitionSpi::new)
                        );
                }
            }
        }

        return instance;
    }

    @Override
    public Font cellFont(Workbook workbook) {
        return this.font(FontType.DATA, () -> this.styleDefinition.cellFont(workbook));
    }

    @Override
    public Font headerFont(Workbook workbook) {
        return this.font(FontType.HEADER, () -> this.styleDefinition.headerFont(workbook));
    }

    /**
     * 红星表头字体
     * @param workbook {@link Workbook}
     * @return {@link Font}
     */
    public Font readHeadFont(Workbook workbook) {
        return
            this.font(FontType.RED_HEADER, () -> {
                Font font = this.styleDefinition.headerFont(workbook);
                font.setColor(Font.COLOR_RED);

                return font;
            });
    }

    @Override
    public CellStyle cellStyle(Workbook workbook) {
        return this.style(CustomStyleType.DATA, workbook, this.styleDefinition::cellStyle);
    }

    @Override
    public CellStyle headCellStyle(Workbook workbook) {
        return this.style(CustomStyleType.HEAD, workbook, this.styleDefinition::headCellStyle);
    }

    /**
     * 斜线单元格样式
     * @param workbook     {@link Workbook}
     * @param isDiagonalUp 斜线方向是否是左下至右上
     * @return {@link CellStyle}
     */
    public CellStyle diagonalCellStyle(Workbook workbook, boolean isDiagonalUp) {
        return
            this.style(
                isDiagonalUp ? CustomStyleType.SEPARATED_DATA_UP : CustomStyleType.SEPARATED_DATA_DOWN,
                workbook,
                wb -> diagonalStyle(wb, this.cellStyle(wb), isDiagonalUp)
            );
    }

    /**
     * 斜线单元格样式
     * @param workbook     {@link Workbook}
     * @param isDiagonalUp 斜线方向是否是左下至右上
     * @return {@link CellStyle}
     */
    public CellStyle diagonalHeadCellStyle(Workbook workbook, boolean isDiagonalUp) {
        return
            this.style(
                isDiagonalUp ? CustomStyleType.SEPARATED_HEAD_UP : CustomStyleType.SEPARATED_HEAD_DOWN,
                workbook,
                wb -> diagonalStyle(wb, this.headCellStyle(wb), isDiagonalUp)
            );
    }

    /**
     * 字体缓存
     * @param fontType 字体类型
     * @param supplier 字体实例提供
     * @return {@link Font}
     */
    Font font(FontType fontType, Supplier<Font> supplier) {
        if (!FONTS.get().containsKey(fontType)) {
            synchronized (FONTS.get()) {
                if (!FONTS.get().containsKey(fontType)) {
                    FONTS.get().put(fontType, supplier.get());
                }
            }
        }

        return FONTS.get().get(fontType);
    }

    /**
     * 单元格样式缓存
     * @param styleType 样式类型
     * @param workbook  {@link Workbook}
     * @param function  样式生成
     * @return {@link CellStyle}
     */
    CellStyle style(StyleType styleType, Workbook workbook, Function<Workbook, CellStyle> function) {
        if (!CELL_STYLES.get().containsKey(styleType)) {
            synchronized (CELL_STYLES.get()) {
                if (!CELL_STYLES.get().containsKey(styleType)) {
                    CELL_STYLES.get()
                        .put(
                            styleType,
                            Optional.ofNullable(CUSTOM_STYLE_FUNCS.get().get(styleType))
                                .orElse(function)
                                .apply(workbook)
                        );
                }
            }
        }

        return CELL_STYLES.get().get(styleType);
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
    static void clean() {
        FONTS.remove();
        CELL_STYLES.remove();
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
