package cn.wisewe.docx4j.output.builder.portable;

import cn.wisewe.docx4j.output.builder.Exportable;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Pdf文档构建器
 * @author xiehai
 * @date 2020/12/31 11:54
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PortableExporter extends PortableDocument<PortableExporter> implements Exportable {
    /**
     * pdf文档
     */
    Document document;
    /**
     * 内存字节流
     */
    ByteArrayOutputStream byteArrayOutputStream;
    PdfWriter writer;

    PortableExporter(Document document) throws DocumentException {
        this.document = document;
        this.byteArrayOutputStream = new ByteArrayOutputStream(1024);
        this.writer = PdfWriter.getInstance(this.document, this.byteArrayOutputStream);
    }

    /**
     * 使用指定{@link Document}创建
     * @param supplier {@link Document}提供器
     * @return {@link PortableExporter}
     */
    public static PortableExporter create(Supplier<Document> supplier) {
        try {
            return new PortableExporter(supplier.get());
        } catch (DocumentException e) {
            throw new PortableExportException(e);
        }
    }

    /**
     * 创建builder
     * @return {@link PortableExporter}
     */
    public static PortableExporter create() {
        return PortableExporter.create(Document::new);
    }

    /**
     * 快速构建文档并开启
     * @return {@link PortableExporter}
     */
    public static PortableExporter fastCreate() {
        return PortableExporter.create().open();
    }

    /**
     * 快速构建给定文档并开启
     * @param supplier {@link Document}提供
     * @return {@link PortableExporter}
     */
    public static PortableExporter fastCreate(Supplier<Document> supplier) {
        return PortableExporter.create(supplier).open();
    }

    /**
     * 文档开启
     * @return {@link PortableExporter}
     */
    public PortableExporter open() {
        return this.documentAccept(Document::open);
    }

    /**
     * {@link Document}设置
     * @param consumer 设置方法
     * @return {@link PortableExporter}
     */
    public PortableExporter documentAccept(Consumer<Document> consumer) {
        consumer.accept(this.document);
        return this;
    }

    /**
     * {@link PdfWriter}设置
     * @param consumer 设置方法
     * @return {@link PortableExporter}
     */
    public PortableExporter writerAccept(Consumer<PdfWriter> consumer) {
        consumer.accept(this.writer);
        return this;
    }

    /**
     * 纸张设置{@link com.itextpdf.text.PageSize}中的常量定义 纸张设置应该在open之前，横纵切换{@link Rectangle#rotate()}
     * @param rectangle 纸张矩形大小
     * @return {@link PortableExporter}
     */
    public PortableExporter pageSize(Rectangle rectangle) {
        return
            this.documentAccept(d -> {
                if (d.isOpen()) {
                    throw new PortableExportException("设置纸张大小应该在open之前");
                }
                d.setPageSize(rectangle);
            });
    }

    /**
     * 添加事件
     * @param event 任意{@link PdfPageEvent}事件
     * @param <T>   事件类型
     * @return {@link PortableExporter}
     */
    public <T extends PdfPageEvent> PortableExporter event(T event) {
        return this.events(Collections.singletonList(event));
    }

    /**
     * 添加多个事件
     * @param events {@link List}
     * @param <T>    事件类型
     * @return {@link PortableExporter}
     */
    public <T extends PdfPageEvent> PortableExporter events(List<T> events) {
        if (Objects.nonNull(events) && !events.isEmpty()) {
            if (this.document.isOpen()) {
                throw new PortableExportException("事件设置应该open之前");
            }
            events.forEach(this.writer::setPageEvent);
        }

        return this;
    }

    /**
     * 插入分页符
     * @return {@link PortableExporter}
     */
    public PortableExporter pageBreak() {
        this.writerAccept(w -> w.setPageEmpty(false));
        return this.documentAccept(Document::newPage);
    }

    /**
     * 多文档创建 自动分页
     * @param iterable 迭代器
     * @param consumer builder消费
     * @param <U>      迭代元素类型
     * @return {@link PortableExporter}
     */
    public <U> PortableExporter documents(Iterable<U> iterable, BiConsumer<U, PortableExporter> consumer) {
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

    @Override
    public PortableFileType defaultFileType() {
        return PortableFileType.PDF;
    }


    @Override
    public void writeTo(OutputStream outputStream, boolean closeable) {
        try {
            // 若文档为空 添加一页空文档
            if (this.document.getPageNumber() == 0) {
                this.pageBreak();
            }

            // 先关闭文档再访问字节输出流
            this.document.close();
            this.writerAccept(w -> {
                w.flush();
                w.close();
            });

            this.byteArrayOutputStream.writeTo(outputStream);
        } catch (IOException e) {
            throw new PortableExportException(e);
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
            throw new PortableExportException(e);
        }
    }
}
