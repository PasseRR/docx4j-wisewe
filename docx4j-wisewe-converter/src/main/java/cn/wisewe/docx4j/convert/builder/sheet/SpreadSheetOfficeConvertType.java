package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.office.OfficeConvertHandler;
import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import com.spire.xls.Workbook;

/**
 * excel转换类型支持
 * @author xiehai
 * @date 2022/03/25 12:44
 */
public enum SpreadSheetOfficeConvertType implements OfficeConvertHandler<Workbook> {
    /**
     * excel转html
     */
    HTML {
        @Override
        public OfficeDocumentHandler<Workbook> getHandler() {
            return HtmlHandler.INSTANCE;
        }
    }
}
