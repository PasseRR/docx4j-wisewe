package cn.wisewe.docx4j.input.builder.compression;

import cn.wisewe.docx4j.input.InputException;

/**
 * 压缩包导入异常
 * @author xiehai
 * @date 2021/01/12 14:16
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class CompressionImportException extends InputException {
    public CompressionImportException(String message) {
        super(message);
    }

    public CompressionImportException(String message, Throwable t) {
        super(message, t);
    }

    public CompressionImportException(Throwable t) {
        this(t.getMessage(), t);
    }
}
