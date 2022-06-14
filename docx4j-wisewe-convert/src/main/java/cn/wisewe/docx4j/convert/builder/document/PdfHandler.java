package cn.wisewe.docx4j.convert.builder.document;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;

import java.io.OutputStream;

/**
 * word转pdf文档处理器
 * @author xiehai
 * @date 2022/03/25 10:10
 */
class PdfHandler extends DocumentHandler {
    static final DocumentHandler INSTANCE = new PdfHandler();

    private PdfHandler() {

    }

    @Override
    protected void postHandle(Document document, OutputStream outputStream) throws Exception {
        document.save(outputStream, SaveFormat.PDF);
    }
}
