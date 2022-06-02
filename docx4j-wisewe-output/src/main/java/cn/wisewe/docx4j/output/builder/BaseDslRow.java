package cn.wisewe.docx4j.output.builder;

import javax.annotation.Generated;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * dsl表格数据行抽象 针对表格excel、word、pdf均区分表头和数据单元格
 * @param <T> 行类型
 * @param <U> 列类型
 * @author xiehai
 * @date 2022/06/01 16:25
 * @see cn.wisewe.docx4j.output.builder.portable.DslRow
 * @see cn.wisewe.docx4j.output.builder.portable.DslRow
 * @see cn.wisewe.docx4j.output.builder.document.DslTableRow
 */
@Generated({})
public abstract class BaseDslRow<T extends BaseDslRow<?, ?>, U> {
    /**
     * 行添加一个单元格
     * @param o        单元格内容
     * @param consumer 单元格设置
     * @return {@link T}
     */
    public abstract T cell(Object o, Consumer<U> consumer);

    /**
     * 行添加一个单元格
     * @param consumer 单元格消费
     * @return {@link T}
     */
    public T cell(Consumer<U> consumer) {
        return this.cell(null, consumer);
    }

    /**
     * 添加一个表头单元格
     * @param o        表头单元格内容
     * @param consumer 单元格设置
     * @return {@link T}
     */
    public abstract T headCell(Object o, Consumer<U> consumer);

    /**
     * 添加一个表头单元格
     * @param supplier 单元格提供
     * @param consumer 单元格设置
     * @return {@link T}
     */
    public T headCell(Supplier<?> supplier, Consumer<U> consumer) {
        return this.headCell(supplier.get(), consumer);
    }

    /**
     * 添加一个表头单元格
     * @param consumer 单元格消费
     * @return {@link T}
     */
    public T headCell(Consumer<U> consumer) {
        return this.headCell(null, consumer);
    }

    /**
     * 添加一个表头单元格
     * @param o 单元格对象
     * @return {@link T}
     */
    public T headCell(Object o) {
        return this.headCell(o, null);
    }

    /**
     * 添加一个表头单元格
     * @param supplier 表头单元格提供
     * @return {@link T}
     */
    public T headCell(Supplier<?> supplier) {
        return this.headCell(supplier.get());
    }

    /**
     * 添加多个表头单元格
     * @param objects 多个单元格内容
     * @return {@link T}
     */
    public T headCells(Object... objects) {
        if (Objects.nonNull(objects) && objects.length > 0) {
            for (Object object : objects) {
                this.headCell(object);
            }
        }

        return (T) this;
    }

    /**
     * 添加多个表头单元格
     * @param iterable 迭代器
     * @param function 元素处理方法
     * @param <E>      元素类型
     * @return {@link T}
     */
    public <E> T headCells(Iterable<E> iterable, Function<E, ?> function) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(it -> this.headCell(function.apply(it)));
        }

        return (T) this;
    }

    /**
     * 添加多个表头单元格
     * @param iterable 迭代器
     * @param <E>      元素类型
     * @return {@link T}
     */
    public <E> T headCells(Iterable<E> iterable) {
        return this.headCells(iterable, it -> it);
    }

    /**
     * 添加一个数据单元格
     * @param o        数据单元格
     * @param consumer 单元格设置
     * @return {@link T}
     */
    public abstract T dataCell(Object o, Consumer<U> consumer);

    /**
     * 添加一个数据单元格
     * @param supplier 单元格提供
     * @param consumer 单元格设置
     * @return {@link T}
     */
    public T dataCell(Supplier<?> supplier, Consumer<U> consumer) {
        return this.dataCell(supplier.get(), consumer);
    }

    /**
     * 添加一个数据单元格
     * @param consumer 单元格消费
     * @return {@link T}
     */
    public T dataCell(Consumer<U> consumer) {
        return this.dataCell(null, consumer);
    }

    /**
     * 添加一个数据单元格
     * @param o 单元格对象
     * @return {@link T}
     */
    public T dataCell(Object o) {
        return this.dataCell(o, null);
    }

    /**
     * 添加一个数据单元格
     * @param supplier 数据单元格提供
     * @return {@link T}
     */
    public T dataCell(Supplier<?> supplier) {
        return this.dataCell(supplier.get());
    }

    /**
     * 添加多个数据单元格
     * @param suppliers 多个数据单元格提供
     * @return {@link T}
     */
    public T dataCells(Supplier<?>... suppliers) {
        if (Objects.nonNull(suppliers)) {
            for (Supplier<?> supplier : suppliers) {
                this.dataCell(supplier.get());
            }
        }

        return (T) this;
    }

    /**
     * 添加多个数据单元格
     * @param iterable 迭代器
     * @param function 元素处理方法
     * @param <E>      元素类型
     * @return {@link T}
     */
    public <E> T dataCells(Iterable<E> iterable, Function<E, ?> function) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(it -> this.dataCell(function.apply(it)));
        }

        return (T) this;
    }

    /**
     * 添加多个数据单元格
     * @param iterable 迭代器
     * @param <E>      元素类型
     * @return {@link T}
     */
    public <E> T dataCells(Iterable<E> iterable) {
        return this.dataCells(iterable, it -> it);
    }
}
