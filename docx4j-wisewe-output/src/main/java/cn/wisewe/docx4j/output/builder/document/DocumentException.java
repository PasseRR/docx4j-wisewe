package cn.wisewe.docx4j.output.builder.document;

import cn.wisewe.docx4j.output.OutputException;

/**
 * word导出异常
 * @author xiehai
 * @date 2020/12/24 10:11
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class DocumentException extends OutputException {
    public DocumentException() {
        super();
    }

    public DocumentException(String message) {
        super(message);
    }

    public DocumentException(String message, Throwable t) {
        super(message, t);
    }
}
