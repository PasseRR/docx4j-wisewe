package cn.wisewe.docx4j.output.builder.compression;

import cn.wisewe.docx4j.output.OutputConstants;
import cn.wisewe.docx4j.output.utils.HttpResponseUtil;
import cn.wisewe.docx4j.output.utils.HttpServletUtil;
import cn.wisewe.docx4j.output.utils.TrConsumer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.util.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
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
public class CompressionBuilder {
    ByteArrayOutputStream byteArrayOutputStream;
    ZipOutputStream zipOutputStream;

    CompressionBuilder(int level) {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        // 设置中文路径支持
        this.zipOutputStream = new ZipOutputStream(this.byteArrayOutputStream, Charset.forName("GBK"));
        this.zipOutputStream.setLevel(level);
    }

    /**
     * 快速构建压缩包builder
     * @return {@link CompressionBuilder}
     */
    public static CompressionBuilder create() {
        return CompressionBuilder.create(Deflater.DEFAULT_COMPRESSION);
    }

    /**
     * 设置压缩级别并构建builder
     * @param level 压缩级别 0-9 0不压缩 1最快 9文件最小
     * @return {@link CompressionBuilder}
     */
    public static CompressionBuilder create(int level) {
        return new CompressionBuilder(level);
    }

    /**
     * 添加一个文件进压缩包
     * @param fileName 文件名称
     * @param consumer {@link ZipOutputStream}消费
     * @return {@link CompressionBuilder}
     */
    public CompressionBuilder file(String fileName, Consumer<ZipOutputStream> consumer) {
        Objects.requireNonNull(fileName);
        try {
            this.zipOutputStream.putNextEntry(new ZipEntry(fileName));
            consumer.accept(this.zipOutputStream);
            this.zipOutputStream.closeEntry();
            return this;
        } catch (IOException e) {
            throw new CompressionException(e.getMessage(), e);
        }
    }

    /**
     * 添加文件
     * @param file {@link File}
     * @return {@link CompressionBuilder}
     */
    public CompressionBuilder file(File file) {
        if (file.isDirectory()) {
            throw new CompressionException("input file is directory, only file support");
        }

        return
            this.file(file.getName(), os -> {
                try {
                    IOUtils.copy(new FileInputStream(file), os);
                } catch (IOException e) {
                    throw new CompressionException(e.getMessage(), e);
                }
            });
    }

    /**
     * 添加多个文件进压缩包
     * @param iterable 迭代器
     * @param function 文件名生成方法
     * @param consumer {@link ZipOutputStream}消费
     * @param <U>      迭代器元素类型
     * @return {@link CompressionBuilder}
     */
    public <U> CompressionBuilder files(Iterable<U> iterable, Function<U, String> function,
                                        BiConsumer<U, ZipOutputStream> consumer) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(u -> this.file(function.apply(u), os -> consumer.accept(u, os)));
        }

        return this;
    }

    /**
     * 添加一个文件夹进压缩包
     * @param folderName 文件夹名称
     * @param consumer   {@link CompressionBuilder}消费
     * @return {@link CompressionBuilder}
     */
    public CompressionBuilder folder(String folderName, BiConsumer<String, CompressionBuilder> consumer) {
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
     * @return {@link CompressionBuilder}
     */
    public CompressionBuilder folder(File file) {
        if (!file.isDirectory()) {
            throw new CompressionException("input file is file, only directory support");
        }
        return this.folder(file.getName(), (fp, b) -> {});
    }

    /**
     * 添加多个文件夹
     * @param iterable 文件夹迭代器
     * @param function 文件夹名称生成方法
     * @param consumer {@link CompressionBuilder}消费
     * @param <U>      迭代器元素类型
     * @return {@link CompressionBuilder}
     */
    public <U> CompressionBuilder folders(Iterable<U> iterable, Function<U, String> function,
                                          TrConsumer<U, String, CompressionBuilder> consumer) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(u -> this.folder(function.apply(u), (fn, b) -> consumer.accept(u, fn, b)));
        }
        return this;
    }

    /**
     * 直接添加文件/目录到压缩包
     * @param file {@link File}
     * @return {@link CompressionBuilder}
     */
    public CompressionBuilder any(File file) {
        Objects.requireNonNull(file);

        return file.isDirectory() ? this.folder(file) : this.file(file);
    }

    /**
     * 将word文档写到servlet输出流并指定文件后缀
     * @param fileName 文件名
     */
    public void writeToServletResponse(String fileName) {
        HttpServletResponse response = HttpServletUtil.getCurrentResponse();
        try {
            // http文件名处理 并固定为zip后缀
            HttpResponseUtil.handleOutputFileName(CompressionFileType.ZIP.fullName(fileName), response);
            this.writeTo(response.getOutputStream(), false);
        } catch (IOException e) {
            throw new CompressionException(e.getMessage(), e);
        }
    }

    /**
     * 将word文档写到给定输出流并关闭流
     * @param outputStream 输出流
     * @param closeable    是否需要关闭输出流
     */
    public void writeTo(OutputStream outputStream, boolean closeable) {
        this.doWrite(outputStream, closeable);
    }

    /**
     * 将word文档写到给定输出流并关闭流
     * @param outputStream 输出流
     */
    public void writeTo(OutputStream outputStream) {
        this.writeTo(outputStream, true);
    }

    /**
     * 将word文档写到输出流
     * @param outputStream 输出流
     * @param closeable    是否需要关闭输出流
     */
    protected void doWrite(OutputStream outputStream, boolean closeable) {
        try {
            // 结束压缩
            this.zipOutputStream.finish();
            // 将内存文件写到输出流
            this.byteArrayOutputStream.writeTo(outputStream);
        } catch (IOException e) {
            throw new CompressionException(e.getMessage(), e);
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
