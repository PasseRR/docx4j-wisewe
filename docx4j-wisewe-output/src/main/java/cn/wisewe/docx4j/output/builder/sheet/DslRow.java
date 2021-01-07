package cn.wisewe.docx4j.output.builder.sheet;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link Row} dsl
 * @author xiehai
 * @date 2020/12/24 13:28
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslRow {
    Row row;
    /**
     * 列号
     */
    AtomicInteger cellNumber;
    /**
     * 列缓存
     */
    Map<Integer, DslCell> cells;

    DslRow(Row row) {
        this.row = row;
        this.cellNumber = new AtomicInteger();
        this.cells = new HashedMap<>(16);
    }

    /**
     * 添加一个单元格主要方法
     * @param consumer 单元格消费
     * @return {@link DslCell}
     */
    public DslRow cell(Consumer<DslCell> consumer) {
        DslCell cell = this.getOrCreateCell(this.cellNumber.getAndIncrement());
        consumer.accept(cell);
        this.cellNumber.getAndAdd(cell.colspan - 1);

        if (cell.colspan > 1 || cell.rowspan > 1) {
            this.row.getSheet().addMergedRegion(cell.getCellRangeAddress());
        }

        return this;
    }

    /**
     * 添加一个表头单元格
     * @param consumer 单元格消费
     * @return {@link DslRow}
     */
    public DslRow headCell(Consumer<DslCell> consumer) {
        return this.cell(cell -> consumer.accept(cell.defaultHeadStyle()));
    }

    /**
     * 添加一个表头单元格
     * @param o 单元格对象
     * @return {@link DslCell}
     */
    public DslRow headCell(Object o) {
        return this.headCell(cell -> cell.text(o));
    }

    /**
     * 添加一个表头单元格
     * @param supplier 单元格内容提供
     * @return {@link DslRow}
     */
    public DslRow headCell(Supplier<Object> supplier) {
        return this.headCell(supplier.get());
    }

    /**
     * 批量添加表头
     * @param iterable 迭代器
     * @param function 表头字段取值
     * @param <T>      迭代器内容类型
     * @return {@link DslRow}
     */
    public <T> DslRow headCells(Iterable<T> iterable, Function<T, Object> function) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(it -> this.headCell(function.apply(it)));
        }

        return this;
    }

    /**
     * 批量添加表头单元格
     * @param iterable 表头迭代器
     * @return {@link DslRow}
     */
    public DslRow headCells(Iterable<Object> iterable) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(this::headCell);
        }

        return this;
    }

    /**
     * 批量添加表格单元格
     * @param objects 对象数组
     * @return {@link DslRow}
     */
    public DslRow headCells(Object... objects) {
        if (Objects.nonNull(objects) && objects.length > 0) {
            for (Object object : objects) {
                this.headCell(object);
            }
        }

        return this;
    }

    /**
     * 添加一个数据单元格
     * @param consumer 单元格消费
     * @return {@link DslRow}
     */
    public DslRow dataCell(Consumer<DslCell> consumer) {
        return this.cell(cell -> consumer.accept(cell.defaultDataStyle()));
    }

    /**
     * 添加一个数据单元格
     * @param o 单元格内容对象
     * @return {@link DslCell}
     */
    public DslRow dataCell(Object o) {
        return this.dataCell(c -> c.text(o));
    }

    /**
     * 添加一个数据单元格
     * @param supplier 单元格内容提供
     * @return {@link DslRow}
     */
    public DslRow dataCell(Supplier<Object> supplier) {
        return this.dataCell(supplier.get());
    }

    /**
     * 图片单元格
     * @param width  图片宽度
     * @param height 图片高度
     * @param file   图片文件
     * @return {@link DslRow}
     */
    public DslRow pictureCell(File file, int width, int height) {
        return this.cell(c -> c.picture(file, width, height));
    }

    /**
     * 添加多个数据单元格
     * @param suppliers 数据提供数组
     * @return {@link DslRow}
     */
    public DslRow dataCells(Supplier<?>... suppliers) {
        if (Objects.nonNull(suppliers) && suppliers.length > 0) {
            for (Supplier<?> supplier : suppliers) {
                this.dataCell(supplier.get());
            }
        }

        return this;
    }

    /**
     * 根据单元格编号获取或创建一个单元格
     * @param cellNumber 单元格编号
     * @return {@link DslCell}
     */
    protected DslCell getOrCreateCell(int cellNumber) {
        return
            Optional.ofNullable(this.cells.get(cellNumber))
                .orElseGet(() -> {
                    DslCell cell = new DslCell(this.row.createCell(cellNumber));
                    this.cells.put(cellNumber, cell);

                    return cell;
                });
    }
}
