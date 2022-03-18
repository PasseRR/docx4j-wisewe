package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 表格{@link XWPFTable} dsl
 * @author xiehai
 * @date 2020/12/28 19:18
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslTable {
    XWPFTable table;
    AtomicInteger rowIndex;

    DslTable(XWPFTable table) {
        this.table = table;
        this.rowIndex = new AtomicInteger();
    }

    /**
     * table其他设置
     * @param consumer 表格消费
     * @return {@link DslTable}
     */
    public DslTable accept(Consumer<XWPFTable> consumer) {
        consumer.accept(this.table);
        return this;
    }

    /**
     * table更多设置，兼容方法使用{@link #accept(Consumer)}代替，后期会移除此方法
     * @param consumer 设置方法
     * @return {@link DslTable}
     */
    public DslTable more(Consumer<XWPFTable> consumer) {
        return this.accept(consumer);
    }

    /**
     * 添加一行数据
     * @param consumer 行消费
     * @return {@link DslTable}
     */
    public DslTable row(Consumer<DslTableRow> consumer) {
        int index = rowIndex.getAndIncrement();
        consumer.accept(new DslTableRow(this.table.getRow(index), index));

        return this;
    }

    /**
     * 添加多行数据
     * @param iterable 迭代器
     * @param consumer 迭代元素消费
     * @param <U>      迭代元素类型
     * @return {@link DslTable}
     */
    public <U> DslTable rows(Iterable<U> iterable, BiConsumer<U, DslTableRow> consumer) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(u -> this.row(r -> consumer.accept(u, r)));
        }

        return this;
    }
}
