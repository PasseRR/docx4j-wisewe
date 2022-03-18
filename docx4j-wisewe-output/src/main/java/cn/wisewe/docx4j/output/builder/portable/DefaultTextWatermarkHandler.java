package cn.wisewe.docx4j.output.builder.portable;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.stream.IntStream;

/**
 * 默认文字水印
 * @author xiehai
 * @date 2021/01/05 14:00
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultTextWatermarkHandler extends PdfPageEventHelper {
    /**
     * 水印文本
     */
    String text;
    /**
     * 文本旋转度数
     */
    float rotate;
    /**
     * 字体设置
     */
    Font font;

    public DefaultTextWatermarkHandler(String text) {
        this(text, 32);
    }

    public DefaultTextWatermarkHandler(String text, float size) {
        // 默认文字旋转45°
        this(text, size, 45);
    }

    public DefaultTextWatermarkHandler(String text, float size, float rotate) {
        this.text = text;
        this.rotate = rotate;
        // 设置水印字体颜色
        this.font = Fonts.font(size, Font.BOLD);
        // 字体颜色透明度
        this.font.setColor(new GrayColor(0.9F));
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        // TODO 根据纸张大小调整水印
        int count = 10;
        IntStream.range(0, count)
            .forEach(x ->
                IntStream.range(0, count)
                    .forEach(y ->
                        ColumnText.showTextAligned(
                            writer.getDirectContentUnder(),
                            Element.ALIGN_CENTER,
                            new Phrase(this.text, this.font),
                            (50.5F + x * 250.0F),
                            (40.0F + y * 150.0F),
                            this.rotate
                        )
                    )
            );
    }
}
