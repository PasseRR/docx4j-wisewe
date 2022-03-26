package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.ConvertException;
import cn.wisewe.docx4j.convert.office.OfficeConverter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.function.Function;

/**
 * excel转换器
 * @author xiehai
 * @date 2022/03/22 11:14
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlideConverter extends OfficeConverter<SlideConverter, SlideConvertType> {
    SlideConverter() {
    }

    /**
     * {@link SlideConverter}工厂方法
     * @return {@link SlideConverter}
     */
    public static SlideConverter create() {
        return new SlideConverter();
    }

    @Override
    protected Function<String, ConvertException> messageException() {
        return SlideConvertException::new;
    }

    @Override
    protected Function<Exception, ConvertException> exception() {
        return SlideConvertException::new;
    }
}
