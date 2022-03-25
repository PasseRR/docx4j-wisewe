package cn.wisewe.docx4j.convert.builder.sheet;

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
 * excel转换器
 * @author xiehai
 * @date 2022/03/22 11:14
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpreadSheetConverter implements OfficeConverter<SpreadSheetOfficeConvertType> {
    BufferedInputStream inputStream;
    OutputStream outputStream;

    SpreadSheetConverter() {
    }

    public static SpreadSheetConverter create() {
        return new SpreadSheetConverter();
    }

    public SpreadSheetConverter input(InputStream inputStream) {
        this.inputStream = new BufferedInputStream(inputStream);
        return this;
    }

    public SpreadSheetConverter input(File file) {
        try {
            return this.input(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new SpreadSheetConvertException(e);
        }
    }

    public SpreadSheetConverter output(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

    public SpreadSheetConverter output(File file) {
        try {
            return this.output(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new SpreadSheetConvertException(e);
        }
    }

    @Override
    public void convert(SpreadSheetOfficeConvertType type) {
        if (Objects.isNull(this.inputStream)) {
            throw new SpreadSheetConvertException("input stream not set");
        }

        if (Objects.isNull(this.outputStream)) {
            throw new SpreadSheetConvertException("output stream not set");
        }

        try {
            type.getHandler().handle(this.inputStream, this.outputStream);
        } finally {
            IOUtils.closeQuietly(this.inputStream);
            IOUtils.closeQuietly(this.outputStream);
        }
    }
}
