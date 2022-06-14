package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.FileHandler;

/**
 * ppt转换文件类型支持
 * @author xiehai
 * @date 2022/03/25 13:25
 */
public enum SlideConvertType implements FileHandler {
    /**
     * ppt转pdf
     */
    PDF {
        @Override
        public SlideHandler getHandler() {
            return PdfHandler.INSTANCE;
        }
    },
    /**
     * ppt转html
     */
    HTML {
        @Override
        public SlideHandler getHandler() {
            return HtmlHandler.INSTANCE;
        }
    }
}
