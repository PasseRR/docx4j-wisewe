package cn.wisewe.docx4j.convert.builder.portable;

import cn.wisewe.docx4j.convert.ConvertHandler;
import cn.wisewe.docx4j.convert.builder.HtmlTransfer;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;

import java.io.BufferedInputStream;
import java.io.OutputStream;

/**
 * pdf转html处理器
 * @author xiehai
 * @date 2022/04/17 19:01
 */
class HtmlHandler implements ConvertHandler {
    static final HtmlHandler INSTANCE = new HtmlHandler();

    @Override
    public void handle(BufferedInputStream inputStream, OutputStream outputStream) {
        PdfDocument document = new PdfDocument(inputStream);
        document.getConvertOptions().setOutputToOneSvg(true);

        HtmlTransfer.create(os -> document.saveToStream(os, FileFormat.HTML))
            .handle(d -> {
                // 内容居中
                d.body().attr("style", "text-align: center");
                // 警告信息移除
                d.body().select("svg > g > g:eq(1)").remove();
            })
            .transfer(outputStream);
    }
}
