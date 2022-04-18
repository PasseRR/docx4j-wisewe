package cn.wisewe.docx4j.convert.builder;

import cn.wisewe.docx4j.convert.ConvertException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.jsoup.Jsoup;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * svg转pdf转换器
 * @author xiehai
 * @date 2022/04/18 13:39
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SvgTransfer {
    /**
     * 多个svg提供
     */
    final Supplier<List<byte[]>> supplier;
    /**
     * 移除警告信息过程
     */
    BiConsumer<Integer, Elements> consumer;
    /**
     * html大小写敏感解析器
     */
    private static final Parser PARSER = Parser.htmlParser();

    static {
        PARSER.settings(new ParseSettings(true, true));
    }

    SvgTransfer(Supplier<List<byte[]>> supplier) {
        this.supplier = supplier;
    }

    /**
     * 工厂方法
     * @param supplier svg图片提供
     * @return {@link SvgTransfer}
     */
    public static SvgTransfer create(Supplier<List<byte[]>> supplier) {
        Objects.requireNonNull(supplier);
        return new SvgTransfer(supplier);
    }

    /**
     * 处理svg中的警告信息
     * @param consumer 处理过程
     * @return {@link SvgTransfer}
     */
    public SvgTransfer handle(BiConsumer<Integer, Elements> consumer) {
        Objects.requireNonNull(consumer);
        this.consumer = consumer;
        return this;
    }

    /**
     * 将svg转为png图片 然后添加进pdf文件中
     * @param outputStream {@link OutputStream}
     */
    public void transfer(OutputStream outputStream) {
        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            float height = document.getPageSize().getHeight(), width = document.getPageSize().getWidth();
            // svg转为pdf的一页
            int index = 0;
            for (byte[] it : this.supplier.get()) {
                // 先要添加段落 否则会empty pages
                document.add(new Chunk(""));
                Elements svg = Jsoup.parse(new String(it, StandardCharsets.UTF_8), PARSER).select("svg");
                this.consumer.accept(index++, svg);

                try (StringReader reader = new StringReader(svg.toString());
                     ByteArrayOutputStream baos = new ByteArrayOutputStream(1024)) {
                    TranscoderInput input = new TranscoderInput(reader);
                    TranscoderOutput output = new TranscoderOutput(baos);
                    new PNGTranscoder().transcode(input, output);
                    Image image = Image.getInstance(baos.toByteArray());
                    image.scaleToFit(document.getPageSize());
                    // 设置图片绝对位置
                    image.setAbsolutePosition(
                        (width - image.getScaledWidth()) / 2,
                        (height - image.getScaledHeight()) / 2
                    );
                    writer.getDirectContent().addImage(image);
                    document.newPage();
                }
            }
        } catch (Exception e) {
            throw new ConvertException(e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}
