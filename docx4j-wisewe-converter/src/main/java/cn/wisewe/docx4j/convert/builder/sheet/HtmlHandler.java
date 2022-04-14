package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.sprie.Warnings;
import com.spire.xls.Workbook;
import com.spire.xls.core.spreadsheet.HTMLOptions;

import java.io.OutputStream;

/**
 * excel转html处理器
 * @author xiehai
 * @date 2022/03/25 12:34
 */
class HtmlHandler extends SpreadSheetHandler {
    static final HtmlHandler INSTANCE = new HtmlHandler();

    private HtmlHandler() {

    }

    @Override
    protected void postHandle(Workbook workbook, OutputStream outputStream) {
        // TODO 多sheet处理
        Warnings.HTML_EXCEL.remove(os -> {
            HTMLOptions options = new HTMLOptions();
            options.setImageEmbedded(true);
            workbook.getWorksheets().get(0).saveToHtml(os, options);
        }, outputStream);
    }
}
