package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.builder.FileType;
import cn.wisewe.docx4j.convert.builder.ImageUtils;
import cn.wisewe.docx4j.convert.builder.W3cDocumentUtils;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.poi.xwpf.converter.xhtml.Base64EmbedImgManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.FontReplacer;
import org.apache.poi.hwpf.converter.WordToFoConverter;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.convert.out.fo.renderers.FORendererApacheFOP;
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
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Objects;

/**
 * word文档类型
 * @author xiehai
 * @date 2022/03/23 16:05
 */
enum DocumentType {
    /**
     * xml格式的word文档
     */
    XML {
        @Override
        void doConvertPdf(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
            WordprocessingMLPackage pkg = WordprocessingMLPackage.load(inputStream);
            pkg.setFontMapper(IS_WINDOWS ? new BestMatchingMapper() : new IdentityPlusMapper());
            FOSettings foSettings = new FOSettings(pkg);
            FopFactoryBuilder fopFactoryBuilder = FORendererApacheFOP.getFopFactoryBuilder(foSettings);
            FORendererApacheFOP.getFOUserAgent(foSettings, fopFactoryBuilder.build());

            Docx4J.toFO(foSettings, outputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);
        }

        @Override
        void doConvertHtml(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
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
            htmlSettings.setScriptElementHandler((pg, doc, sd) -> W3cDocumentUtils.mobileMeta(doc));
            // 图片转base64
            htmlSettings.setImageHandler((awxp, rs, bp) -> ImageUtils.base64(bp.getBytes()));

            Docx4J.toHTML(htmlSettings, outputStream, Docx4J.FLAG_NONE);
        }
    },
    /**
     * 97word
     */
    DOC {
        @Override
        void doConvertPdf(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
            HWPFDocument document = new HWPFDocument(inputStream);
            WordToFoConverter converter = new WordToFoConverter(XMLHelper.newDocumentBuilder().newDocument());
            // 图片转base64
            converter.setPicturesManager((content, pt, sn, w, h) -> ImageUtils.base64(content));
            // 字体转换
            converter.setFontReplacer(t -> {
                FontReplacer.Triplet triplet = new FontReplacer.Triplet();
                triplet.fontName = "simsun";
                return triplet;
            });
            converter.processDocument(document);
            // TODO 中文字体不显示问题
            FopFactory fopFactory = FopFactory.newInstance(new File(DocumentType.class.getResource("fop.xml").toURI()));
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, fopFactory.newFOUserAgent(), outputStream);
            TransformerFactory.newInstance()
                .newTransformer()
                .transform(new DOMSource(converter.getDocument()), new SAXResult(fop.getDefaultHandler()));
        }

        @Override
        void doConvertHtml(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
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
            W3cDocumentUtils.mobileSupport(document);

            serializer.transform(new DOMSource(document), new StreamResult(outputStream));
        }
    },
    /**
     * 压缩包格式word
     */
    DOCX {
        @Override
        void doConvertPdf(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
            XWPFDocument document = new XWPFDocument(inputStream);
            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(document, outputStream, options);
        }

        @Override
        void doConvertHtml(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
            XWPFDocument document = new XWPFDocument(inputStream);
            // 带图片的word，则将图片转为base64编码，保存在一个页面中
            XHTMLOptions options = XHTMLOptions.create().indent(4).setImageManager(new Base64EmbedImgManager());
            XHTMLConverter.getInstance().convert(document, outputStream, options);
        }
    };

    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");

    /**
     * pdf转换
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     */
    abstract void doConvertPdf(BufferedInputStream inputStream, OutputStream outputStream) throws Exception;

    /**
     * html转换
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     */
    abstract void doConvertHtml(BufferedInputStream inputStream, OutputStream outputStream) throws Exception;

    /**
     * 转换pdf
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     */
    static void convertPdf(BufferedInputStream inputStream, OutputStream outputStream) {
        try {
            type(inputStream).doConvertPdf(inputStream, outputStream);
        } catch (Exception e) {
            throw new DocumentConvertException(e);
        }
    }

    /**
     * 转换html
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     */
    static void convertHtml(BufferedInputStream inputStream, OutputStream outputStream) {
        try {
            type(inputStream).doConvertHtml(inputStream, outputStream);
        } catch (Exception e) {
            throw new DocumentConvertException(e);
        }
    }

    /**
     * 获得文档转换类型
     * @param inputStream {@link BufferedInputStream}
     * @return {@link DocumentType}
     */
    private static DocumentType type(BufferedInputStream inputStream) {
        switch (FileType.type(inputStream)) {
            case ZIP: {
                return DOCX;
            }
            case COMPOUND: {
                return DOC;
            }
            default: {
                return XML;
            }
        }
    }
}
