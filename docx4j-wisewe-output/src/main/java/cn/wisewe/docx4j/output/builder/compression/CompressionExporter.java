package cn.wisewe.docx4j.output.builder.compression;

import cn.wisewe.docx4j.output.OutputConstants;
import cn.wisewe.docx4j.output.builder.Exportable;
import cn.wisewe.docx4j.output.utils.TrConsumer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩文件构造器
 * @author xiehai
 * @date 2021/01/05 18:14
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompressionExporter implements Exportable {
    ByteArrayOutputStream byteArrayOutputStream;
    ZipOutputStream zipOutputStream;

    CompressionExporter() {
        this(Deflater.DEFAULT_COMPRESSION);
    }

    CompressionExporter(int level) {
        // 默认UTF编码
        this(level, StandardCharsets.UTF_8);
    }

    CompressionExporter(Charset charset) {
        this(Deflater.DEFAULT_COMPRESSION, charset);
    }

    CompressionExporter(int level, Charset charset) {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        // 设置中文路径支持
        this.zipOutputStream = new ZipOutputStream(this.byteArrayOutputStream, charset);
        this.zipOutputStream.setLevel(level);
    }

    /**
     * 设置压缩级别及编码
     * @param level   压缩级别
     * @param charset 编码
     * @return {@link CompressionExporter}
     */
    public static CompressionExporter create(int level, Charset charset) {
        return new CompressionExporter(level, charset);
    }

    /**
     * 设置压缩级别并构建builder
     * @param level 压缩级别 0-9 0不压缩 1最快 9文件最小
     * @return {@link CompressionExporter}
     */
    public static CompressionExporter create(int level) {
        return new CompressionExporter(level);
    }

    /**
     * 设置编码并构建builder
     * @param charset 编码
     * @return {@link CompressionExporter}
     */
    public static CompressionExporter create(Charset charset) {
        return new CompressionExporter(charset);
    }

    /**
     * 快速构建压缩包builder
     * @return {@link CompressionExporter}
     */
    public static CompressionExporter create() {
        return new CompressionExporter();
    }


    /**
     * 添加一个文件进压缩包
     * @param fileName 文件名称
     * @param consumer {@link ZipOutputStream}消费
     * @return {@link CompressionExporter}
     */
    public CompressionExporter file(String fileName, Consumer<ZipOutputStream> consumer) {
        Objects.requireNonNull(fileName);
        try {
            this.zipOutputStream.putNextEntry(new ZipEntry(fileName));
            consumer.accept(this.zipOutputStream);
            this.zipOutputStream.closeEntry();
            return this;
        } catch (IOException e) {
            throw new CompressionExportException(e);
        }
    }

    /**
     * 添加一个{@link InputStream}
     * @param fileName 文件名
     * @param is       {@link InputStream}
     * @return {@link CompressionExporter}
     */
    public CompressionExporter file(String fileName, InputStream is) {
        return
            this.file(fileName, os -> {
                try {
                    IOUtils.copy(is, os);
                } catch (IOException e) {
                    throw new CompressionExportException(e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            });
    }

    /**
     * 添加文件
     * @param file {@link File}
     * @return {@link CompressionExporter}
     */
    public CompressionExporter file(File file) {
        if (file.isDirectory()) {
            throw new CompressionExportException("input file is directory, only file support");
        }

        try {
            return this.file(file.getName(), new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new CompressionExportException(e);
        }
    }

    /**
     * 添加多个文件进压缩包
     * @param iterable 迭代器
     * @param function 文件名生成方法
     * @param consumer {@link ZipOutputStream}消费
     * @param <U>      迭代器元素类型
     * @return {@link CompressionExporter}
     */
    public <U> CompressionExporter files(Iterable<U> iterable, Function<U, String> function,
                                         BiConsumer<U, ZipOutputStream> consumer) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(u -> this.file(function.apply(u), os -> consumer.accept(u, os)));
        }

        return this;
    }

    /**
     * 添加一个文件夹进压缩包
     * @param folderName 文件夹名称
     * @param consumer   {@link CompressionExporter}消费
     * @return {@link CompressionExporter}
     */
    public CompressionExporter folder(String folderName, BiConsumer<String, CompressionExporter> consumer) {
        Objects.requireNonNull(folderName);
        // 文件路径必须以/结尾
        String folderPath =
            Optional.of(folderName)
                .filter(it -> it.endsWith(OutputConstants.SLASH))
                .orElseGet(() -> folderName + OutputConstants.SLASH);

        // 添加文件夹
        this.file(folderPath, os -> consumer.accept(folderPath, this));

        return this;
    }

    /**
     * 文件目录
     * @param file {@link File}
     * @return {@link CompressionExporter}
     */
    public CompressionExporter folder(File file) {
        if (!file.isDirectory()) {
            throw new CompressionExportException("input file is file, only directory support");
        }
        return this.folder(file.getName(), (fp, b) -> {});
    }

    /**
     * 添加多个文件夹
     * @param iterable 文件夹迭代器
     * @param function 文件夹名称生成方法
     * @param consumer {@link CompressionExporter}消费
     * @param <U>      迭代器元素类型
     * @return {@link CompressionExporter}
     */
    public <U> CompressionExporter folders(Iterable<U> iterable, Function<U, String> function,
                                           TrConsumer<U, String, CompressionExporter> consumer) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(u -> this.folder(function.apply(u), (fn, b) -> consumer.accept(u, fn, b)));
        }
        return this;
    }

    /**
     * 直接添加文件/目录到压缩包
     * @param file {@link File}
     * @return {@link CompressionExporter}
     */
    public CompressionExporter any(File file) {
        Objects.requireNonNull(file);

        return file.isDirectory() ? this.folder(file) : this.file(file);
    }

    @Override
    public CompressionFileType defaultFileType() {
        return CompressionFileType.ZIP;
    }

    @Override
    public void writeTo(OutputStream outputStream, boolean closeable) {
        try {
            // 结束压缩
            this.zipOutputStream.finish();
            // 将内存文件写到输出流
            this.byteArrayOutputStream.writeTo(outputStream);
        } catch (IOException e) {
            throw new CompressionExportException(e);
        } finally {
            // 输出流关闭
            IOUtils.closeQuietly(this.zipOutputStream);
            IOUtils.closeQuietly(this.byteArrayOutputStream);
            // 若需要关闭输入流则关闭 如ServletOutputStream则不能关闭
            if (closeable) {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }
}
