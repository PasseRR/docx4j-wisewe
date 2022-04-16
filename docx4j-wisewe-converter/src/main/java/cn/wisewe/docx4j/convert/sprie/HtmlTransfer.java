package cn.wisewe.docx4j.convert.sprie;

import cn.wisewe.docx4j.convert.ConvertException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * html转换器
 * @author xiehai
 * @date 2022/04/14 20:17
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
class HtmlTransfer {
    final Consumer<OutputStream> consumer;
    Consumer<Document> handler;

    HtmlTransfer(Consumer<OutputStream> consumer) {
        this.consumer = consumer;
    }

    /**
     * 工厂方法
     * @param consumer 输出流处理
     * @param <T>      转换类型
     * @return {@link HtmlTransfer}
     */
    static HtmlTransfer create(Consumer<OutputStream> consumer) {
        return new HtmlTransfer(consumer);
    }

    /**
     * 过程处理
     * @param handler {@link Consumer}
     * @return {@link HtmlTransfer}
     */
    HtmlTransfer handle(Consumer<Document> handler) {
        this.handler = handler;
        return this;
    }

    /**
     * 将目标对象传输至输出流
     */
    void transfer(OutputStream outputStream) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            this.consumer.accept(baos);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                Document document = HtmlTransfer.parse(bais);
                if (Objects.nonNull(this.handler)) {
                    this.handler.accept(document);
                }

                HtmlTransfer.convert(document, outputStream);
            }
        } catch (IOException e) {
            throw new ConvertException(e);
        }
    }

    /**
     * {@link InputStream}转{@link Document}
     * @param inputStream {@link InputStream}
     * @return {@link Document}
     */
    protected static Document parse(InputStream inputStream) {
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
    protected static void convert(Document document, OutputStream outputStream) {
        try {
            outputStream.write(document.html().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ConvertException(e);
        }
    }
}
