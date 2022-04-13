package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import com.spire.presentation.FileFormat;
import com.spire.presentation.Presentation;

import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * excel文档处理器
 * @author xiehai
 * @date 2022/04/13 16:53
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
abstract class SlideHandler extends OfficeDocumentHandler<Presentation> {
    @Override
    protected Supplier<Presentation> newDocument() {
        return Presentation::new;
    }

    @Override
    protected BiConsumer<Presentation, InputStream> preHandleFlat() {
        return
            (t, is) -> {
                try {
                    t.loadFromStream(is, FileFormat.AUTO);
                } catch (Exception e) {
                    throw new SlideConvertException(e);
                }
            };
    }

    @Override
    protected BiConsumer<Presentation, InputStream> preHandleBinary() {
        return this.preHandleFlat();
    }

    @Override
    protected BiConsumer<Presentation, InputStream> preHandleZipped() {
        return this.preHandleFlat();
    }
}
