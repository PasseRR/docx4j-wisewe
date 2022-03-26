package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.office.OfficeConvertHandler;
import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;

/**
 * ppt转换文件类型支持
 * @author xiehai
 * @date 2022/03/25 13:25
 */
public enum SlideConvertType implements OfficeConvertHandler {
    /**
     * ppt转pdf
     */
    PDF {
        @Override
        public OfficeDocumentHandler getHandler() {
            return PdfHandler.INSTANCE;
        }
    },
    /**
     * ppt转html
     */
    HTML {
        @Override
        public OfficeDocumentHandler getHandler() {
            return HtmlHandler.INSTANCE;
        }
    }
}
