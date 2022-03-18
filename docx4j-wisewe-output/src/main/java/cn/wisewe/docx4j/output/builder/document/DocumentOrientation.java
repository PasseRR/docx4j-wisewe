package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

/**
 * Word文档方向
 * @author xiehai
 * @date 2022/03/18 13:52
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public enum DocumentOrientation {
    /**
     * 纵向
     */
    PORTRAIT(1),
    /**
     * 横向
     */
    LANDSCAPE(2) {
        @Override
        void apply(CTPageSz pageSz, long width, long height) {
            super.apply(pageSz, width, height);
            pageSz.setH(width);
            pageSz.setW(height);
        }
    };

    /**
     * 枚举值
     */
    int value;

    void apply(CTPageSz pageSz, long width, long height) {
        pageSz.setW(width);
        pageSz.setH(height);
        pageSz.setOrient(STPageOrientation.Enum.forInt(this.value));
    }
}
