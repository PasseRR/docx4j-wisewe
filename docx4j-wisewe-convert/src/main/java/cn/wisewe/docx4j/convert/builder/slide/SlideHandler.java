package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.ConvertHandler;
import cn.wisewe.docx4j.convert.builder.Asposes;
import com.aspose.slides.License;
import com.aspose.slides.Presentation;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ppt文档处理器
 * @author xiehai
 * @date 2022/04/13 16:53
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
abstract class SlideHandler implements ConvertHandler {
    /**
     * 是否初始化
     */
    private static final AtomicBoolean HAS_LICENSED = new AtomicBoolean();

    @Override
    public void handle(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        Asposes.tryLoadLicense(HAS_LICENSED, () -> SlideHandler.class, new License()::setLicense);
        this.postHandle(new Presentation(inputStream), outputStream);
    }

    /**
     * 后置处理
     * @param presentation {@link Presentation}
     * @param outputStream {@link OutputStream}
     * @throws Exception 异常
     */
    protected abstract void postHandle(Presentation presentation, OutputStream outputStream) throws Exception;
}
