package cn.wisewe.docx4j.convert.builder.sheet;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.util.IOUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * excel转换器
 * @author xiehai
 * @date 2022/03/22 11:14
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpreadSheetConverter {
    InputStream inputStream;

    SpreadSheetConverter(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public static SpreadSheetConverter create(File file) {
        try {
            return new SpreadSheetConverter(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new SpreadSheetConvertException(e);
        }
    }

    public void toHtml(File file) {
        try (FileWriter out = new FileWriter(file)) {
            Document doc = ExcelToHtmlConverter.process(this.inputStream);
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(out);

            Transformer serializer = TransformerFactory.newInstance().newTransformer();

            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "no");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);
        } catch (IOException | ParserConfigurationException | TransformerException e) {
            throw new SpreadSheetConvertException(e);
        } finally {
            IOUtils.closeQuietly(this.inputStream);
        }
    }
}
