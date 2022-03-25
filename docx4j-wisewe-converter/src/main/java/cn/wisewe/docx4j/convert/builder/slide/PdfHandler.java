package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;

import java.io.BufferedInputStream;
import java.io.OutputStream;

/**
 * ppt转pdf处理器
 * @author xiehai
 * @date 2022/03/25 13:24
 */
class PdfHandler extends OfficeDocumentHandler {
    static final PdfHandler INSTANCE = new PdfHandler();

    private PdfHandler() {

    }

    @Override
    protected void handleFlat(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {

    }

    @Override
    protected void handleBinary(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {

    }

    @Override
    protected void handleZipped(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {

    }
}
