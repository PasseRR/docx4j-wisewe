package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.OutputConstants;
import cn.wisewe.docx4j.output.utils.StringConverterUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.PackagePrivate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link Cell} dsl
 * @author xiehai
 * @date 2020/12/24 13:46
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslCell {
    Cell cell;
    @NonFinal
    @PackagePrivate
    int rowspan = 1;
    @NonFinal
    @PackagePrivate
    int colspan = 1;
    @NonFinal
    @PackagePrivate
    boolean diagonalUp = false;
    @NonFinal
    @PackagePrivate
    int diagonalIndex;
    /**
     * 对角线
     */
    protected static final ThreadLocal<Map<Integer, Set<DslCell>>> DIAGONAL_COLUMNS =
        ThreadLocal.withInitial(HashMap::new);
    /**
     * 列宽最低
     */
    protected static final ThreadLocal<Map<Integer, Integer>> AT_LEAST_MIN_WIDTH =
        ThreadLocal.withInitial(HashMap::new);
    /**
     * 样式定义
     */
    protected static final StylesHolder LOADER = StylesHolder.instance();

    DslCell(Cell cell) {
        this.cell = cell;
    }

    /**
     * 单元格设置
     * @param consumer 设置方法
     * @return {@link DslCell}
     */
    public DslCell accept(Consumer<Cell> consumer) {
        consumer.accept(this.cell);
        return this;
    }

    /**
     * 设置单元格内容
     * @param o 单元格内容对象
     * @return {@link DslCell}
     */
    public DslCell text(Object o) {
        if (Objects.isNull(o)) {
            return this.accept(Cell::setBlank);
        }

        return this.accept(c -> c.setCellValue(StringConverterUtil.convert(o)));
    }

    /**
     * 富文本
     * @param text 富文本内容
     * @return {@link DslCell}
     */
    public DslCell rich(RichTextString text) {
        return this.accept(c -> c.setCellValue(text));
    }

    /**
     * 自定义富文本
     * @param function 生成富文本方法
     * @return {@link DslCell}
     */
    public DslCell rich(Function<Workbook, RichTextString> function) {
        return this.rich(function.apply(this.getWorkBook()));
    }

    /**
     * 以红星开头文本
     * @param o 文本对象
     * @return {@link DslCell}
     */
    public DslCell redAster(Object o) {
        return
            this.rich(wb -> {
                RichTextString rich =
                    wb.getCreationHelper()
                        .createRichTextString(OutputConstants.ASTER + StringConverterUtil.convert(o));
                Font aster = LOADER.readHeadFont(wb);
                rich.applyFont(0, 1, aster);
                rich.applyFont(1, rich.length(), LOADER.headerFont(wb));

                return rich;
            });
    }

    /**
     * 设置单元格内容
     * @param supplier 单元格内容提供器
     * @return {@link DslCell}
     */
    public DslCell text(Supplier<?> supplier) {
        return this.text(supplier.get());
    }

    /**
     * 添加图片到单元格
     * @return {@link DslCell}
     */
    public DslCell picture(File file, int width, int height) {
        CreationHelper helper = this.getWorkBook().getCreationHelper();
        Drawing<?> drawing = this.cell.getSheet().createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();

        try (FileInputStream is = new FileInputStream(file)) {
            int format = SpreadSheetPictureType.getFormat(file.getName());
            int index = this.getWorkBook().addPicture(IOUtils.toByteArray(is), format);
            anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
            int c = this.cell.getColumnIndex(), r = this.cell.getRowIndex();
            // 设置图片起止行列
            anchor.setCol1(c);
            anchor.setCol2(c + 1);
            anchor.setRow1(r);
            anchor.setRow2(r + 1);
            // 宽度单位转像素
            double cellHeight = Units.columnWidthToEMU(height * 256) / (Units.EMU_PER_PIXEL * 1.0);
            // 若图片高度大于当前行高度
            if (this.cell.getRow().getHeightInPoints() < cellHeight) {
                // 以图片高度设置单元格高度
                this.cell.getRow().setHeightInPoints((float) Units.pixelToPoints(cellHeight));
            }
            // 插入图片，如果原图宽度大于最终要求的图片宽度，就按比例缩小，否则展示原图
            drawing.createPicture(anchor, index).resize(1);
        } catch (IOException e) {
            throw new SpreadSheetExportException(e);
        }

        return this.atLeastWidth(width);
    }

    /**
     * 合并列
     * @param colSpan 合并列数
     * @return {@link DslCell}
     */
    public DslCell colspan(int colSpan) {
        if (colSpan > 1) {
            this.colspan = colSpan;
        }
        return this;
    }

    /**
     * 合并行
     * @param rowspan 合并行数
     * @return {@link DslCell}
     */
    public DslCell rowspan(int rowspan) {
        if (rowspan > 1) {
            this.rowspan = rowspan;
        }

        return this;
    }

    /**
     * 设置最低宽度
     * @param width 最低宽度
     * @return {@link DslCell}
     */
    public DslCell atLeastWidth(int width) {
        AT_LEAST_MIN_WIDTH.get().merge(this.cell.getColumnIndex(), width, Integer::max);

        return this;
    }

    /**
     * 设置单元格样式
     * @param style {@link CellStyle}
     * @return {@link DslCell}
     */
    public DslCell style(CellStyle style) {
        return this.accept(c -> c.setCellStyle(style));
    }

    /**
     * 单元格样式设置
     * @param function 单元格样式生成方法
     * @return {@link DslCell}
     */
    public DslCell style(Function<Workbook, CellStyle> function) {
        return this.style(function.apply(this.getWorkBook()));
    }

    /**
     * 样式扩展
     * @param consumer 扩展方法
     * @return {@link DslCell}
     */
    public DslCell expandStyle(BiConsumer<Workbook, CellStyle> consumer) {
        Workbook workBook = this.getWorkBook();
        CellStyle cellStyle = workBook.createCellStyle();
        // 样式clone修改
        cellStyle.cloneStyleFrom(this.cell.getCellStyle());
        consumer.accept(workBook, cellStyle);

        return this.style(cellStyle);
    }

    /**
     * 设置单元格为表头样式
     * @return {@link DslCell}
     */
    public DslCell headStyle() {
        return this.style(LOADER.headCellStyle(this.getWorkBook()));
    }

    /**
     * 设置表头单元格斜线样式
     * @return {@link DslCell}
     */
    public DslCell diagonalHeadStyle(boolean isDiagonalUp) {
        this.setDiagonal(isDiagonalUp);

        return this.style(LOADER.diagonalHeadCellStyle(this.getWorkBook(), isDiagonalUp));
    }

    /**
     * 设置数据单元格斜线样式
     * @return {@link DslCell}
     */
    public DslCell diagonalDataStyle(boolean isDiagonalUp) {
        this.setDiagonal(isDiagonalUp);

        return this.style(LOADER.diagonalCellStyle(this.getWorkBook(), isDiagonalUp));
    }

    /**
     * 设置单元格为数据行样式
     * @return {@link DslCell}
     */
    public DslCell dataStyle() {
        return this.style(LOADER.cellStyle(this.getWorkBook()));
    }

    /**
     * 带斜线单元格
     * @param left  第一部分
     * @param right 第二部分
     * @return {@link DslCell}
     */
    protected DslCell text(String left, String right) {
        // 设置分隔字符串索引
        this.diagonalIndex = left.length();
        return this.text(left + right);
    }

    /**
     * 单元格合并范围
     * @return {@link CellRangeAddress}
     */
    protected CellRangeAddress getCellRangeAddress() {
        CellRangeAddress cellAddresses = new CellRangeAddress(
            this.cell.getRowIndex(),
            this.cell.getRowIndex() + this.rowspan - 1,
            this.cell.getColumnIndex(),
            this.cell.getColumnIndex() + this.colspan - 1
        );

        // 单元格合并边框样式
        CellStyle style = this.cell.getCellStyle();
        Sheet sheet = this.cell.getSheet();
        RegionUtil.setBorderLeft(style.getBorderLeft(), cellAddresses, sheet);
        RegionUtil.setBorderRight(style.getBorderRight(), cellAddresses, sheet);
        RegionUtil.setBorderTop(style.getBorderTop(), cellAddresses, sheet);
        RegionUtil.setBorderBottom(style.getBorderBottom(), cellAddresses, sheet);
        RegionUtil.setLeftBorderColor(style.getLeftBorderColor(), cellAddresses, sheet);
        RegionUtil.setRightBorderColor(style.getRightBorderColor(), cellAddresses, sheet);
        RegionUtil.setTopBorderColor(style.getTopBorderColor(), cellAddresses, sheet);
        RegionUtil.setBottomBorderColor(style.getBottomBorderColor(), cellAddresses, sheet);

        return cellAddresses;
    }

    /**
     * 斜线设置
     * @param isDiagonalUp 斜线方向
     */
    protected void setDiagonal(boolean isDiagonalUp) {
        this.diagonalUp = isDiagonalUp;
        DIAGONAL_COLUMNS.get()
            .merge(this.cell.getColumnIndex(), new HashSet<>(Collections.singletonList(this)), (o, n) -> {
                o.addAll(n);

                return o;
            });
    }

    /**
     * 获得单元格所在的{@link Workbook}
     * @return {@link Workbook}
     */
    protected Workbook getWorkBook() {
        return this.cell.getSheet().getWorkbook();
    }

    /**
     * 移除线程变量
     */
    protected static void removeThreadLocal() {
        DIAGONAL_COLUMNS.remove();
        AT_LEAST_MIN_WIDTH.remove();
    }

    protected static int width(String s) {
        return s.getBytes(StandardCharsets.UTF_8).length + s.length();
    }
}
