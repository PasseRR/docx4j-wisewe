package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.FileHandler;

/**
 * excel转换类型支持
 * @author xiehai
 * @date 2022/03/25 12:44
 */
public enum SpreadSheetOfficeConvertType implements FileHandler {
    /**
     * excel转html
     */
    HTML {
        @Override
        public SpreadSheetHandler getHandler() {
            return HtmlHandler.INSTANCE;
        }
    }
}
