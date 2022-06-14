package cn.wisewe.docx4j.convert.builder.slide;

import com.aspose.slides.SaveFormat;

import java.io.OutputStream;

/**
 * ppt转pdf处理器
 * @author xiehai
 * @date 2022/03/25 13:24
 */
class PdfHandler extends SlideHandler {
    static final PdfHandler INSTANCE = new PdfHandler();

    private PdfHandler() {
    }

    @Override
    protected void postHandle(com.aspose.slides.Presentation presentation, OutputStream outputStream) throws Exception {
        presentation.save(outputStream, SaveFormat.Pdf);
    }
}
