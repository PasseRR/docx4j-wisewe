package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.ConvertException;

/**
 * 幻灯片转换异常
 * @author xiehai
 * @date 2022/03/22 11:11
 */
class SlideConvertException extends ConvertException {
    public SlideConvertException(String message) {
        super(message);
    }

    public SlideConvertException(String message, Throwable t) {
        super(message, t);
    }

    public SlideConvertException(Throwable t) {
        this(t.getMessage(), t);
    }
}
