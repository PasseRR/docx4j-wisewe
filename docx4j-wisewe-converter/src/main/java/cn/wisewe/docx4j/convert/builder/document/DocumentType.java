package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.builder.FileType;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.BufferedInputStream;
import java.io.OutputStream;

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
                WordprocessingMLPackage mlPackage = Docx4J.load(inputStream);
                Docx4J.toPDF(mlPackage, outputStream);
            } catch (Exception e) {
                throw new DocumentConvertException(e);
            }
        }
    },
    /**
     * ooxml word
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
    };

    private static final String OS = System.getProperty("os.name").toLowerCase();

    abstract void doConvertPdf(BufferedInputStream inputStream, OutputStream outputStream);

    static void convertPdf(BufferedInputStream inputStream, OutputStream outputStream) {
        type(inputStream).doConvertPdf(inputStream, outputStream);
    }

    private static DocumentType type(BufferedInputStream inputStream) {
        FileType type = FileType.type(inputStream);
        switch (type) {
            case COMPOUND: {
                return DOC;
            }
            case ZIP: {
                return DOCX;
            }
            default: {
                throw new DocumentConvertException("not support document type " + type);
            }
        }
    }
}
