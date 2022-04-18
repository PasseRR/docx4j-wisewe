package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.builder.SvgTransfer;
import com.spire.presentation.Presentation;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.OutputStream;
import java.util.Optional;

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
        SvgTransfer.create(presentation::saveToSVG)
            .handle((index, it) ->
                Optional.of(it.select("svg > g > text"))
                    .filter(t -> t.size() > 0)
                    .map(Elements::last)
                    .ifPresent(Element::remove)
            )
            .transfer(outputStream);
    }
}
