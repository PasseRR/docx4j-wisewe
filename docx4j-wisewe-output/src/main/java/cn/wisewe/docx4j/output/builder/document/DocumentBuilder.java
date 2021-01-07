package cn.wisewe.docx4j.output.builder.document;

import cn.wisewe.docx4j.output.utils.FileUtil;
import cn.wisewe.docx4j.output.utils.HttpResponseUtil;
import cn.wisewe.docx4j.output.utils.HttpServletUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.util.IOUtils;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.StylesDocument;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
public class DocumentBuilder extends RichableDocument<DocumentBuilder> {
    XWPFDocument document;

    private DocumentBuilder(XWPFDocument document) {
        // 若文档样式为空则创建样式集
        if (Objects.isNull(document.getStyles())) {
            document.createStyles();
        }

        this.document = document;
        // 添加默认样式
        try {
            StylesDocument stylesDocument =
                StylesDocument.Factory.parse(new File(FileUtil.brotherPath(DocumentBuilder.class, "styles.xml")));
            this.document.getStyles().setStyles(stylesDocument.getStyles());
        } catch (XmlException | IOException e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }

    /**
     * 创建word文档
     * @return {@link DocumentBuilder}
     */
    public static DocumentBuilder create() {
        return new DocumentBuilder(new XWPFDocument());
    }

    /**
     * 通过输入流创建word文档
     * @param is {@link InputStream}
     * @return {@link DocumentBuilder}
     */
    public static DocumentBuilder create(InputStream is) {
        try {
            return new DocumentBuilder(new XWPFDocument(is));
        } catch (IOException e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }

    /**
     * 自定义样式
     * @return {@link DocumentBuilder}
     */
    public DocumentBuilder style(Supplier<XWPFStyle> supplier) {
        this.addStyle(supplier);
        return this;
    }

    /**
     * 添加页眉
     * @param type     页眉类型
     * @param consumer 页眉消费
     * @return {@link DocumentBuilder}
     */
    public DocumentBuilder header(HeaderFooterType type, Consumer<DslHeader> consumer) {
        consumer.accept(new DslHeader(this.document.createHeader(type)));
        return this;
    }

    /**
     * 添加默认所有页的页眉文本
     * @param text 文本内容
     * @return {@link DocumentBuilder}
     */
    public DocumentBuilder header(String text) {
        return this.header(HeaderFooterType.DEFAULT, header -> header.textParagraph(text));
    }

    /**
     * 多文档创建 自动分页
     * @param iterable 迭代器
     * @param consumer builder消费
     * @param <U>      迭代元素类型
     * @return {@link DocumentBuilder}
     */
    public <U> DocumentBuilder documents(Iterable<U> iterable, BiConsumer<U, DocumentBuilder> consumer) {
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
     * @return {@link DocumentBuilder}
     */
    public DocumentBuilder footer(HeaderFooterType type, Consumer<DslFooter> consumer) {
        consumer.accept(new DslFooter(this.document.createFooter(type)));
        return this;
    }

    /**
     * 添加默认所有的页脚文本
     * @param text 文本内容
     * @return {@link DocumentBuilder}
     */
    public DocumentBuilder footer(String text) {
        return this.footer(HeaderFooterType.DEFAULT, footer -> footer.textParagraph(text));
    }

    /**
     * 将word文档写到servlet输出流并指定文件后缀
     * @param fileName 文件名
     */
    public void writeToServletResponse(String fileName) {
        HttpServletResponse response = HttpServletUtil.getCurrentResponse();
        try {
            // http文件名处理 并固定为docx后缀
            HttpResponseUtil.handleOutputFileName(DocumentFileType.DOCX.fullName(fileName), response);
            this.writeTo(response.getOutputStream(), false);
        } catch (IOException e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }

    /**
     * 将word文档写到给定输出流并关闭流
     * @param outputStream 输出流
     * @param closeable    是否需要关闭输出流
     */
    public void writeTo(OutputStream outputStream, boolean closeable) {
        this.doWrite(outputStream, closeable);
    }

    /**
     * 将word文档写到给定输出流并关闭流
     * @param outputStream 输出流
     */
    public void writeTo(OutputStream outputStream) {
        this.writeTo(outputStream, true);
    }

    /**
     * 为文档添加一个特定样式
     * @param supplier 样式提供
     */
    protected void addStyle(Supplier<XWPFStyle> supplier) {
        this.document.getStyles().addStyle(supplier.get());
    }

    /**
     * 为文档添加一个特定样式
     * @param style {@link XWPFStyle}
     */
    protected void addStyle(XWPFStyle style) {
        this.addStyle(() -> style);
    }

    /**
     * 将word文档写到输出流
     * @param outputStream 输出流
     * @param closeable    是否需要关闭输出流
     */
    protected void doWrite(OutputStream outputStream, boolean closeable) {
        try {
            this.document.write(outputStream);
        } catch (IOException e) {
            throw new DocumentException(e.getMessage(), e);
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
