package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.AbstractConverter;

/**
 * word文档转换
 * @author xiehai
 * @date 2022/03/23 15:59
 */
public class DocumentConverter extends AbstractConverter<DocumentConverter, DocumentConvertType> {
    DocumentConverter() {
        super(DocumentConvertException::new, DocumentConvertException::new);
    }

    /**
     * 快速创建word转换器工厂方法
     * @return {@link DocumentConverter}
     */
    public static DocumentConverter create() {
        return new DocumentConverter();
    }
}
