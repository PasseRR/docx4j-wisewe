package cn.wisewe.docx4j.input.builder.sheet;

import cn.wisewe.docx4j.input.InputException;

/**
 * 电子表格导入异常
 * @author xiehai
 * @date 2021/01/07 18:03
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class SpreadSheetImportException extends InputException {
    public SpreadSheetImportException(String message) {
        super(message);
    }

    public SpreadSheetImportException(String message, Throwable t) {
        super(message, t);
    }

    public SpreadSheetImportException(Throwable t) {
        this(t.getMessage(), t);
    }
}
