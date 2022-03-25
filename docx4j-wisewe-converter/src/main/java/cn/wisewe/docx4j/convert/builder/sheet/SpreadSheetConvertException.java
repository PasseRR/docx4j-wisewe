package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.ConvertException;

/**
 * 电子表格转换异常
 * @author xiehai
 * @date 2022/03/22 11:11
 */
class SpreadSheetConvertException extends ConvertException {
    public SpreadSheetConvertException(String message) {
        super(message);
    }

    public SpreadSheetConvertException(String message, Throwable t) {
        super(message, t);
    }

    public SpreadSheetConvertException(Throwable t) {
        this(t.getMessage(), t);
    }
}
