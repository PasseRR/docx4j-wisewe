package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import com.spire.xls.Workbook;

import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * excel文档处理器
 * @author xiehai
 * @date 2022/04/13 16:53
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
abstract class SpreadSheetHandler extends OfficeDocumentHandler<Workbook> {
    @Override
    protected Supplier<Workbook> newDocument() {
        return Workbook::new;
    }

    @Override
    protected BiConsumer<Workbook, InputStream> preHandleFlat() {
        return Workbook::loadFromXml;
    }

    @Override
    protected BiConsumer<Workbook, InputStream> preHandleBinary() {
        return Workbook::loadFromStream;
    }

    @Override
    protected BiConsumer<Workbook, InputStream> preHandleZipped() {
        return this.preHandleBinary();
    }
}
