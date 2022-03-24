package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.builder.FileType;
import cn.wisewe.docx4j.convert.builder.W3cDocumentUtils;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.poi.xwpf.converter.xhtml.Base64EmbedImgManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.Filetype;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

/**
 * word文档类型
 * @author xiehai
 * @date 2022/03/23 16:05
 */
enum DocumentType {
    /**
     * 97word
     */
    DOC {
        @Override
        void doConvertPdf(BufferedInputStream inputStream, OutputStream outputStream) {
            try {
                WordprocessingMLPackage mlPackage = (WordprocessingMLPackage) WordprocessingMLPackage.load(
                    inputStream, Filetype.FlatOPC);
                Docx4J.toPDF(mlPackage, outputStream);
            } catch (Exception e) {
                throw new DocumentConvertException(e);
            }
        }

        @Override
        void doConvertHtml(BufferedInputStream inputStream, OutputStream outputStream) {
            try {
                WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
                );

                // 图片转base64
                wordToHtmlConverter.setPicturesManager((content, pictureType, suggestedName, w, h) -> {
                    String base64 = Base64.getEncoder().encodeToString(content);
                    return String.format("data:;base64,%s", base64);
                });

                wordToHtmlConverter.processDocument(new HWPFDocument(inputStream));

                Transformer serializer = TransformerFactory.newInstance().newTransformer();
                serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
                serializer.setOutputProperty(OutputKeys.INDENT, "yes");
                serializer.setOutputProperty(OutputKeys.METHOD, "html");

                Document document = wordToHtmlConverter.getDocument();
                W3cDocumentUtils.mobileSupport(document);

                serializer.transform(new DOMSource(document), new StreamResult(outputStream));
            } catch (IOException | ParserConfigurationException | TransformerException e) {
                throw new DocumentConvertException(e);
            }
        }
    },
    /**
     * 压缩包格式word
     */
    DOCX {
        @Override
        void doConvertPdf(BufferedInputStream inputStream, OutputStream outputStream) {
            try {
                XWPFDocument document = new XWPFDocument(inputStream);
                PdfOptions options = PdfOptions.create();
                PdfConverter.getInstance().convert(document, outputStream, options);
            } catch (Exception e) {
                throw new DocumentConvertException(e);
            }
        }

        @Override
        void doConvertHtml(BufferedInputStream inputStream, OutputStream outputStream) {
            try {
                XWPFDocument document = new XWPFDocument(inputStream);
                // 带图片的word，则将图片转为base64编码，保存在一个页面中
                XHTMLOptions options = XHTMLOptions.create().indent(4).setImageManager(new Base64EmbedImgManager());
                XHTMLConverter.getInstance().convert(document, outputStream, options);
            } catch (IOException e) {
                throw new DocumentConvertException(e);
            }
        }
    };

    /**
     * pdf转换
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     */
    abstract void doConvertPdf(BufferedInputStream inputStream, OutputStream outputStream);

    /**
     * html转换
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     */
    abstract void doConvertHtml(BufferedInputStream inputStream, OutputStream outputStream);

    /**
     * 转换pdf
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     */
    static void convertPdf(BufferedInputStream inputStream, OutputStream outputStream) {
        type(inputStream).doConvertPdf(inputStream, outputStream);
    }

    /**
     * 转换html
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     */
    static void convertHtml(BufferedInputStream inputStream, OutputStream outputStream) {
        type(inputStream).doConvertHtml(inputStream, outputStream);
    }

    /**
     * 获得文档转换类型
     * @param inputStream {@link BufferedInputStream}
     * @return {@link DocumentType}
     */
    private static DocumentType type(BufferedInputStream inputStream) {
        if (FileType.type(inputStream) == FileType.ZIP) {
            return DOCX;
        }
        return DOC;
    }
}
