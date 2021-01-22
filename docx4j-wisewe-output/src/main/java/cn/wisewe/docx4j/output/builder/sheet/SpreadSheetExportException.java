package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.OutputException;

/**
 * 电子表格导出异常
 * @author xiehai
 * @date 2020/12/24 10:11
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class SpreadSheetExportException extends OutputException {
    public SpreadSheetExportException(String message) {
        super(message);
    }

    public SpreadSheetExportException(String message, Throwable t) {
        super(message, t);
    }

    public SpreadSheetExportException(Throwable t) {
        this(t.getMessage(), t);
    }
}
