package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import java.math.BigInteger;

/**
 * word文档纸张设置
 * @author xiehai
 * @date 2022/03/18 16:54
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentRectangle {
    /**
     * 宽度
     */
    long w;
    /**
     * 高度
     */
    long h;
    /**
     * 0 横向 1 纵向
     */
    int orientation;

    public DocumentRectangle(long w, long h) {
        this.w = w;
        this.h = h;
        this.orientation = 0;
    }

    public DocumentRectangle rotate() {
        long t = this.w;
        this.w = this.h;
        this.h = t;
        this.orientation ^= 1;

        return this;
    }

    void apply(CTPageSz pageSz) {
        pageSz.setW(BigInteger.valueOf(this.w));
        pageSz.setH(BigInteger.valueOf(this.h));
        pageSz.setOrient(STPageOrientation.Enum.forInt(this.orientation + 1));
    }
}
