package cn.wisewe.docx4j.convert.office;

import cn.wisewe.docx4j.convert.ConvertException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.util.IOUtils;

import javax.annotation.Generated;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Function;


/**
 * office文件转换器
 * @author xiehai
 * @date 2022/03/25 12:48
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
@FieldDefaults(level = AccessLevel.PROTECTED)
@Generated({})
public abstract class OfficeConverter<T extends OfficeConverter<?, U>, U extends OfficeConvertHandler> {
    BufferedInputStream inputStream;
    OutputStream outputStream;

    /**
     * 待转换文件输入流
     * @param inputStream {@link InputStream}
     * @return {@link T}
     */
    public T input(InputStream inputStream) {
        this.inputStream = new BufferedInputStream(inputStream);
        return (T) this;
    }

    /**
     * 待转换文件
     * @param file {@link File}
     * @return {@link T}
     */
    public T input(File file) {
        try {
            return this.input(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw this.exception().apply(e);
        }
    }

    /**
     * 转换后文件输出流
     * @param outputStream {@link OutputStream}
     * @return {@link T}
     */
    public T output(OutputStream outputStream) {
        this.outputStream = outputStream;

        return (T) this;
    }

    /**
     * 转换后文件
     * @param file {@link File}
     * @return {@link T}
     */
    public T output(File file) {
        try {
            return this.output(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw this.exception().apply(e);
        }
    }

    /**
     * 文档转换
     * @param type 转换类型
     */
    public void convert(U type) {
        if (Objects.isNull(this.inputStream)) {
            throw this.messageException().apply("input stream not set");
        }

        if (Objects.isNull(this.outputStream)) {
            throw this.messageException().apply("output stream not set");
        }

        try {
            type.getHandler().handle(this.inputStream, this.outputStream);
        } finally {
            IOUtils.closeQuietly(this.inputStream);
            IOUtils.closeQuietly(this.outputStream);
        }
    }

    /**
     * 消息异常
     * @return {@link Function}
     */
    protected abstract Function<String, ConvertException> messageException();

    /**
     * 异常转换
     * @return {@link Function}
     */
    protected abstract Function<Exception, ConvertException> exception();
}
