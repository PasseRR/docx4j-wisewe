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
    float rotate;
    /**
     * 文本旋转度数
     */
    double radians;
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
        this.radians = Math.toRadians(rotate);
        // 设置水印字体颜色
        this.font = Fonts.font(size, Font.BOLD);
        // 字体颜色透明度
        this.font.setColor(new GrayColor(0.9F));
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        // x、y轴间隔为字体大小的1.5倍
        float size = this.font.getSize(), interval = size * 1.5F, max = size * text.length();
        // 根据三角函数计算字体x轴和y轴高度
        float x = Float.max((float) Math.cos(this.radians) * max, size) + interval,
            y = Float.max((float) Math.sin(this.radians) * max, size) + interval;
        // 根据纸张的长度和宽度计算最多迭代次数
        float w = document.getPageSize().getWidth(), h = document.getPageSize().getHeight();
        // 多绘制两次 使得水印不工整
        int row = (int) (h / y) + 2, column = (int) (w / x) + 2;
        // 不够整除的多余宽度及高度把起作为间隔
        float lx = (w % x) / column, ly = (h % x) / row;

        IntStream.rangeClosed(1, row)
            .forEach(r ->
                IntStream.rangeClosed(1, column)
                    .forEach(c ->
                        ColumnText.showTextAligned(
                            writer.getDirectContentUnder(),
                            Element.ALIGN_CENTER,
                            new Phrase(this.text, this.font),
                            (c - 1) * (lx + x),
                            (r - 1) * (ly + y),
                            this.rotate
                        )
                    )
            );
    }
}
