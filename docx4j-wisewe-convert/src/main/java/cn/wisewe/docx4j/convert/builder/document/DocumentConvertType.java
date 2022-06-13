package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.FileHandler;

/**
 * 支持转换类型
 * @author xiehai
 * @date 2022/03/23 16:03
 */
public enum DocumentConvertType implements FileHandler {
    /**
     * pdf转换
     */
    PDF {
        @Override
        public DocumentHandler getHandler() {
            return PdfHandler.INSTANCE;
        }
    },
    /**
     * html转换
     */
    HTML {
        @Override
        public DocumentHandler getHandler() {
            return HtmlHandler.INSTANCE;
        }
    }
}
