package cn.wisewe.docx4j.convert.sprie;

import cn.wisewe.docx4j.convert.ConvertException;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfReader;
import org.jsoup.nodes.Node;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Consumer;

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
                    PdfDictionary catalog = reader.getCatalog();
                    System.out.println(catalog.size());
                }
            } catch (Exception e) {
                throw new ConvertException(e);
            }
        }
    },
    HTML_EXCEL {
        @Override
        public void remove(Consumer<OutputStream> consumer, OutputStream outputStream) {
            HtmlTransfer.create(consumer)
                .handle(document -> document.body().select("h2:lt(1)").remove())
                .transfer(outputStream);
        }
    },
    /**
     * 移除word转换html中p的警告信息
     */
    HTML_DOC {
        @Override
        public void remove(Consumer<OutputStream> consumer, OutputStream outputStream) {
            HtmlTransfer.create(consumer)
                .handle(document -> {
                    Optional.of(document.body().getElementsByTag("p"))
                        .filter(it -> it.size() > 0)
                        .map(it -> it.get(0))
                        .ifPresent(Node::remove);

                    Optional.of(document.body().getElementsByAttributeValue("style", "min-height:72pt"))
                        .filter(it -> it.size() > 0)
                        .map(it -> it.get(0))
                        .ifPresent(it -> it.removeAttr("style"));
                })
                .transfer(outputStream);
        }
    },
    /**
     * 移除ppt转换html中svg的信息
     */
    HTML_SLIDE {
        @Override
        public void remove(Consumer<OutputStream> consumer, OutputStream outputStream) {
            HtmlTransfer.create(consumer)
                .handle(document ->
                    document.body()
                        .select("div > svg")
                        .forEach(it -> {
                            it.attr("width", "100%");
                            it.attr("height", "100%");
                            it.select("svg > g > text").forEach(Node::remove);
                        })
                )
                .transfer(outputStream);
        }
    };

    private static final String WARN_MESSAGE = "Evaluation Warning: The document was created with Spire.Doc for JAVA.";

    /**
     * 移除警告信息
     * @param consumer     {@link OutputStream}处理
     * @param outputStream 目标输出流
     */
    public abstract void remove(Consumer<OutputStream> consumer, OutputStream outputStream);
}
