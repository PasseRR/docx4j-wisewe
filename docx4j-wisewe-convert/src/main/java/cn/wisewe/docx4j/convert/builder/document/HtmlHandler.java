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
    static final DocumentHandler INSTANCE = new HtmlHandler();
    private static final HtmlSaveOptions HTML_SAVE_OPTIONS = new HtmlSaveOptions();

    static {
        HTML_SAVE_OPTIONS.setExportImagesAsBase64(true);
        HTML_SAVE_OPTIONS.setExportPageMargins(true);
    }

    private HtmlHandler() {

    }

    @Override
    protected void postHandle(Document document, OutputStream outputStream) throws Exception {
        // 添加移动端html支持
        HtmlTransfer.create(os -> {
                try {
                    document.save(os, HTML_SAVE_OPTIONS);
                } catch (Exception e) {
                    throw new DocumentConvertException(e);
                }
            })
            .transfer(outputStream);
    }
}
