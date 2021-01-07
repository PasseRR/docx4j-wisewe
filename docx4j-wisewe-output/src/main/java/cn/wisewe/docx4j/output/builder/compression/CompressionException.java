package cn.wisewe.docx4j.output.builder.compression;

import cn.wisewe.docx4j.output.OutputException;

/**
 * 压缩文件异常
 * @author xiehai
 * @date 2020/12/24 10:11
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class CompressionException extends OutputException {
    public CompressionException() {
        super();
    }

    public CompressionException(String message) {
        super(message);
    }

    public CompressionException(String message, Throwable t) {
        super(message, t);
    }
}
