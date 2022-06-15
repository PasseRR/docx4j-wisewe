package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.ConvertHandler;
import cn.wisewe.docx4j.convert.builder.Licenses;
import com.aspose.cells.License;
import com.aspose.cells.Workbook;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * excel文档处理器
 * @author xiehai
 * @date 2022/04/13 16:53
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
abstract class SpreadSheetHandler implements ConvertHandler {
    /**
     * 是否初始化
     */
    private static final AtomicBoolean HAS_LICENSED = new AtomicBoolean();

    @Override
    public void handle(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        Licenses.tryLoadLicense(HAS_LICENSED, new License()::setLicense);
        this.postHandle(new Workbook(inputStream), outputStream);
    }

    /**
     * 后置处理
     * @param workbook     {@link Workbook}
     * @param outputStream {@link OutputStream}
     * @throws Exception 异常
     */
    protected abstract void postHandle(Workbook workbook, OutputStream outputStream) throws Exception;
}
