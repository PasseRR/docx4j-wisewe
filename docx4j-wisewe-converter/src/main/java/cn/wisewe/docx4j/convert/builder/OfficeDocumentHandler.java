package cn.wisewe.docx4j.convert.builder;

import cn.wisewe.docx4j.convert.ConvertException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.function.Function;

/**
 * word文档转换处理器
 * @author xiehai
 * @date 2022/03/25 09:54
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class OfficeDocumentHandler {
    /**
     * 判断当前操作系统是否是windows
     */
    protected static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");

    /**
     * 处理flat xml格式文档
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     * @throws Exception 转换异常
     */
    protected abstract void handleFlat(BufferedInputStream inputStream, OutputStream outputStream) throws Exception;

    /**
     * 处理二进制格式文档
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     * @throws Exception 转换异常
     */
    protected abstract void handleBinary(BufferedInputStream inputStream, OutputStream outputStream) throws Exception;

    /**
     * 处理压缩包格式文档
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     * @throws Exception 转换异常
     */
    protected abstract void handleZipped(BufferedInputStream inputStream, OutputStream outputStream) throws Exception;

    /**
     * 异常转换 精确异常类型
     * @return {@link Function}
     */
    protected Function<Exception, ConvertException> handleException() {
        return ConvertException::new;
    }

    /**
     * 对外处理文档方法
     * @param inputStream  {@link BufferedInputStream}
     * @param outputStream {@link OutputStream}
     */
    public void handle(BufferedInputStream inputStream, OutputStream outputStream) {
        try {
            // 通过文件头的两个字节判断文件类型
            switch (FileType.type(inputStream)) {
                case ZIP: {
                    this.handleZipped(inputStream, outputStream);
                    break;
                }
                case COMPOUND: {
                    this.handleBinary(inputStream, outputStream);
                    break;
                }
                default: {
                    this.handleFlat(inputStream, outputStream);
                    break;
                }
            }
        } catch (Exception e) {
            throw this.handleException().apply(e);
        }
    }

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
