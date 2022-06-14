package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.AbstractConverter;

/**
 * ppt转换器
 * @author xiehai
 * @date 2022/03/22 11:14
 */
public class SlideConverter extends AbstractConverter<SlideConverter, SlideConvertType> {
    SlideConverter() {
        super(SlideConvertException::new, SlideConvertException::new);
    }

    /**
     * {@link SlideConverter}工厂方法
     * @return {@link SlideConverter}
     */
    public static SlideConverter create() {
        return new SlideConverter();
    }
}
