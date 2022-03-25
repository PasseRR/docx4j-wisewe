package cn.wisewe.docx4j.convert.builder.sheet;

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
public class SpreadSheetConverter extends OfficeConverter<SpreadSheetConverter, SpreadSheetOfficeConvertType> {
    SpreadSheetConverter() {
    }

    public static SpreadSheetConverter create() {
        return new SpreadSheetConverter();
    }

    @Override
    protected Function<String, ConvertException> messageException() {
        return SpreadSheetConvertException::new;
    }

    @Override
    protected Function<Exception, ConvertException> exception() {
        return SpreadSheetConvertException::new;
    }
}
