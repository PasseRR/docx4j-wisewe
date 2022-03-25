package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.ConvertException;
import cn.wisewe.docx4j.convert.builder.OfficeDocumentHandler;
import cn.wisewe.docx4j.convert.utils.ImageUtils;
import fr.opensagres.poi.xwpf.converter.xhtml.Base64EmbedImgManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.convert.out.html.HtmlCssHelper;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Function;

/**
 * word文档转html处理器
 * @author xiehai
 * @date 2022/03/25 10:00
 */
class HtmlHandler extends OfficeDocumentHandler {
    static final HtmlHandler INSTANCE = new HtmlHandler();

    private HtmlHandler() {

    }

    @Override
    protected void handleFlat(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.load(inputStream);
        // 先初始化样式
        pkg.getMainDocumentPart().getStyleTree();
        HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
        htmlSettings.setFontMapper(IS_WINDOWS ? new BestMatchingMapper() : new IdentityPlusMapper());
        htmlSettings.setOpcPackage(pkg);
        htmlSettings.setStyleElementHandler((pg, doc, sd) -> {
            pkg.getMainDocumentPart()
                .getStyleDefinitionsPart()
                .getJaxbElement()
                .getStyle()
                .forEach(it -> {
                    String id = it.getStyleId();
                    // 临时替换样式id以数字开头的
                    if (Objects.nonNull(id) && !id.isEmpty() && Character.isDigit(id.charAt(0))) {
                        it.setStyleId(String.format("\\3%c %s", id.charAt(0), id.substring(1)));
                    }
                });
            StringBuilder sb = new StringBuilder();
            HtmlCssHelper.createCssForStyles(pkg, pkg.getMainDocumentPart().getStyleTree(), sb);
            Element style = doc.createElement("style");
            style.setAttribute("type", "text/css");
            // 文档样式
            sb.append(".document{")
                .append("width:595.3pt;margin-bottom:72.0pt;margin-top:72.0pt;")
                .append("margin-left:90.0pt;margin-right:90.0pt;}");
            style.setTextContent(sb.toString());

            return style;
        });
        // 移动端支持
        htmlSettings.setScriptElementHandler((pg, doc, sd) -> mobileMeta(doc));
        // 图片转base64
        htmlSettings.setImageHandler((awxp, rs, bp) -> ImageUtils.base64(bp.getBytes()));

        Docx4J.toHTML(htmlSettings, outputStream, Docx4J.FLAG_NONE);
    }

    @Override
    protected void handleBinary(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
            DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        );
        // 图片转base64
        wordToHtmlConverter.setPicturesManager((content, pt, sn, w, h) -> ImageUtils.base64(content));
        wordToHtmlConverter.processDocument(new HWPFDocument(inputStream));

        Transformer serializer = TransformerFactory.newInstance().newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");

        Document document = wordToHtmlConverter.getDocument();
        // 移动端支持
        mobileSupport(document);

        serializer.transform(new DOMSource(document), new StreamResult(outputStream));
    }

    @Override
    protected void handleZipped(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        // 带图片的word，则将图片转为base64编码，保存在一个页面中
        XHTMLOptions options = XHTMLOptions.create().indent(4).setImageManager(new Base64EmbedImgManager());
        XHTMLConverter.getInstance().convert(new XWPFDocument(inputStream), outputStream, options);
    }

    @Override
    protected Function<Exception, ConvertException> handleException() {
        return DocumentConvertException::new;
    }
}
