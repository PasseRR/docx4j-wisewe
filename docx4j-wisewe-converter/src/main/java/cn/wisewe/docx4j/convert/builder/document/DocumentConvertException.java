package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.ConvertException;

/**
 * word文档转换异常
 * @author xiehai
 * @date 2022/03/23 15:59
 */
public class DocumentConvertException extends ConvertException {
    public DocumentConvertException(Throwable t) {
        super(t);
    }

    public DocumentConvertException(String message) {
        super(message);
    }

    public DocumentConvertException(String message, Throwable t) {
        super(message, t);
    }
}
