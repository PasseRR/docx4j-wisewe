package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.OutputException;

/**
 * 电子表格导出异常
 * @author xiehai
 * @date 2020/12/24 10:11
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class SpreadSheetException extends OutputException {
    public SpreadSheetException() {
        super();
    }

    public SpreadSheetException(String message) {
        super(message);
    }

    public SpreadSheetException(String message, Throwable t) {
        super(message, t);
    }
}
