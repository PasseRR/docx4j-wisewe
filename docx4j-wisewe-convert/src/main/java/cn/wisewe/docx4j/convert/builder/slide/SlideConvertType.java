package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.office.OfficeFileHandler;
import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import com.spire.presentation.Presentation;

/**
 * ppt转换文件类型支持
 * @author xiehai
 * @date 2022/03/25 13:25
 */
public enum SlideConvertType implements OfficeFileHandler<Presentation> {
    /**
     * ppt转pdf
     */
    PDF {
        @Override
        public OfficeDocumentHandler<Presentation> getHandler() {
            return PdfHandler.INSTANCE;
        }
    },
    /**
     * ppt转html
     */
    HTML {
        @Override
        public OfficeDocumentHandler<Presentation> getHandler() {
            return HtmlHandler.INSTANCE;
        }
    }
}
