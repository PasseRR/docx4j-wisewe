package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.builder.HtmlTransfer;
import com.spire.presentation.FileFormat;
import com.spire.presentation.Presentation;
import org.jsoup.nodes.Node;

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
        presentation.getSaveToHtmlOption().setCenter(true);
        HtmlTransfer.create(os -> presentation.saveToFile(os, FileFormat.HTML))
            .handle(document ->
                document.body()
                    .select("div > svg")
                    .forEach(it -> {
                        it.attr("width", "100%");
                        it.attr("height", "100%");
                        it.select("svg > g > text").forEach(Node::remove);
                    })
            )
            .transfer(outputStream);
    }
}
