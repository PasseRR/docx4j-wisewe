package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.sprie.HtmlWarnings;
import com.spire.presentation.FileFormat;
import com.spire.presentation.Presentation;
import org.jsoup.Jsoup;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
        presentation.saveToSVG()
            .forEach(it -> {
                // TODO 考虑使用svg转pdf
                String svg = new String(it, StandardCharsets.UTF_8);
                Jsoup.parse(svg);
            });
        // TODO pdf移除
        HtmlWarnings.PDF.remove(os -> presentation.saveToFile(os, FileFormat.PDF), outputStream);
    }
}
