package cn.wisewe.docx4j.convert.builder.document;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.util.IOUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * word文档转换
 * @author xiehai
 * @date 2022/03/23 15:59
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentConverter {
    BufferedInputStream bufferedInputStream;
    OutputStream outputStream;

    DocumentConverter() {
    }

    public static DocumentConverter create() {
        return new DocumentConverter();
    }

    public DocumentConverter input(InputStream inputStream) {
        this.bufferedInputStream = new BufferedInputStream(inputStream);
        return this;
    }

    public DocumentConverter output(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

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
