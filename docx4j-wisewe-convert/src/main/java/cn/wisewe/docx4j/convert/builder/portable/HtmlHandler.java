package cn.wisewe.docx4j.convert.builder.portable;

import cn.wisewe.docx4j.convert.builder.HtmlTransfer;
import com.aspose.pdf.Document;
import com.aspose.pdf.HtmlSaveOptions;
import com.aspose.pdf.SaveOptions;

import java.io.OutputStream;

/**
 * pdf转html处理器
 * @author xiehai
 * @date 2022/04/17 19:01
 */
class HtmlHandler extends PortableHandler {
    static final PortableHandler INSTANCE = new HtmlHandler();
    private static final HtmlSaveOptions HTML_SAVE_OPTIONS = new HtmlSaveOptions();

    static {
        HTML_SAVE_OPTIONS.setFixedLayout(true);
        HTML_SAVE_OPTIONS.setPageBorderIfAny(new SaveOptions.BorderInfo());
        HTML_SAVE_OPTIONS.setPartsEmbeddingMode(HtmlSaveOptions.PartsEmbeddingModes.EmbedAllIntoHtml);
        HTML_SAVE_OPTIONS.setRasterImagesSavingMode(
            HtmlSaveOptions.RasterImagesSavingModes.AsEmbeddedPartsOfPngPageBackground
        );
    }

    private HtmlHandler() {

    }

    @Override
    protected void postHandle(Document document, OutputStream outputStream) {
        HtmlTransfer.create(os -> document.save(os, HTML_SAVE_OPTIONS)).transfer(outputStream);
    }
}
