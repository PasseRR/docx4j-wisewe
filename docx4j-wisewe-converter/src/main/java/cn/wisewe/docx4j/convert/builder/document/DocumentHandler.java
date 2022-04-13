package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;

import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * word文档处理器
 * @author xiehai
 * @date 2022/04/13 16:53
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
abstract class DocumentHandler extends OfficeDocumentHandler<Document> {
    @Override
    protected Supplier<Document> newDocument() {
        return Document::new;
    }

    @Override
    protected BiConsumer<Document, InputStream> preHandleFlat() {
        return (t, is) -> t.loadFromStream(is, FileFormat.Word_Xml);
    }

    @Override
    protected BiConsumer<Document, InputStream> preHandleBinary() {
        return (t, is) -> t.loadFromStream(is, FileFormat.Doc);
    }

    @Override
    protected BiConsumer<Document, InputStream> preHandleZipped() {
        return (t, is) -> t.loadFromStream(is, FileFormat.Docx);
    }
}
