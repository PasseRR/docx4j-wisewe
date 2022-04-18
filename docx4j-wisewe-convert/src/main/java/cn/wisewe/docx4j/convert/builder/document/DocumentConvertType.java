package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.office.OfficeFileHandler;
import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import com.spire.doc.Document;

/**
 * 支持转换类型
 * @author xiehai
 * @date 2022/03/23 16:03
 */
public enum DocumentConvertType implements OfficeFileHandler<Document> {
    /**
     * pdf转换
     */
    PDF {
        @Override
        public OfficeDocumentHandler<Document> getHandler() {
            return PdfHandler.INSTANCE;
        }
    },
    /**
     * html转换
     */
    HTML {
        @Override
        public OfficeDocumentHandler<Document> getHandler() {
            return HtmlHandler.INSTANCE;
        }
    }
}
