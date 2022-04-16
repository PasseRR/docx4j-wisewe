package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.sprie.Warnings;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;

import java.io.OutputStream;

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
        Warnings.PDF.remove(os -> document.saveToStream(os, FileFormat.PDF), outputStream);
    }
}
