package cn.wisewe.docx4j.convert.builder.portable;

import com.aspose.pdf.Document;
import com.aspose.pdf.SaveFormat;

import java.io.OutputStream;

/**
 * pdf转doc
 * @author xiehai
 * @date 2022/06/14 09:28
 */
public class DocHandler extends PortableHandler {
    static final PortableHandler INSTANCE = new DocHandler();

    @Override
    protected void postHandle(Document document, OutputStream outputStream) throws Exception {
        document.save(outputStream, SaveFormat.Doc);
    }
}
