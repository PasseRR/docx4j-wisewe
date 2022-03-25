package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.ConvertException;
import cn.wisewe.docx4j.convert.office.OfficeConverter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.function.Function;

/**
 * word文档转换
 * @author xiehai
 * @date 2022/03/23 15:59
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentConverter extends OfficeConverter<DocumentConverter, DocumentConvertType> {
    DocumentConverter() {
    }

    /**
     * 快速创建word转换器工厂方法
     * @return {@link DocumentConverter}
     */
    public static DocumentConverter create() {
        return new DocumentConverter();
    }

    @Override
    protected Function<String, ConvertException> messageException() {
        return DocumentConvertException::new;
    }

    @Override
    protected Function<Exception, ConvertException> exception() {
        return DocumentConvertException::new;
    }
}
