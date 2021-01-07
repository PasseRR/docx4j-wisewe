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
import lombok.experimental.NonFinal;

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
    Image image;
    @NonFinal
    float width;
    @NonFinal
    float height;

    public DefaultPictureWatermarkHandler(File file) {
        this(file, 0);
    }

    public DefaultPictureWatermarkHandler(File file, float width) {
        try {
            this.image = Image.getInstance(file.toURI().toURL());
        } catch (IOException | BadElementException e) {
            throw new PortableException(e.getMessage(), e);
        }

        this.width = this.image.getWidth();
        this.height = this.image.getHeight();
        // 等宽比调整
        if (width > 0) {
            float scale = width / this.width;
            this.width *= scale;
            this.height *= scale;
        }
    }


    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte contentUnder = writer.getDirectContentUnder();
        int count = 10;
        IntStream.range(0, count)
            .boxed()
            .forEach(x ->
                IntStream.range(0, count)
                    .boxed()
                    .forEach(y -> {
                        Image image = Image.getInstance(this.image);
                        image.scaleAbsoluteWidth(this.width);
                        image.scaleAbsoluteHeight(this.height);
                        image.setRotation(45F);
                        image.setAbsolutePosition(x * this.width * 1.5F + 50.5F, y * this.height * 2.5F + 40.0F);

                        PdfGState state = new PdfGState();
                        // 水印图片透明度
                        state.setFillOpacity(0.3F);

                        contentUnder.saveState();
                        contentUnder.setGState(state);
                        try {
                            contentUnder.addImage(image);
                        } catch (DocumentException e) {
                            throw new PortableException(e.getMessage(), e);
                        }
                        contentUnder.restoreState();
                    })
            );
    }
}
