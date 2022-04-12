package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.office.OfficeConverter;

/**
 * excel转换器
 * @author xiehai
 * @date 2022/03/22 11:14
 */
public class SpreadSheetConverter extends OfficeConverter<SpreadSheetConverter, SpreadSheetOfficeConvertType> {
    SpreadSheetConverter() {
        super(SpreadSheetConvertException::new, SpreadSheetConvertException::new);
    }

    public static SpreadSheetConverter create() {
        return new SpreadSheetConverter();
    }
}
