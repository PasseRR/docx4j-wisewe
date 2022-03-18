package cn.wisewe.docx4j.output.builder.portable;

import cn.wisewe.docx4j.output.OutputException;

/**
 * pdf导出异常
 * @author xiehai
 * @date 2020/12/31 11:57
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class PortableExportException extends OutputException {
    public PortableExportException(String message) {
        super(message);
    }

    public PortableExportException(String message, Throwable t) {
        super(message, t);
    }

    public PortableExportException(Throwable t) {
        this(t.getMessage(), t);
    }
}
