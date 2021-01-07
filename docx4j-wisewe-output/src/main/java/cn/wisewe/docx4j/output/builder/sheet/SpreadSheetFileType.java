package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.builder.OutputFileType;

/**
 * 电子表格文件类型
 * @author xiehai
 * @date 2020/12/24 12:33
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public enum SpreadSheetFileType implements OutputFileType {
    /**
     * 2003 excel
     */
    XLS,
    /**
     * 2007及以上excel
     */
    XLSX
}
