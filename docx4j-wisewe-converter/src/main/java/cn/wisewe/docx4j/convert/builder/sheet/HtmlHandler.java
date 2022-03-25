package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import cn.wisewe.docx4j.convert.utils.ImageUtils;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.FlatOpcXmlImporter;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;

import java.io.BufferedInputStream;
import java.io.OutputStream;

/**
 * excel转html处理器
 * @author xiehai
 * @date 2022/03/25 12:34
 */
class HtmlHandler extends OfficeDocumentHandler {
    static final HtmlHandler INSTANCE = new HtmlHandler();

    private HtmlHandler() {

    }

    @Override
    protected void handleFlat(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        FlatOpcXmlImporter flatOpcXmlImporter = new FlatOpcXmlImporter(inputStream);
        OpcPackage pkg = flatOpcXmlImporter.get();
        HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
        htmlSettings.setFontMapper(fontMapper());
        htmlSettings.setOpcPackage(pkg);
        // 图片转base64
        htmlSettings.setImageHandler((awxp, rs, bp) -> ImageUtils.base64(bp.getBytes()));

        Docx4J.toHTML(htmlSettings, outputStream, Docx4J.FLAG_NONE);
    }

    @Override
    protected void handleBinary(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        
    }

    @Override
    protected void handleZipped(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        SpreadsheetMLPackage pkg = SpreadsheetMLPackage.load(inputStream);
        HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
        htmlSettings.setFontMapper(fontMapper());
        htmlSettings.setOpcPackage(pkg);
        // 图片转base64
        htmlSettings.setImageHandler((awxp, rs, bp) -> ImageUtils.base64(bp.getBytes()));

        Docx4J.toHTML(htmlSettings, outputStream, Docx4J.FLAG_NONE);
    }
}
