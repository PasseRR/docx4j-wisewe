package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

/**
 * word页边距
 * @author xiehai
 * @date 2022/06/13 10:22
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public enum DocumentPaperPadding {
    /**
     * 常规
     */
    NORMAL(2.54D, 3.18D),
    /**
     * 窄
     */
    NARROW(1.27D, 1.27D),
    /**
     * 中等
     */
    MEDIUM(2.54D, 1.91D),
    /**
     * 宽
     */
    WIDE(2.54D, 5.08D),
    /**
     * 对称
     */
    SYMMETRICAL(2.54D, 2.54D, 3.18D, 2.54D);
    /**
     * 上边距
     */
    double top;
    /**
     * 下边距
     */
    double bottom;
    /**
     * 左边距
     */
    double left;
    /**
     * 右边距
     */
    double right;

    /**
     * mm与像素转换基础
     */
    static final double BASE = 28.3D;

    DocumentPaperPadding(double topAndBottom, double leftAndRight) {
        this.top = this.bottom = topAndBottom;
        this.left = this.right = leftAndRight;
    }

    /**
     * 左右边距像素值
     * @return 像素值
     */
    public double widthPadding() {
        return DocumentPaperPadding.padding(this.left, this.right);
    }

    /**
     * 上下边距像素值
     * @return 像素值
     */
    public double heightPadding() {
        return DocumentPaperPadding.padding(this.top, this.bottom);
    }

    /**
     * 计算指定纸张的实际宽度像素
     * @param size 纸张
     * @return 实际宽度像素值
     */
    public double widthPixel(DocumentPaperSize size) {
        return size.width - this.widthPadding();
    }

    /**
     * 计算指定纸张的实际高度
     * @param size 纸张
     * @return 实际高度像素值
     */
    public double heightPixel(DocumentPaperSize size) {
        return size.height - this.heightPadding();
    }

    /**
     * 根据指定长度 计算像素
     * @param values 多个值
     * @return 像素
     */
    public static double padding(double... values) {
        double sum = 0D;
        if (Objects.nonNull(values)) {
            for (double value : values) {
                sum += value;
            }
        }

        return sum * BASE;
    }
}
