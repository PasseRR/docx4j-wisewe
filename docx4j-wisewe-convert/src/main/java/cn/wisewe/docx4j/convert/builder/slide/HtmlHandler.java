package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.builder.HtmlTransfer;
import com.aspose.slides.SaveFormat;

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
    protected void postHandle(com.aspose.slides.Presentation presentation, OutputStream outputStream) throws Exception {
        // 添加移动端标签支持
        HtmlTransfer.create(os -> presentation.save(outputStream, SaveFormat.Html)).transfer(outputStream);
    }
}
