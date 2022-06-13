package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.builder.HtmlTransfer;
import com.aspose.words.Document;
import com.aspose.words.HtmlSaveOptions;

import java.io.OutputStream;

/**
 * word文档转html处理器
 * @author xiehai
 * @date 2022/03/25 10:00
 */
class HtmlHandler extends DocumentHandler {
    static final HtmlHandler INSTANCE = new HtmlHandler();

    private HtmlHandler() {

    }

    @Override
    protected void postHandle(Document document, OutputStream outputStream) throws Exception {
        HtmlSaveOptions options = new HtmlSaveOptions();
        options.setExportImagesAsBase64(true);
        options.setExportPageMargins(true);
        // 添加移动端html支持
        HtmlTransfer.create(os -> {
                try {
                    document.save(os, options);
                } catch (Exception e) {
                    throw new DocumentConvertException(e);
                }
            })
            .transfer(outputStream);
    }
}
