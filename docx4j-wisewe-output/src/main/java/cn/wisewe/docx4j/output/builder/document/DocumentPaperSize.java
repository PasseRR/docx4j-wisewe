package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * word文档纸张
 * <a href="https://www.gnu.org/software/gv/manual/html_node/Paper-Keywords-and-paper-size-in-points.html">参考</a>
 * @author xiehai
 * @date 2022/03/18 13:29
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
@Getter
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

    DocumentRectangle rectangle() {
        return new DocumentRectangle(this.width * BASE, this.height * BASE);
    }

    public DocumentRectangle rotate() {
        return this.rectangle().rotate();
    }

    /**
     * 计算指定纸张的实际宽度像素
     * @param padding 边距类型
     * @return 实际宽度像素值
     */
    public double widthPixel(DocumentPaperPadding padding) {
        return this.width - padding.widthPixel();
    }

    /**
     * 默认宽度
     * @return 宽度像素
     */
    public double widthPixel() {
        return this.widthPixel(DocumentPaperPadding.NORMAL);
    }

    /**
     * 计算指定纸张的实际高度
     * @param padding 边距类型
     * @return 实际高度像素值
     */
    public double heightPixel(DocumentPaperPadding padding) {
        return this.height - padding.heightPixel();
    }

    /**
     * 默认高度
     * @return 高度像素
     */
    public double heightPixel() {
        return this.heightPixel(DocumentPaperPadding.NORMAL);
    }
}
