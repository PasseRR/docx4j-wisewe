package cn.wisewe.docx4j.output.builder.portable;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

/**
 * 默认图片水印
 * @author xiehai
 * @date 2021/01/05 14:00
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultPictureWatermarkHandler extends PdfPageEventHelper {
    /**
     * 水印图片
     */
    Image image;
    /**
     * 图片宽度
     */
    float width;
    /**
     * 图片高度
     */
    float height;
    /**
     * 图片旋转度数
     */
    float rotate;

    public DefaultPictureWatermarkHandler(File file) {
        this(file, 0);
    }

    public DefaultPictureWatermarkHandler(File file, float width) {
        this(file, width, 45F);
    }

    public DefaultPictureWatermarkHandler(File file, float width, float rotate) {
        try {
            this.image = Image.getInstance(file.toURI().toURL());
        } catch (IOException | BadElementException e) {
            throw new PortableExportException(e);
        }

        float w = this.image.getWidth(), h = this.image.getHeight();
        // 等宽比调整
        if (width > 0) {
            float scale = width / w;
            w *= scale;
            h *= scale;
        }

        this.width = w;
        this.height = h;
        this.rotate = rotate;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        // 长度按照对角线计算
        float x = (float) Math.sqrt(this.width * this.width + this.height * this.height),
            // 间隔长度为0.5倍长度
            interval = x * 0.5F,
            // 加间隔长度
            y = x + interval;
        float w = document.getPageSize().getWidth(), h = document.getPageSize().getHeight();
        int row = (int) (h / y) + 2, column = (int) (w / y) + 2;
        // 多余宽度及高度
        float lx = (w % y) / row, ly = (h % y) / column;
        PdfContentByte contentUnder = writer.getDirectContentUnder();
        
        IntStream.rangeClosed(1, row)
            .boxed()
            .forEach(r ->
                IntStream.rangeClosed(1, column)
                    .boxed()
                    .forEach(c -> {
                        Image image = Image.getInstance(this.image);
                        image.scaleAbsoluteWidth(this.width);
                        image.scaleAbsoluteHeight(this.height);
                        image.setRotation(this.rotate);
                        image.setAbsolutePosition((c - 1) * (lx + y), (r - 1) * (ly + y));

                        PdfGState state = new PdfGState();
                        // 水印图片透明度
                        state.setFillOpacity(0.3F);

                        contentUnder.saveState();
                        contentUnder.setGState(state);
                        try {
                            contentUnder.addImage(image);
                        } catch (DocumentException e) {
                            throw new PortableExportException(e);
                        }
                        contentUnder.restoreState();
                    })
            );
    }
}
