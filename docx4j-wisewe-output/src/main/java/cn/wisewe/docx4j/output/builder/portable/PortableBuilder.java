package cn.wisewe.docx4j.output.builder.portable;

import cn.wisewe.docx4j.output.utils.HttpResponseUtil;
import cn.wisewe.docx4j.output.utils.HttpServletUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.util.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Pdf文档构建器
 * @author xiehai
 * @date 2020/12/31 11:54
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PortableBuilder extends PortableDocument<PortableBuilder> {
    /**
     * pdf文档
     */
    Document document;
    /**
     * 内存字节流
     */
    ByteArrayOutputStream byteArrayOutputStream;
    PdfWriter writer;

    PortableBuilder(Document document) throws DocumentException {
        this.document = document;
        this.byteArrayOutputStream = new ByteArrayOutputStream(1024);
        this.writer = PdfWriter.getInstance(this.document, this.byteArrayOutputStream);
    }

    /**
     * 使用指定{@link Document}创建
     * @param supplier {@link Document}提供器
     * @return {@link PortableBuilder}
     */
    public static PortableBuilder create(Supplier<Document> supplier) {
        try {
            return new PortableBuilder(supplier.get());
        } catch (DocumentException e) {
            throw new PortableException(e.getMessage(), e);
        }
    }

    /**
     * 创建builder
     * @return {@link PortableBuilder}
     */
    public static PortableBuilder create() {
        return PortableBuilder.create(Document::new);
    }

    /**
     * 快速构建文档并开启
     * @return {@link PortableBuilder}
     */
    public static PortableBuilder fastCreate() {
        return PortableBuilder.create().open();
    }

    /**
     * 文档开启
     * @return {@link PortableBuilder}
     */
    public PortableBuilder open() {
        this.document.open();
        return this;
    }

    /**
     * 添加事件
     * @param event 任意{@link PdfPageEvent}事件
     * @param <T>   事件类型
     * @return {@link PortableBuilder}
     */
    public <T extends PdfPageEvent> PortableBuilder event(T event) {
        this.writer.setPageEvent(event);
        return this;
    }

    /**
     * 插入分页符
     * @return {@link PortableBuilder}
     */
    public PortableBuilder pageBreak() {
        this.writer.setPageEmpty(false);
        this.document.newPage();
        return this;
    }

    /**
     * 多文档创建 自动分页
     * @param iterable 迭代器
     * @param consumer builder消费
     * @param <U>      迭代元素类型
     * @return {@link PortableBuilder}
     */
    public <U> PortableBuilder documents(Iterable<U> iterable, BiConsumer<U, PortableBuilder> consumer) {
        if (Objects.nonNull(iterable)) {
            Iterator<U> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                consumer.accept(iterator.next(), this);
                // 自动插入分页符
                if (iterator.hasNext()) {
                    this.pageBreak();
                }
            }
        }

        return this;
    }

    /**
     * 将pdf文档写到servlet输出流
     * @param fileName 下载文件名名称
     */
    public void writeToServletResponse(String fileName) {
        HttpServletResponse response = HttpServletUtil.getCurrentResponse();
        try {
            // http文件名处理
            HttpResponseUtil.handleOutputFileName(PortableFileType.PDF.fullName(fileName), response);

            this.writeTo(response.getOutputStream(), false);
        } catch (IOException e) {
            throw new PortableException(e.getMessage(), e);
        }
    }

    /**
     * 将pdf文档写到给定输出流并关闭流
     * @param outputStream 输出流
     * @param closeable    是否需要关闭输出流
     */
    public void writeTo(OutputStream outputStream, boolean closeable) {
        this.doWrite(outputStream, closeable);
    }

    /**
     * 将pdf文档写到给定输出流并关闭流
     * @param outputStream 输出流
     */
    public void writeTo(OutputStream outputStream) {
        this.writeTo(outputStream, true);
    }

    /**
     * 将pdf文档写到输出流
     * @param outputStream 输出流
     * @param closeable    是否需要关闭输出流
     */
    protected void doWrite(OutputStream outputStream, boolean closeable) {
        try {
            // 若文档为空 添加一页空文档
            if (this.document.getPageNumber() == 0) {
                this.writer.setPageEmpty(false);
                this.document.newPage();
            }

            // 先关闭文档再访问字节输出流
            this.document.close();
            this.writer.flush();
            this.writer.close();

            this.byteArrayOutputStream.writeTo(outputStream);
        } catch (IOException e) {
            throw new PortableException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(this.byteArrayOutputStream);
            // 若需要关闭输入流则关闭 如ServletOutputStream则不能关闭
            if (closeable) {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

    @Override
    void addElement(Element element) {
        try {
            this.document.add(element);
        } catch (DocumentException e) {
            throw new PortableException(e.getMessage(), e);
        }
    }
}
