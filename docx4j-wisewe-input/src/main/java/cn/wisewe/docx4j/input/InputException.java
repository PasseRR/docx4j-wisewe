package cn.wisewe.docx4j.input;

/**
 * 导入异常
 * @author xiehai
 * @date 2021/01/07 18:02
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class InputException extends RuntimeException {
    public InputException() {
        super();
    }

    public InputException(String message) {
        super(message);
    }

    public InputException(String message, Throwable t) {
        super(message, t);
    }
}
