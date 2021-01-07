package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.OutputConstants;
import cn.wisewe.docx4j.output.utils.StringConverterUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.PackagePrivate;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
    /**
     * 使用ThreadLocal缓存最大列宽 每个sheet渲染结束时回收
     */
    protected static final ThreadLocal<Map<Integer, Integer>> COLUMN_WIDTH = ThreadLocal.withInitial(HashMap::new);

    DslCell(Cell cell) {
        this.cell = cell;
    }

    /**
     * 设置单元格内容
     * @param o 单元格内容对象
     * @return {@link DslCell}
     */
    public DslCell text(Object o) {
        if (Objects.isNull(o)) {
            this.cell.setBlank();
            return this;
        }

        return this.doSetText(StringConverterUtil.convert(o));
    }

    /**
     * 设置单元格内容
     * @param supplier 单元格内容提供器
     * @return {@link DslCell}
     */
    public DslCell text(Supplier<Object> supplier) {
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
            anchor.setCol1(this.cell.getColumnIndex());
            anchor.setRow1(this.cell.getRowIndex());
            // 宽度单位转像素
            double cellHeight = Units.columnWidthToEMU(height * 256) / (Units.EMU_PER_PIXEL * 1.0);
            // 以图片高度设置单元格高度
            this.cell.getRow().setHeightInPoints((float) Units.pixelToPoints(cellHeight));
            // 插入图片，如果原图宽度大于最终要求的图片宽度，就按比例缩小，否则展示原图
            drawing.createPicture(anchor, index).resize(1, 1);
            // 设置列最大宽度
            this.doUpdateLength(width);
        } catch (IOException e) {
            throw new SpreadSheetException(e.getMessage(), e);
        }

        return this;
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
     * 设置单元格样式
     * @param style {@link CellStyle}
     * @return {@link DslCell}
     */
    public DslCell style(CellStyle style) {
        this.cell.setCellStyle(style);

        return this;
    }

    /**
     * 单元格合并范围
     * @return {@link CellRangeAddress}
     */
    protected CellRangeAddress getCellRangeAddress() {
        return
            new CellRangeAddress(
                this.cell.getRowIndex(),
                this.cell.getRowIndex() + this.rowspan - 1,
                this.cell.getColumnIndex(),
                this.cell.getColumnIndex() + this.colspan - 1
            );
    }

    /**
     * 设置单元格内容并缓存列最长字符串字节数
     * @param text 内容
     * @return {@link DslCell}
     */
    protected DslCell doSetText(String text) {
        this.cell.setCellValue(text);

        Integer length = Optional.of(text)
            .filter(it -> it.contains(OutputConstants.BREAK_LINE))
            .map(it ->
                // 若存在换行符
                Stream.of(it.split(OutputConstants.BREAK_LINE))
                    .mapToInt(line -> line.getBytes().length)
                    .boxed()
                    .max(Comparator.comparingInt(t -> t))
                    .orElse(0)
            )
            .orElseGet(() -> text.getBytes().length);

        return this.doUpdateLength(length);
    }

    /**
     * 更新最大宽度
     * @param length 宽度
     * @return {@link DslCell}
     */
    protected DslCell doUpdateLength(int length) {
        COLUMN_WIDTH.get().merge(this.cell.getColumnIndex(), length, Math::max);
        return this;
    }

    /**
     * 默认表头样式
     * @return {@link DslCell}
     */
    protected DslCell defaultHeadStyle() {
        // 表头字体加粗
        Font font = this.getWorkBook().createFont();
        font.setBold(true);

        // 单元格居中 表头背景色
        CellStyle cellStyle = this.getWorkBook().createCellStyle();
        cellStyle.setFont(font);
        // 设置表头背景色
        cellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置表头边线及边线颜色
        BorderStyle borderStyle = BorderStyle.THIN;
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        short borderColor = IndexedColors.GREY_25_PERCENT.getIndex();
        cellStyle.setTopBorderColor(borderColor);
        cellStyle.setBottomBorderColor(borderColor);
        cellStyle.setLeftBorderColor(borderColor);
        cellStyle.setRightBorderColor(borderColor);
        // 内容对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return this.style(cellStyle);
    }

    /**
     * 默认数据行样式
     * @return {@link DslCell}
     */
    protected DslCell defaultDataStyle() {
        // 单元格居中
        CellStyle cellStyle = this.getWorkBook().createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 自动换行仅对数据支持
        cellStyle.setWrapText(true);

        return this.style(cellStyle);
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
        COLUMN_WIDTH.remove();
    }
}
