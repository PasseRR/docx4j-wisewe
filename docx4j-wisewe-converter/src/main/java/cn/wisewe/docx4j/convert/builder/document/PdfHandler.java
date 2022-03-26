package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.fop.FontUtils;
import cn.wisewe.docx4j.convert.fop.FopUtils;
import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import cn.wisewe.docx4j.convert.utils.ImageUtils;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToFoConverter;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.fo.renderers.FORendererApacheFOP;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import java.io.BufferedInputStream;
import java.io.OutputStream;

/**
 * word转pdf文档处理器
 * @author xiehai
 * @date 2022/03/25 10:10
 */
class PdfHandler extends OfficeDocumentHandler {
    static final PdfHandler INSTANCE = new PdfHandler();

    private PdfHandler() {

    }

    @Override
    protected void handleFlat(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.load(inputStream);
        pkg.setFontMapper(fontMapper());
        FOSettings foSettings = new FOSettings(pkg);
        FopFactoryBuilder fopFactoryBuilder = FORendererApacheFOP.getFopFactoryBuilder(foSettings);
        FORendererApacheFOP.getFOUserAgent(foSettings, fopFactoryBuilder.build());

        Docx4J.toFO(foSettings, outputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);
    }

    @Override
    protected void handleBinary(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        HWPFDocument document = new HWPFDocument(inputStream);
        WordToFoConverter converter = new WordToFoConverter(XMLHelper.newDocumentBuilder().newDocument());
        // 图片转base64
        converter.setPicturesManager((content, pt, sn, w, h) -> ImageUtils.base64(content));
        // 字体转换
        converter.setFontReplacer(t -> {
            t.fontName = FontUtils.defaultFontName();
            return t;
        });
        converter.processDocument(document);

        Fop fop = FopUtils.newFopInstance(outputStream);

        TransformerFactory.newInstance()
            .newTransformer()
            .transform(new DOMSource(converter.getDocument()), new SAXResult(fop.getDefaultHandler()));
    }

    @Override
    protected void handleZipped(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        PdfConverter.getInstance().convert(new XWPFDocument(inputStream), outputStream, PdfOptions.create());
    }
}
