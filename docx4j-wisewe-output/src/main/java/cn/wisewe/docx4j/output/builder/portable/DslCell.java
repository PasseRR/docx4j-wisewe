package cn.wisewe.docx4j.output.builder.portable;

import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.function.Consumer;

/**
 * pdf表格单元格dsl
 * @author xiehai
 * @date 2021/01/04 19:09
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslCell extends PortableDocument<DslCell> {
    PdfPCell cell;

    DslCell(PdfPCell cell) {
        this.cell = cell;
    }

    /**
     * 单元格设置
     * @param consumer 设置方法
     * @return {@link DslCell}
     */
    public DslCell accept(Consumer<PdfPCell> consumer) {
        consumer.accept(this.cell);
        return this;
    }

    /**
     * 单元格设置 兼容方法 使用{@link #accept(Consumer)}代替，后期会移除此方法
     * @param consumer 设置方法
     * @return {@link DslCell}
     */
    public DslCell more(Consumer<PdfPCell> consumer) {
        return this.accept(consumer);
    }

    /**
     * 合并列
     * @param colspan 合并列数
     * @return {@link DslCell}
     */
    public DslCell colspan(int colspan) {
        return this.accept(c -> c.setColspan(colspan));
    }

    /**
     * 合并行
     * @param rowspan 合并行数
     * @return {@link DslCell}
     */
    public DslCell rowspan(int rowspan) {
        return this.accept(c -> c.setRowspan(rowspan));
    }

    @Override
    void addElement(Element element) {
        this.cell.addElement(element);
    }
}
