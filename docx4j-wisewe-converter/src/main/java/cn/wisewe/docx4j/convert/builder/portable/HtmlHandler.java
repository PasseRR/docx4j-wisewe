package cn.wisewe.docx4j.convert.builder.portable;

import cn.wisewe.docx4j.convert.base.ConvertHandler;
import cn.wisewe.docx4j.convert.sprie.Warnings;
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
        Warnings.HTML_PDF.remove(os -> document.saveToStream(os, FileFormat.HTML), outputStream);
    }
}
