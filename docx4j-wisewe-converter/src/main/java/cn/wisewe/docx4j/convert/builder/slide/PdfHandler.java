package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import static java.awt.geom.AffineTransform.TYPE_GENERAL_SCALE;

/**
 * ppt转pdf处理器
 * @author xiehai
 * @date 2022/03/25 13:24
 */
class PdfHandler extends OfficeDocumentHandler {
    static final PdfHandler INSTANCE = new PdfHandler();

    private PdfHandler() {

    }

    @Override
    protected void handleFlat(BufferedInputStream inputStream, OutputStream outputStream) {
        throw new SlideConvertException("slide not support flat opc xml file");
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
        SlideShow<S, P> ppt, OutputStream outputStream) throws DocumentException, IOException {
        Dimension dimension = ppt.getPageSize();
        double scale = TYPE_GENERAL_SCALE;
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToScale(scale, scale);
        List<? extends Slide<S, P>> slides = ppt.getSlides();
        Document document = new Document();
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            for (Slide<S, P> it : slides) {
                it.getShapes()
                    .stream()
                    .filter(s -> s instanceof XSLFTextShape)
                    .map(XSLFTextShape.class::cast)
                    // 不确定是在Linux系统下有效
                    .forEach(
                        s -> s.getTextParagraphs().forEach(p -> p.getTextRuns().forEach(r -> r.setFontFamily("宋体"))));

                BufferedImage bufferedImage = new BufferedImage(
                    (int) Math.ceil(dimension.width * scale),
                    (int) Math.ceil(dimension.height * scale),
                    BufferedImage.TYPE_INT_RGB
                );
                Graphics2D graphics = bufferedImage.createGraphics();
                DrawFactory.getInstance(graphics).getFontManager(graphics);
                graphics.setTransform(affineTransform);
                if (it instanceof XSLFSlide) {
                    graphics.setPaint(((XSLFSlide) it).getBackground().getFillColor());
                } else if (it instanceof HSLFSlide) {
                    graphics.setPaint(((HSLFSlide) it).getBackground().getFill().getBackgroundColor());
                }
                graphics.fill(new Rectangle2D.Float(0, 0, dimension.width, dimension.height));
                it.draw(graphics);
                Image image = Image.getInstance(bufferedImage, null);
                document.setPageSize(new Rectangle(image.getScaledWidth(), image.getScaledHeight()));
                document.newPage();
                image.setAbsolutePosition(0, 0);
                document.add(image);
            }
        } finally {
            document.close();
            if (Objects.nonNull(writer)) {
                writer.close();
            }
        }
    }
}
