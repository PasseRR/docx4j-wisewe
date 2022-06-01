package cn.wisewe.docx4j.output.builder.portable;

import cn.wisewe.docx4j.output.builder.BaseDslRow;
import cn.wisewe.docx4j.output.utils.StringConverterUtil;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.PackagePrivate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@link com.itextpdf.text.pdf.PdfPRow} dsl
 * @author xiehai
 * @date 2021/01/05 09:08
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslRow extends BaseDslRow<DslRow, DslCell> {
    @Getter
    @PackagePrivate
    List<PdfPCell> cells;

    DslRow() {
        this.cells = new ArrayList<>();
    }

    public DslRow cell(Phrase phrase, Consumer<DslCell> consumer) {
        PdfPCell cell = Optional.ofNullable(phrase).map(PdfPCell::new).orElseGet(PdfPCell::new);
        // 默认单元格垂直居中
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        consumer.accept(new DslCell(cell));
        this.cells.add(cell);

        return this;
    }

    @Override
    public DslRow cell(Consumer<DslCell> consumer) {
        return this.cell(null, consumer);
    }

    @Override
    public DslRow headCell(Consumer<DslCell> consumer) {
        return this.cell(consumer);
    }

    /**
     * 表头单元格
     * @param o        任意对象
     * @param consumer 单元格追加消费
     * @return {@link DslRow}
     */
    public DslRow headCell(Object o, Consumer<DslCell> consumer) {
        return
            this.cell(
                new Phrase(StringConverterUtil.convert(o), Fonts.BOLD_NORMAL.font()),
                c -> {
                    if (Objects.nonNull(consumer)) {
                        consumer.accept(c);
                    }
                }
            );
    }

    /**
     * 表头单元格
     * @param supplier 表头单元格内容提供
     * @param consumer 单元格追加消费
     * @return {@link DslRow}
     */
    public DslRow headCell(Supplier<?> supplier, Consumer<DslCell> consumer) {
        return this.headCell(supplier.get(), consumer);
    }

    /**
     * 添加表头单元格
     * @param o 任意对象
     * @return {@link DslRow}
     */
    @Override
    public DslRow headCell(Object o) {
        // 表头加粗
        return this.headCell(o, null);
    }

    @Override
    public DslRow dataCell(Consumer<DslCell> consumer) {
        return this.cell(consumer);
    }

    /**
     * 数据单元格
     * @param o        文本
     * @param consumer 单元格追加消费
     * @return {@link DslRow}
     */
    public DslRow dataCell(Object o, Consumer<DslCell> consumer) {
        return
            this.cell(
                new Phrase(StringConverterUtil.convert(o), Fonts.NORMAL.font()),
                c -> {
                    if (Objects.nonNull(consumer)) {
                        consumer.accept(c);
                    }
                }
            );
    }

    /**
     * 数据单元格
     * @param supplier 数据提供
     * @param consumer 单元格追加消费
     * @return {@link DslRow}
     */
    public DslRow dataCell(Supplier<?> supplier, Consumer<DslCell> consumer) {
        return this.dataCell(supplier.get(), consumer);
    }

    /**
     * 添加数据单元格
     * @param o 文本
     * @return {@link DslRow}
     */
    @Override
    public DslRow dataCell(Object o) {
        return this.dataCell(o, null);
    }

    /**
     * 添加多个数据单元格
     * @param iterable 迭代器
     * @param consumer 一个迭代产生多个单元格消费
     * @param <U>      迭代内容类型
     * @return {@link DslRow}
     */
    public <U> DslRow dataCells(Iterable<U> iterable, BiConsumer<U, DslRow> consumer) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(u -> consumer.accept(u, this));
        }

        return this;
    }
}
