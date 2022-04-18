package cn.wisewe.docx4j.convert.builder.portable;

import cn.wisewe.docx4j.convert.ConvertHandler;
import cn.wisewe.docx4j.convert.FileHandler;

/**
 * pdf支持转换文件类型
 * @author xiehai
 * @date 2022/04/17 19:05
 */
public enum PortableConvertType implements FileHandler {
    /**
     * 转html
     */
    HTML {
        @Override
        public ConvertHandler getHandler() {
            return HtmlHandler.INSTANCE;
        }
    }
}
