package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.builder.SvgTransfer;
import com.spire.doc.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.OutputStream;
import java.util.Optional;

/**
 * word转pdf文档处理器
 * @author xiehai
 * @date 2022/03/25 10:10
 */
class PdfHandler extends DocumentHandler {
    static final PdfHandler INSTANCE = new PdfHandler();

    private PdfHandler() {

    }

    @Override
    protected void postHandle(Document document, OutputStream outputStream) {
        SvgTransfer.create(document::saveToSVG)
            .handle((index, it) -> {
                // 大小写敏感 标签替换
                it.select("clippath").tagName("clipPath");
                if (index == 0) {
                    // 前68个都是警告信息文本
                    it.select("svg > g > g:lt(68)").remove();
                } else {
                    Optional.of(it.select("svg > g > g"))
                        .filter(e -> e.size() > 0)
                        .map(Elements::last)
                        .ifPresent(Element::remove);
                }
            })
            .transfer(outputStream);
    }
}
