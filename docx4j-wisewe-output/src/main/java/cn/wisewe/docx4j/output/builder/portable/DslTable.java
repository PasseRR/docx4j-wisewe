package cn.wisewe.docx4j.output.builder.portable;

import com.itextpdf.text.pdf.PdfPTable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * pdf表格dsl
 * @author xiehai
 * @date 2021/01/04 18:47
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslTable {
    PdfPTable table;

    DslTable(PdfPTable table) {
        this.table = table;
    }

    /**
     * 表格其他设置
     * @param consumer 表格消费
     * @return {@link DslTable}
     */
    public DslTable accept(Consumer<PdfPTable> consumer) {
        consumer.accept(this.table);
        return this;
    }

    /**
     * 表格其他设置 兼容方法 使用{@link #accept(Consumer)}代替，后期会移除此方法
     * @param consumer 设置方法
     * @return {@link DslTable}
     */
    public DslTable more(Consumer<PdfPTable> consumer) {
        return this.accept(consumer);
    }

    /**
     * 添加单行数据
     * @param consumer 行消费
     * @return {@link DslTable}
     */
    public DslTable row(Consumer<DslRow> consumer) {
        DslRow row = new DslRow();
        consumer.accept(row);
        // 将行单元格数据依次添加
        row.getCells().forEach(c -> this.accept(t -> t.addCell(c)));

        return this;
    }

    /**
     * 添加多行数据
     * @param iterable 迭代器
     * @param consumer 迭代元素消费
     * @param <U>      迭代元素类型
     * @return {@link DslTable}
     */
    public <U> DslTable rows(Iterable<U> iterable, BiConsumer<U, DslRow> consumer) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(u -> this.row(t -> consumer.accept(u, t)));
        }

        return this;
    }
}
