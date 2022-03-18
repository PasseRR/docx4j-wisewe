package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.util.function.Consumer;

/**
 * 页眉
 * @author xiehai
 * @date 2020/12/25 15:23
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslHeader extends RichableDocument<DslHeader> {
    XWPFHeader header;

    DslHeader(XWPFHeader header) {
        this.header = header;
    }

    /**
     * 页眉更多设置
     * @param consumer 设置方法
     * @return {@link DslHeader}
     */
    public DslHeader accept(Consumer<XWPFHeader> consumer) {
        consumer.accept(this.header);
        return this;
    }

    /**
     * 默认居中页眉文本
     * @param text 文本内容
     * @return {@link DslHeader}
     */
    public DslHeader text(String text) {
        return this.paragraph(p -> p.run(text).accept(xp -> xp.setAlignment(ParagraphAlignment.CENTER)));
    }

    @Override
    protected XWPFParagraph createParagraph() {
        return this.header.createParagraph();
    }

    @Override
    protected XWPFTable createTable(int rows, int columns) {
        return this.header.createTable(rows, columns);
    }
}
