package cn.wisewe.docx4j.output.builder.document;

import cn.wisewe.docx4j.output.OutputException;

/**
 * word导出异常
 * @author xiehai
 * @date 2020/12/24 10:11
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class DocumentExportException extends OutputException {
    public DocumentExportException(String message) {
        super(message);
    }

    public DocumentExportException(String message, Throwable t) {
        super(message, t);
    }

    public DocumentExportException(Throwable t) {
        this(t.getMessage(), t);
    }
}
