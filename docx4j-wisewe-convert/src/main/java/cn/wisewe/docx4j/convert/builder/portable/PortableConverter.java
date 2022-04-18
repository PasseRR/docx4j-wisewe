package cn.wisewe.docx4j.convert.builder.portable;

import cn.wisewe.docx4j.convert.AbstractConverter;

/**
 * pdf文档转换
 * @author xiehai
 * @date 2022/04/17 19:00
 */
public class PortableConverter extends AbstractConverter<PortableConverter, PortableConvertType> {
    protected PortableConverter() {
        super(PortableConvertException::new, PortableConvertException::new);
    }

    /**
     * pdf文档转换工厂方法
     * @return {@link PortableConverter}
     */
    public static PortableConverter create() {
        return new PortableConverter();
    }
}
