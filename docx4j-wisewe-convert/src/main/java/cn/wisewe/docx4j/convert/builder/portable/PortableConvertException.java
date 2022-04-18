package cn.wisewe.docx4j.convert.builder.portable;

import cn.wisewe.docx4j.convert.ConvertException;

/**
 * pdf文档转换异常
 * @author xiehai
 * @date 2022/03/23 15:59
 */
class PortableConvertException extends ConvertException {
    public PortableConvertException(Throwable t) {
        super(t);
    }

    public PortableConvertException(String message) {
        super(message);
    }

    public PortableConvertException(String message, Throwable t) {
        super(message, t);
    }
}
