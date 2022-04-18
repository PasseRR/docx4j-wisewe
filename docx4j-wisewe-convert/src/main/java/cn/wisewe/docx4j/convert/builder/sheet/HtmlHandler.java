package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.builder.HtmlTransfer;
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
        HTMLOptions options = new HTMLOptions();
        options.setImageEmbedded(true);
        // TODO 多sheet处理
        HtmlTransfer.create(os -> workbook.getWorksheets().get(0).saveToHtml(os, options))
            .handle(document -> document.body().select("h2:lt(1)").remove())
            .transfer(outputStream);
    }
}
