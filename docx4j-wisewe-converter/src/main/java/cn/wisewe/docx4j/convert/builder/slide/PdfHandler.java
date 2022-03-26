package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.fop.FontUtils;
import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * ppt转pdf处理器
 * @author xiehai
 * @date 2022/03/25 13:24
 */
class PdfHandler extends OfficeDocumentHandler {
    static final PdfHandler INSTANCE = new PdfHandler();

    private PdfHandler() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(FontUtils.defaultFont());
    }

    @Override
    protected void handleFlat(BufferedInputStream inputStream, OutputStream outputStream) {
        // ppt不支持xml格式文件转换
        throw new SlideConvertException("slide file content error");
    }

    @Override
    protected void handleBinary(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        this.handlePoi(new HSLFSlideShow(inputStream), outputStream);
    }

    @Override
    protected void handleZipped(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        this.handlePoi(new XMLSlideShow(inputStream), outputStream);
    }

    /**
     * 处理ppt文件
     * @param ppt          {@link SlideShow}
     * @param outputStream {@link OutputStream}
     * @param <S>          S
     * @param <P>          P
     * @throws DocumentException PDF异常
     */
    protected <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> void handlePoi(
        SlideShow<S, P> ppt, OutputStream outputStream) throws DocumentException {
        Document document = new Document();
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            SlideImageUtils.handleImage(ppt, bufferedImage -> {
                try {
                    Image image = Image.getInstance(bufferedImage, null);
                    document.setPageSize(new Rectangle(image.getScaledWidth(), image.getScaledHeight()));
                    document.newPage();
                    image.setAbsolutePosition(0, 0);
                    document.add(image);
                } catch (Exception e) {
                    throw new SlideConvertException(e);
                }
            });
        } finally {
            document.close();
            if (Objects.nonNull(writer)) {
                writer.close();
            }
        }
    }
}
