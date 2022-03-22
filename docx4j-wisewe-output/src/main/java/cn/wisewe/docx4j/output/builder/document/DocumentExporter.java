package cn.wisewe.docx4j.output.builder.document;

import cn.wisewe.docx4j.output.builder.Exportable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.util.IOUtils;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.StylesDocument;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * word文档构造器
 * @author xiehai
 * @date 2020/12/24 11:56
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentExporter extends RichableDocument<DocumentExporter> implements Exportable {
    XWPFDocument document;

    private DocumentExporter(XWPFDocument document) {
        // 若文档样式为空则创建样式集
        if (Objects.isNull(document.getStyles())) {
            document.createStyles();
        }

        this.document = document;
        // 添加默认样式
        try {
            StylesDocument stylesDocument =
                StylesDocument.Factory.parse(DocumentExporter.class.getResourceAsStream("styles.xml"));
            this.document.getStyles().setStyles(stylesDocument.getStyles());
        } catch (XmlException | IOException e) {
            throw new DocumentExportException(e);
        }
    }

    /**
     * 创建word文档
     * @return {@link DocumentExporter}
     */
    public static DocumentExporter create() {
        return new DocumentExporter(new XWPFDocument());
    }

    /**
     * 通过输入流创建word文档
     * @param is {@link InputStream}
     * @return {@link DocumentExporter}
     */
    public static DocumentExporter create(InputStream is) {
        try {
            return new DocumentExporter(new XWPFDocument(is));
        } catch (IOException e) {
            throw new DocumentExportException(e);
        }
    }

    /**
     * 文档设置
     * @param consumer 消费
     * @return {@link DocumentExporter}
     */
    public DocumentExporter accept(Consumer<XWPFDocument> consumer) {
        consumer.accept(this.document);
        return this;
    }

    /**
     * 自定义样式
     * @return {@link DocumentExporter}
     */
    public DocumentExporter style(Supplier<XWPFStyle> supplier) {
        return this.accept(d -> d.getStyles().addStyle(supplier.get()));
    }

    /**
     * 纸张设置
     * @param consumer 自定义纸张
     * @return {@link DocumentExporter}
     */
    public DocumentExporter pageSize(Consumer<CTPageSz> consumer) {
        return
            this.accept(d -> {
                CTDocument1 document = d.getDocument();
                CTBody body = document.getBody();
                if (!body.isSetSectPr()) {
                    body.addNewSectPr();
                }

                CTSectPr sectPr = body.getSectPr();
                if (!sectPr.isSetPgSz()) {
                    sectPr.addNewPgSz();
                }
                consumer.accept(sectPr.getPgSz());
            });
    }

    /**
     * 设置纸张 默认纵向 {@link DocumentRectangle#rotate()}转为横向
     * @return {@link DocumentExporter}
     */
    public DocumentExporter pageSize(DocumentRectangle rectangle) {
        return this.pageSize(rectangle::apply);
    }

    /**
     * 设置纸张大小及方向
     * @param paperSize   纸张大小
     * @param orientation 方向
     * @return {@link DocumentExporter}
     */
    public DocumentExporter pageSize(DocumentPaperSize paperSize) {
        return this.pageSize(paperSize.rectangle());
    }

    /**
     * 添加页眉
     * @param type     页眉类型
     * @param consumer 页眉消费
     * @return {@link DocumentExporter}
     */
    public DocumentExporter header(HeaderFooterType type, Consumer<DslHeader> consumer) {
        consumer.accept(new DslHeader(this.document.createHeader(type)));
        return this;
    }

    /**
     * 添加默认所有页的页眉文本
     * @param text 文本内容
     * @return {@link DocumentExporter}
     */
    public DocumentExporter header(String text) {
        return this.header(HeaderFooterType.DEFAULT, header -> header.textParagraph(text));
    }

    /**
     * 多文档创建 自动分页
     * @param iterable 迭代器
     * @param consumer builder消费
     * @param <U>      迭代元素类型
     * @return {@link DocumentExporter}
     */
    public <U> DocumentExporter documents(Iterable<U> iterable, BiConsumer<U, DocumentExporter> consumer) {
        if (Objects.nonNull(iterable)) {
            Iterator<U> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                consumer.accept(iterator.next(), this);

                // 若存在下一项则添加分页符
                if (iterator.hasNext()) {
                    // 添加自动换页
                    super.pageBreak();
                }
            }
        }

        return this;
    }

    /**
     * 添加页脚
     * @param type     页脚类型
     * @param consumer 页脚消费
     * @return {@link DocumentExporter}
     */
    public DocumentExporter footer(HeaderFooterType type, Consumer<DslFooter> consumer) {
        consumer.accept(new DslFooter(this.document.createFooter(type)));
        return this;
    }

    /**
     * 添加默认所有的页脚文本
     * @param text 文本内容
     * @return {@link DocumentExporter}
     */
    public DocumentExporter footer(String text) {
        return this.footer(HeaderFooterType.DEFAULT, footer -> footer.textParagraph(text));
    }

    @Override
    public DocumentFileType defaultFileType() {
        return DocumentFileType.DOCX;
    }

    @Override
    public void writeTo(OutputStream outputStream, boolean closeable) {
        try {
            this.document.write(outputStream);
        } catch (IOException e) {
            throw new DocumentExportException(e);
        } finally {
            // 文档流关闭
            IOUtils.closeQuietly(this.document);
            // 若需要关闭输入流则关闭 如ServletOutputStream则不能关闭
            if (closeable) {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

    @Override
    protected XWPFParagraph createParagraph() {
        return this.document.createParagraph();
    }

    @Override
    protected XWPFTable createTable(int rows, int columns) {
        return this.document.createTable(rows, columns);
    }
}
