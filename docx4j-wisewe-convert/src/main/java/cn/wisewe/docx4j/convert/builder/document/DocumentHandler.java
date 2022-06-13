package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.ConvertHandler;
import com.aspose.words.Document;
import com.aspose.words.License;

import java.io.BufferedInputStream;
import java.io.OutputStream;

/**
 * 文档处理
 * @author xiehai
 * @date 2022/06/13 18:47
 */
abstract class DocumentHandler implements ConvertHandler {
    /**
     * 是否初始化
     */
    private static volatile boolean hasLicensed = false;

    @Override
    public void handle(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        if (!hasLicensed) {
            synchronized (DocumentHandler.class) {
                if (!hasLicensed) {
                    new License().setLicense("/license.xml");
                    hasLicensed = true;
                }
            }
        }
        this.postHandle(new Document(inputStream), outputStream);
    }

    /**
     * 后置处理
     * @param document     {@link Document}
     * @param outputStream {@link OutputStream}
     * @throws Exception 异常
     */
    protected abstract void postHandle(Document document, OutputStream outputStream) throws Exception;
}
