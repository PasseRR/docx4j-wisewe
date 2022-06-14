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

    @Override
    protected void postHandle(Document document, OutputStream outputStream) {
        HtmlSaveOptions options = new HtmlSaveOptions();
        options.setFixedLayout(true);
        options.setPageBorderIfAny(new SaveOptions.BorderInfo());
        options.setPartsEmbeddingMode(HtmlSaveOptions.PartsEmbeddingModes.EmbedAllIntoHtml);
        options.setRasterImagesSavingMode(HtmlSaveOptions.RasterImagesSavingModes.AsEmbeddedPartsOfPngPageBackground);
        HtmlTransfer.create(os -> document.save(os, options))
            .transfer(outputStream);
    }
}
