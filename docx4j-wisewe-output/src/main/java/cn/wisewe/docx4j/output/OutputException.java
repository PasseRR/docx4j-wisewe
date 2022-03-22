package cn.wisewe.docx4j.output;

/**
 * 导出异常
 * @author xiehai
 * @date 2020/12/24 10:10
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class OutputException extends RuntimeException {
    public OutputException(Throwable t) {
        super(t);
    }

    public OutputException(String message) {
        super(message);
    }

    public OutputException(String message, Throwable t) {
        super(message, t);
    }
}
