package cn.wisewe.docx4j.convert;

/**
 * 文档转换异常
 * @author xiehai
 * @date 2022/03/22 11:22
 */
public class ConvertException extends RuntimeException {
    public ConvertException(Throwable t) {
        super(t);
    }

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(String message, Throwable t) {
        super(message, t);
    }
}
