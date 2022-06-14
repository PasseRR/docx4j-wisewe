package cn.wisewe.docx4j.convert.builder.portable;

import cn.wisewe.docx4j.convert.ConvertHandler;
import cn.wisewe.docx4j.convert.builder.Asposes;
import com.aspose.pdf.Document;
import com.aspose.pdf.License;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * pdf处理器
 * @author xiehai
 * @date 2022/06/13 19:40
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
abstract class PortableHandler implements ConvertHandler {
    /**
     * 是否初始化
     */
    private static final AtomicBoolean HAS_LICENSED = new AtomicBoolean();

    @Override
    public void handle(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        Asposes.tryLoadLicense(HAS_LICENSED, () -> PortableHandler.class, new License()::setLicense);
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
