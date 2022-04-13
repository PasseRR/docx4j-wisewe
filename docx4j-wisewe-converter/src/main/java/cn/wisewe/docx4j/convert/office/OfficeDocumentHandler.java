package cn.wisewe.docx4j.convert.office;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * office文档转换处理器
 * @param <T> 文档类型
 * @author xiehai
 * @date 2022/03/25 09:54
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class OfficeDocumentHandler<T> {
    /**
     * 新建文档
     * @return 文档提供器
     */
    protected abstract Supplier<T> newDocument();

    /**
     * 前置处理flat xml格式文档
     * @return 处理过程
     */
    protected abstract BiConsumer<T, InputStream> preHandleFlat();

    /**
     * 前置处理二进制格式文档
     * @return 处理过程
     */
    protected abstract BiConsumer<T, InputStream> preHandleBinary();

    /**
     * 前置处理压缩包格式文档
     * @return 处理过程
     */
    protected abstract BiConsumer<T, InputStream> preHandleZipped();

    /**
     * 对外处理文档方法
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     */
    protected void handle(BufferedInputStream inputStream, OutputStream outputStream) {
        // 通过文件头的两个字节判断文件类型
        T t = this.newDocument().get();
        switch (OfficeFileType.type(inputStream)) {
            case ZIP: {
                this.preHandleZipped().accept(t, inputStream);
                break;
            }
            case COMPOUND: {
                this.preHandleBinary().accept(t, inputStream);
                break;
            }
            default: {
                this.preHandleFlat().accept(t, inputStream);
                break;
            }
        }

        this.postHandle(t, outputStream);
    }

    /**
     * 后置处理
     * @param t            文档
     * @param outputStream 输出流
     */
    protected abstract void postHandle(T t, OutputStream outputStream);

    /**
     * html {@link Document} 移动端支持
     * @param document {@link Document}
     */
    protected static void mobileSupport(Document document) {
        NodeList head = document.getElementsByTagName("head");
        if (head.getLength() > 0) {

            head.item(0).appendChild(mobileMeta(document));
        }
    }

    /**
     * 移动端meta节点
     * @param document {@link Document}
     * @return {@link Element}
     */
    protected static Element mobileMeta(Document document) {
        Element meta = document.createElement("meta");
        meta.setAttribute("name", "viewport");
        meta.setAttribute(
            "content",
            "width=device-width,height=device-height, user-scalable=no,initial-scale=1, minimum-scale=1," +
                "maximum-scale=1,target-densitydpi=device-dpi"
        );
        return meta;
    }
}
