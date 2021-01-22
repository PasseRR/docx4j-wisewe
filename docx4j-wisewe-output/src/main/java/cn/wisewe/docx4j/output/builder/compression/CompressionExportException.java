package cn.wisewe.docx4j.output.builder.compression;

import cn.wisewe.docx4j.output.OutputException;

/**
 * 压缩文件异常
 * @author xiehai
 * @date 2020/12/24 10:11
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class CompressionExportException extends OutputException {
    public CompressionExportException(String message) {
        super(message);
    }

    public CompressionExportException(String message, Throwable t) {
        super(message, t);
    }

    public CompressionExportException(Throwable t) {
        this(t.getMessage(), t);
    }
}
