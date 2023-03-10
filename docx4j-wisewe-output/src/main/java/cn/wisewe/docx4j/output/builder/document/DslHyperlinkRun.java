package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;

import java.util.function.Consumer;

/**
 * 超链接 {@link XWPFHyperlinkRun}
 * @author xiehai
 * @date 2020/12/25 17:21
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslHyperlinkRun {
    XWPFHyperlinkRun hyperlinkRun;

    DslHyperlinkRun(XWPFHyperlinkRun hyperlinkRun) {
        this.hyperlinkRun = hyperlinkRun;
    }

    /**
     * 超链接更多设置
     * @param consumer 超链接消费
     * @return {@link DslHyperlinkRun}
     */
    public DslHyperlinkRun accept(Consumer<XWPFHyperlinkRun> consumer) {
        consumer.accept(this.hyperlinkRun);
        return this;
    }

    /**
     * 超链接更多设置，兼容方法使用{@link #accept(Consumer)}代替，后期会删除此方法
     * @param consumer 超链接设置
     * @return {@link DslHyperlinkRun}
     */
    public DslHyperlinkRun more(Consumer<XWPFHyperlinkRun> consumer) {
        return this.accept(consumer);
    }

    /**
     * 快速创建超链接
     * @param text 超链接文本
     * @return {@link DslHyperlinkRun}
     */
    public DslHyperlinkRun text(String text) {
        return this.accept(r -> r.setText(text));
    }
}
