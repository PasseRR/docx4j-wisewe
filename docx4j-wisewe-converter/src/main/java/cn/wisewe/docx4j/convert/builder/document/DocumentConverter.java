package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.office.OfficeConverter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.util.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * word文档转换
 * @author xiehai
 * @date 2022/03/23 15:59
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentConverter implements OfficeConverter<DocumentConvertType> {
    BufferedInputStream bufferedInputStream;
    OutputStream outputStream;

    DocumentConverter() {
    }

    /**
     * 快速创建word转换器工厂方法
     * @return {@link DocumentConverter}
     */
    public static DocumentConverter create() {
        return new DocumentConverter();
    }

    /**
     * 待转换文件输入流
     * @param inputStream {@link InputStream}
     * @return {@link DocumentConverter}
     */
    public DocumentConverter input(InputStream inputStream) {
        this.bufferedInputStream = new BufferedInputStream(inputStream);
        return this;
    }

    /**
     * 待转换文件
     * @param file {@link File}
     * @return {@link DocumentConverter}
     */
    public DocumentConverter input(File file) {
        try {
            return this.input(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new DocumentConvertException(e);
        }
    }

    /**
     * 转换后文件输出流
     * @param outputStream {@link OutputStream}
     * @return {@link OutputStream}
     */
    public DocumentConverter output(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

    /**
     * 转换后文件
     * @param file {@link File}
     * @return {@link DocumentConverter}
     */
    public DocumentConverter output(File file) {
        try {
            return this.output(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new DocumentConvertException(e);
        }
    }


    @Override
    public void convert(DocumentConvertType type) {
        if (Objects.isNull(this.bufferedInputStream)) {
            throw new DocumentConvertException("input stream not set");
        }

        if (Objects.isNull(this.outputStream)) {
            throw new DocumentConvertException("output stream not set");
        }

        try {
            type.getHandler().handle(bufferedInputStream, outputStream);
        } finally {
            IOUtils.closeQuietly(this.bufferedInputStream);
            IOUtils.closeQuietly(this.outputStream);
        }
    }
}
