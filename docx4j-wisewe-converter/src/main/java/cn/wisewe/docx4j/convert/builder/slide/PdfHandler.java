package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.sprie.Warnings;
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
        Warnings.PDF.remove(os -> presentation.saveToFile(os, FileFormat.PDF), outputStream);
    }
}
