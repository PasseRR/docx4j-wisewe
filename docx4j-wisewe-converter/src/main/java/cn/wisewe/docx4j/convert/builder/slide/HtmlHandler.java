package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.sprie.Warnings;
import com.spire.presentation.FileFormat;
import com.spire.presentation.Presentation;

import java.io.OutputStream;

/**
 * ppt转html 使用图片
 * @author xiehai
 * @date 2022/03/26 14:45
 */
class HtmlHandler extends SlideHandler {
    static final HtmlHandler INSTANCE = new HtmlHandler();

    private HtmlHandler() {

    }

    @Override
    protected void postHandle(Presentation presentation, OutputStream outputStream) {
        Warnings.HTML_SLIDE.remove(os -> {
            presentation.getSaveToHtmlOption().setCenter(true);
            presentation.saveToFile(os, FileFormat.HTML);
        }, outputStream);
    }
}
