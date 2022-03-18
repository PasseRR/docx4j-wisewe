package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;

/**
 * word文档纸张
 * @author xiehai
 * @date 2022/03/18 13:29
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public enum DocumentPaperSize {
    /**
     * 纸张大小不能超过558.5mm×558.5mm
     */
    A1(1685, 2384),
    A2(1190, 1684),
    A3(842, 1190),
    A4(595, 842),
    A5(420, 595),
    B4(729, 1032),
    B5(516, 729),
    LETTER(612, 792),
    TABLOID(792, 1224),
    LEDGER(1224, 792),
    LEGAL(612, 1008),
    STATEMENT(396, 612),
    EXECUTIVE(540, 720),
    FOLIO(612, 936),
    QUARTO(610, 780),
    TEN_X_FOURTEEN(720, 1008);

    int width;
    int height;

    static final long BASE = 20L;

    void apply(CTPageSz pageSz, DocumentOrientation orientation) {
        orientation.apply(pageSz, this.width * BASE, this.height * BASE);
    }
}
