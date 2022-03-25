package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.builder.OfficeDocumentHandler;

/**
 * 支持转换类型
 * @author xiehai
 * @date 2022/03/23 16:03
 */
public enum DocumentConvertType {
    /**
     * pdf转换
     */
    PDF {
        @Override
        OfficeDocumentHandler getHandler() {
            return PdfHandler.INSTANCE;
        }
    },
    /**
     * html转换
     */
    HTML {
        @Override
        OfficeDocumentHandler getHandler() {
            return HtmlHandler.INSTANCE;
        }
    };

    /**
     * 文档转换实例
     * @return {@link OfficeDocumentHandler}
     */
    abstract OfficeDocumentHandler getHandler();
}
