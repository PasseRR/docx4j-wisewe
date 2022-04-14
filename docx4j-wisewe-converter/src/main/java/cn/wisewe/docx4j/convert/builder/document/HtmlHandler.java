package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.sprie.Warnings;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;

import java.io.OutputStream;

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
        Warnings.HTML_SPAN.remove(os -> {
            document.getHtmlExportOptions().setImageEmbedded(true);
            document.saveToStream(os, FileFormat.Html);
        }, outputStream);
    }
}
