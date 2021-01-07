package cn.wisewe.docx4j.output.builder.portable;

import cn.wisewe.docx4j.output.utils.StringConverterUtil;
import com.itextpdf.text.pdf.PdfPCell;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.PackagePrivate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
public class DslRow {
    @Getter
    @PackagePrivate
    List<PdfPCell> cells;

    DslRow() {
        this.cells = new ArrayList<>();
    }

    /**
     * 表格添加单元格
     * @param consumer 单元格消费
     * @return {@link DslRow}
     */
    public DslRow cell(Consumer<DslCell> consumer) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        consumer.accept(new DslCell(cell));

        // 单元格顺序缓存
        this.cells.add(cell);

        return this;
    }

    /**
     * 表头单元格
     * @param o        任意对象
     * @param consumer 单元格追加消费
     * @return {@link DslRow}
     */
    public DslRow headCell(Object o, Consumer<DslCell> consumer) {
        return
            this.cell(c -> {
                c.paragraph(p -> p.phrase(StringConverterUtil.convert(o), Fonts.BOLD_NORMAL.font()));
                if (Objects.nonNull(consumer)) {
                    consumer.accept(c);
                }
            });
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
    public DslRow headCell(Object o) {
        // 表头加粗
        return this.headCell(o, null);
    }

    /**
     * 添加表头单元格
     * @param supplier 表头单元格提供
     * @return {@link DslRow}
     */
    public DslRow headCell(Supplier<?> supplier) {
        return this.headCell(supplier.get());
    }

    /**
     * 添加多个表头单元格
     * @param objects 表头单元对象
     * @return {@link DslRow}
     */
    public DslRow headCells(Object... objects) {
        if (Objects.nonNull(objects) && objects.length > 0) {
            for (Object object : objects) {
                this.headCell(() -> object);
            }
        }

        return this;
    }

    /**
     * 数据单元格
     * @param o        文本
     * @param consumer 单元格追加消费
     * @return {@link DslRow}
     */
    public DslRow dataCell(Object o, Consumer<DslCell> consumer) {
        return
            this.cell(c -> {
                c.textParagraph(StringConverterUtil.convert(o));
                if (Objects.nonNull(consumer)) {
                    consumer.accept(c);
                }
            });
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
    public DslRow dataCell(Object o) {
        return this.dataCell(o, null);
    }

    /**
     * 添加数据单元格
     * @param supplier 数据单元内容提供
     * @return {@link DslRow}
     */
    public DslRow dataCell(Supplier<?> supplier) {
        return this.dataCell(StringConverterUtil.convert(supplier.get()));
    }

    /**
     * 添加多个数据单元格
     * @param suppliers 多个数据内容提供
     * @return {@link DslRow}
     */
    public DslRow dataCells(Supplier<?>... suppliers) {
        if (Objects.nonNull(suppliers) && suppliers.length > 0) {
            for (Supplier<?> supplier : suppliers) {
                this.dataCell(supplier);
            }
        }

        return this;
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