package cn.wisewe.docx4j.recognize;

/**
 * 识别异常
 * @author xiehai
 * @date 2022/08/03 10:18
 */
public class RecognizeException extends RuntimeException {
    public RecognizeException(Throwable t) {
        super(t);
    }

    public RecognizeException(String message) {
        super(message);
    }

    public RecognizeException(String message, Throwable t) {
        super(message, t);
    }
}
