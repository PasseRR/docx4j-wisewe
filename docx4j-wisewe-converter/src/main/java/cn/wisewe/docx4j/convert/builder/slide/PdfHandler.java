package cn.wisewe.docx4j.convert.builder.slide;

import com.spire.presentation.FileFormat;
import com.spire.presentation.Presentation;

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
    protected void postHandle(Presentation presentation, OutputStream outputStream) {
        // TODO pdf移除
        presentation.saveToFile(outputStream, FileFormat.PDF);
    }
}
