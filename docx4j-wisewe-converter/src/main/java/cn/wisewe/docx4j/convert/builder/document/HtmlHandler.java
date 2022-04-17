package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.builder.HtmlTransfer;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import org.jsoup.nodes.Node;

import java.io.OutputStream;
import java.util.Optional;

/**
 * word文档转html处理器
 * @author xiehai
 * @date 2022/03/25 10:00
 */
class HtmlHandler extends DocumentHandler {
    static final HtmlHandler INSTANCE = new HtmlHandler();

    private HtmlHandler() {

    }

    @Override
    protected void postHandle(Document document, OutputStream outputStream) {
        document.getHtmlExportOptions().setImageEmbedded(true);
        HtmlTransfer.create(os -> document.saveToStream(os, FileFormat.Html))
            .handle(d -> {
                Optional.of(d.body().getElementsByTag("p"))
                    .filter(it -> it.size() > 0)
                    .map(it -> it.get(0))
                    .ifPresent(Node::remove);

                Optional.of(d.body().getElementsByAttributeValue("style", "min-height:72pt"))
                    .filter(it -> it.size() > 0)
                    .map(it -> it.get(0))
                    .ifPresent(it -> it.removeAttr("style"));
            })
            .transfer(outputStream);
    }
}
