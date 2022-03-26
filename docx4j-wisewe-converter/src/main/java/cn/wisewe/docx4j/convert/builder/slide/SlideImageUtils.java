package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.fop.FontUtils;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

/**
 * ppt图片工具
 * @author xiehai
 * @date 2022/03/26 16:14
 */
interface SlideImageUtils {
    /**
     * ppt转图片
     * @param ppt      {@link SlideShow}
     * @param consumer 图片后续处理
     * @param <S>      S
     * @param <P>      P
     */
    static <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> void handleImage(
        SlideShow<S, P> ppt, Consumer<BufferedImage> consumer) {
        Dimension dimension = ppt.getPageSize();
        double scale = AffineTransform.TYPE_GENERAL_SCALE;
        int width = (int) (dimension.width * scale), height = (int) (dimension.height * scale);
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToScale(scale, scale);
        List<? extends Slide<S, P>> slides = ppt.getSlides();
        for (Slide<S, P> it : slides) {
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.setTransform(affineTransform);
            if (it instanceof XSLFSlide) {
                graphics.setPaint(((XSLFSlide) it).getBackground().getFillColor());
                it.getShapes()
                    .stream()
                    .filter(s -> s instanceof XSLFTextShape)
                    .map(XSLFTextShape.class::cast)
                    // 设置中文字体支持
                    .forEach(s ->
                        s.getTextParagraphs()
                            .forEach(p ->
                                p.getTextRuns().forEach(r -> r.setFontFamily(FontUtils.defaultFontName()))
                            )
                    );
            } else if (it instanceof HSLFSlide) {
                graphics.setPaint(((HSLFSlide) it).getBackground().getFill().getBackgroundColor());
            }
            graphics.fill(new Rectangle2D.Float(0, 0, dimension.width, dimension.height));
            it.draw(graphics);
            consumer.accept(bufferedImage);
        }
    }
}
