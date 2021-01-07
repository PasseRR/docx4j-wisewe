package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.util.function.Consumer;

/**
 * word文档段落dsl{@link XWPFParagraph}
 * @author xiehai
 * @date 2020/12/25 10:38
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslParagraph {
    XWPFParagraph paragraph;

    DslParagraph(XWPFParagraph paragraph) {
        this.paragraph = paragraph;
    }

    /**
     * 段落更多设置
     * @param consumer 段落消费
     * @return {@link DslParagraph}
     */
    public DslParagraph more(Consumer<XWPFParagraph> consumer) {
        consumer.accept(this.paragraph);
        return this;
    }

    /**
     * 段落文本设置
     * @param consumer 文本消费
     * @return {@link DslParagraph}
     */
    public DslParagraph run(Consumer<DslRun> consumer) {
        return this.more(p -> consumer.accept(new DslRun(p.createRun())));
    }

    /**
     * 段落文本设置 快速设置正文
     * @param text 文本内容
     * @return {@link DslParagraph}
     */
    public DslParagraph run(String text) {
        return this.run(r -> r.text(text));
    }

    /**
     * 段落超链接设置
     * @param url      超链接url
     * @param consumer 超链接消费
     * @return {@link DslParagraph}
     */
    public DslParagraph hyperlinkRun(String url, Consumer<DslHyperlinkRun> consumer) {
        consumer.accept(new DslHyperlinkRun(this.paragraph.createHyperlinkRun(url)));
        return this;
    }

    /**
     * 段落快速设置超链接
     * @param url  超链接url
     * @param text 超链接文本
     * @return {@link DslParagraph}
     */
    public DslParagraph hyperlinkRun(String url, String text) {
        return this.hyperlinkRun(url, r -> r.text(text));
    }
}
