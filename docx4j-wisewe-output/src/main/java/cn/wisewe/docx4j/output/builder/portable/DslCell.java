package cn.wisewe.docx4j.output.builder.portable;

import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

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
     * 合并列
     * @param colspan 合并列数
     * @return {@link DslCell}
     */
    public DslCell colspan(int colspan) {
        this.cell.setColspan(colspan);
        return this;
    }

    /**
     * 合并行
     * @param rowspan 合并行数
     * @return {@link DslCell}
     */
    public DslCell rowspan(int rowspan) {
        this.cell.setRowspan(rowspan);
        return this;
    }

    @Override
    void addElement(Element element) {
        this.cell.addElement(element);
    }
}
