package cn.wisewe.docx4j.convert.base;

import cn.wisewe.docx4j.convert.ConvertException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.annotation.Generated;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Function;


/**
 * 文件转换器
 * @author xiehai
 * @date 2022/03/25 12:48
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
@Generated({})
public abstract class AbstractConverter<T extends AbstractConverter<?, U>, U extends FileHandler> {
    BufferedInputStream inputStream;
    OutputStream outputStream;
    /**
     * 消息异常转换方法
     */
    Function<String, ? extends ConvertException> messageConvertExceptionFunction;
    /**
     * 异常转换方法
     */
    Function<Exception, ? extends ConvertException> exceptionConvertExceptionFunction;

    protected AbstractConverter(Function<String, ? extends ConvertException> messageConvertExceptionFunction,
                                Function<Exception, ? extends ConvertException> exceptionConvertExceptionFunction) {
        this.messageConvertExceptionFunction = messageConvertExceptionFunction;
        this.exceptionConvertExceptionFunction = exceptionConvertExceptionFunction;
    }

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
            throw this.exceptionConvertExceptionFunction.apply(e);
        }
    }

    /**
     * 按照文件绝对路径
     * @param fullFilePath 待转换文件绝对路径
     * @return {@link T}
     */
    public T input(String fullFilePath) {
        return this.input(new File(fullFilePath));
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
            throw this.exceptionConvertExceptionFunction.apply(e);
        }
    }

    /**
     * 转换后文件绝对路径
     * @param fullFilePath 文件绝对路径
     * @return {@link T}
     */
    public T output(String fullFilePath) {
        return this.output(new File(fullFilePath));
    }

    /**
     * 指定转换类型
     * @param type      转换类型
     * @param closeable 是否关闭输出流
     */
    public void convert(U type, boolean closeable) {
        if (Objects.isNull(this.inputStream)) {
            throw this.messageConvertExceptionFunction.apply("input stream not set");
        }

        if (Objects.isNull(this.outputStream)) {
            throw this.messageConvertExceptionFunction.apply("output stream not set");
        }

        try {
            type.getHandler().handle(this.inputStream, this.outputStream);
        } catch (Exception e) {
            if (e instanceof ConvertException) {
                throw (ConvertException) e;
            }

            throw this.exceptionConvertExceptionFunction.apply(e);
        } finally {
            if (Objects.nonNull(this.inputStream)) {
                try {
                    this.inputStream.close();
                } catch (IOException e) {
                    throw this.exceptionConvertExceptionFunction.apply(e);
                }
            }

            if (closeable && Objects.nonNull(this.outputStream)) {
                try {
                    this.outputStream.close();
                } catch (IOException e) {
                    throw this.exceptionConvertExceptionFunction.apply(e);
                }
            }
        }
    }

    /**
     * 文档转换 默认关闭输出流
     * @param type 转换类型
     */
    public void convert(U type) {
        this.convert(type, true);
    }
}
