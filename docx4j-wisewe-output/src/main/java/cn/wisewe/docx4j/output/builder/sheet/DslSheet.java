package cn.wisewe.docx4j.output.builder.sheet;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * {@link Sheet} dsl
 * @author xiehai
 * @date 2020/12/24 11:31
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslSheet {
    final Sheet sheet;
    /**
     * 行号
     */
    AtomicInteger rowNumber;
    /**
     * 行数据缓存
     */
    Map<Integer, DslRow> rows;

    DslSheet(Sheet sheet) {
        this.sheet = sheet;
        this.rowNumber = new AtomicInteger();
        this.rows = new HashedMap<>(16);
    }

    /**
     * 表格冻结行列
     * @param column 冻结列数
     * @param row    冻结行数
     * @return {@link DslSheet}
     */
    public DslSheet freeze(int column, int row) {
        this.sheet.createFreezePane(column, row);

        return this;
    }

    /**
     * 默认冻结第一行及第一列
     * @return {@link DslSheet}
     */
    public DslSheet freeze() {
        return this.freeze(1, 1);
    }

    /**
     * 单行添加
     * @param consumer 行消费
     * @return {@link DslSheet}
     */
    public DslSheet row(Consumer<DslRow> consumer) {
        consumer.accept(this.getOrCreateRow(this.rowNumber.getAndIncrement()));
        return this;
    }

    /**
     * 多行数据添加
     * @param iterable 行迭代内容
     * @param consumer 迭代消费
     * @param <T>      迭代内容类型
     * @return {@link DslSheet}
     */
    public <T> DslSheet rows(Iterable<T> iterable, BiConsumer<T, DslRow> consumer) {
        if (Objects.nonNull(iterable)) {
            iterable.forEach(it -> this.row(row -> consumer.accept(it, row)));
        }

        return this;
    }

    /**
     * 自动列宽
     */
    protected void doAutoColumnWidth() {
        try {
            int lastRowNum = this.sheet.getLastRowNum();
            if (lastRowNum <= 0) {
                return;
            }
            int lastCellNum = this.sheet.getRow(0).getLastCellNum();
            if (lastCellNum <= 0) {
                return;
            }
            IntStream.range(0, lastCellNum)
                .forEach(it -> {
                    // 列宽至少宽5个字符
                    int width =
                        Optional.ofNullable(DslCell.COLUMN_WIDTH.get())
                            // 若无数据设置 则赋值为默认宽度
                            .map(m -> m.get(it))
                            .filter(w -> w >= 5)
                            .orElse(5);

                    // 使一列最大宽度为100个字符 并多出2个字符保持美观
                    this.sheet.setColumnWidth(it, (Integer.min(width, 100) + 2) * 256);
                });
        } finally {
            DslCell.removeThreadLocal();
        }
    }

    /**
     * 通过行号获取或者创建行数据
     * @param rowNumber 行号
     * @return {@link DslRow}
     */
    protected DslRow getOrCreateRow(int rowNumber) {
        return
            Optional.ofNullable(this.rows.get(rowNumber))
                .orElseGet(() -> {
                    DslRow row = new DslRow(this.sheet.createRow(rowNumber));
                    this.rows.put(rowNumber, row);

                    return row;
                });
    }
}
