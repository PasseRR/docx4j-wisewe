package cn.wisewe.docx4j.output.builder.portable;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * 默认文本页眉
 * @author xiehai
 * @date 2021/01/05 11:17
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultTextHeaderHandler extends PdfPageEventHelper {
    String text;

    public DefaultTextHeaderHandler(String text) {
        this.text = text;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(
            writer.getDirectContent(),
            Element.ALIGN_CENTER,
            new Phrase(this.text, Fonts.HEADER_FOOTER.font()),
            document.getPageSize().getWidth() / 2.0F,
            document.getPageSize().getHeight() - 10.0F,
            0.0F
        );
    }
}
