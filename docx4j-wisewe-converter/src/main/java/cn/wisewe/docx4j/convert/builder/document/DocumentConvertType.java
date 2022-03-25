package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.office.OfficeConvertHandler;
import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;

/**
 * 支持转换类型
 * @author xiehai
 * @date 2022/03/23 16:03
 */
public enum DocumentConvertType implements OfficeConvertHandler {
    /**
     * pdf转换
     */
    PDF {
        @Override
        public OfficeDocumentHandler getHandler() {
            return PdfHandler.INSTANCE;
        }
    },
    /**
     * html转换
     */
    HTML {
        @Override
        public OfficeDocumentHandler getHandler() {
            return HtmlHandler.INSTANCE;
        }
    }
}
