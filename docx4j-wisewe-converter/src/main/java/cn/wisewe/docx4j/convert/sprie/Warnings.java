package cn.wisewe.docx4j.convert.sprie;

import cn.wisewe.docx4j.convert.ConvertException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * sprie警告信息移除
 * @author xiehai
 * @date 2022/04/14 10:13
 */
public enum Warnings {
    /**
     * 移除pdf中的警告信息
     */
    PDF {
        @Override
        public void remove(Consumer<OutputStream> consumer, OutputStream outputStream) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                consumer.accept(baos);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                    PdfReader reader = new PdfReader(bais);
                    IntStream.rangeClosed(1, reader.getNumberOfPages())
                        .mapToObj(reader::getPageN)
                        .map(it -> it.get(PdfName.CONTENTS))
                        .peek(it -> System.out.println(it.getClass().getName()))
                        .filter(it -> it instanceof PRStream)
                        .map(PRStream.class::cast)
                        .forEach(it -> {
                            try {
                                System.out.println(new String(PdfReader.getStreamBytes(it)));
                            } catch (IOException e) {
                            }
                        });
                    reader.close();
                }
            } catch (Exception e) {
                throw new ConvertException(e);
            }
        }
    },
    /**
     * 移除html中的警告信息
     */
    HTML_SPAN {
        @Override
        public void remove(Consumer<OutputStream> consumer, OutputStream outputStream) {
            Transfer.<Document>create(consumer)
                .source(HtmlUtils::parse)
                .transfer(document -> {
                    Optional.of(document.body().getElementsByTag("p"))
                        .filter(it -> it.size() > 0)
                        .map(it -> it.get(0))
                        .ifPresent(Node::remove);

                    Optional.of(document.body().getElementsByAttributeValue("style", "min-height:72pt"))
                        .filter(it -> it.size() > 0)
                        .map(it -> it.get(0))
                        .ifPresent(it -> it.removeAttr("style"));

                    HtmlUtils.convert(document, outputStream);
                });
        }
    },
    /**
     * 移除html svg中的信息
     */
    HTML_SVG {
        @Override
        public void remove(Consumer<OutputStream> consumer, OutputStream outputStream) {
            Transfer.<Document>create(consumer)
                .source(HtmlUtils::parse)
                .transfer(document -> {
                    document.body()
                        .select("div > svg")
                        .forEach(it -> {
                            it.attr("width", "100%");
                            it.attr("height", "100%");
                            it.select("svg > g > text").forEach(Node::remove);
                        });
                    HtmlUtils.convert(document, outputStream);
                });
        }
    };

    /**
     * 移除警告信息
     * @param consumer     {@link OutputStream}处理
     * @param outputStream 目标输出流
     */
    public abstract void remove(Consumer<OutputStream> consumer, OutputStream outputStream);
}
