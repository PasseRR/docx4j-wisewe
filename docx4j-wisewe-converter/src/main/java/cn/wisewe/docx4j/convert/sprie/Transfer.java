package cn.wisewe.docx4j.convert.sprie;

import cn.wisewe.docx4j.convert.ConvertException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 转换器
 * @author xiehai
 * @date 2022/04/14 20:17
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
class Transfer<T> {
    final Consumer<OutputStream> consumer;
    Function<ByteArrayInputStream, T> function;

    Transfer(Consumer<OutputStream> consumer) {
        this.consumer = consumer;
    }

    /**
     * 工厂方法
     * @param consumer 输出流处理
     * @param <T>      转换类型
     * @return {@link Transfer}
     */
    static <T> Transfer<T> create(Consumer<OutputStream> consumer) {
        return new Transfer<>(consumer);
    }

    /**
     * 转换为源对象
     * @param function 转换方法
     * @return {@link Transfer}
     */
    Transfer<T> source(Function<ByteArrayInputStream, T> function) {
        this.function = function;
        return this;
    }

    /**
     * 将目标对象传输至输出流
     * @param transfer {@link Consumer}
     */
    void transfer(Consumer<T> transfer) {
        Objects.requireNonNull(transfer);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            this.consumer.accept(baos);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                Objects.requireNonNull(this.function);
                transfer.accept(this.function.apply(bais));
            }
        } catch (IOException e) {
            throw new ConvertException(e);
        }
    }
}
