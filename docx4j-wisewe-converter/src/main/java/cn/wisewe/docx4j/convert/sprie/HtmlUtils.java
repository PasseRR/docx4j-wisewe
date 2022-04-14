package cn.wisewe.docx4j.convert.sprie;

import cn.wisewe.docx4j.convert.ConvertException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * html工具
 * @author xiehai
 * @date 2022/04/14 16:06
 */
interface HtmlUtils {
    /**
     * {@link InputStream}转{@link Document}
     * @param inputStream {@link InputStream}
     * @return {@link Document}
     */
    static Document parse(InputStream inputStream) {
        try {
            Document document = Jsoup.parse(inputStream, StandardCharsets.UTF_8.name(), "");
            // 移动端支持
            Element mobile = new Element("meta");
            mobile.attr("name", "viewport");
            mobile.attr(
                "content",
                "width=device-width,height=device-height, user-scalable=no,initial-scale=1, minimum-scale=1," +
                    "maximum-scale=1,target-densitydpi=device-dpi"
            );
            // 编码
            Element lang = new Element("meta");
            lang.attr("http-equiv", "Content-Type");
            lang.attr("content", "text/html; charset=utf-8");
            document.head().appendChildren(Arrays.asList(mobile, lang));
            return document;
        } catch (Exception e) {
            throw new ConvertException(e);
        }
    }

    /**
     * html转{@link OutputStream}
     * @param document     {@link Document}
     * @param outputStream {@link OutputStream}
     */
    static void convert(Document document, OutputStream outputStream) {
        try {
            outputStream.write(document.html().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ConvertException(e);
        }
    }
}
