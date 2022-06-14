package cn.wisewe.docx4j.convert.builder.portable;

import com.aspose.pdf.Document;
import com.aspose.pdf.SaveFormat;

import java.io.OutputStream;

/**
 * pdf转docx处理器
 * @author xiehai
 * @date 2022/06/14 09:27
 */
class DocxHandler extends PortableHandler {
    static final PortableHandler INSTANCE = new DocxHandler();
    
    @Override
    protected void postHandle(Document document, OutputStream outputStream) throws Exception {
        document.save(outputStream, SaveFormat.DocX);
    }
}
