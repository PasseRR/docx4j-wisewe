package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.builder.BaseDslRow;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * {@link Row} dsl
 * @author xiehai
 * @date 2020/12/24 13:28
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslRow extends BaseDslRow<DslRow, DslCell> {
    @Getter(AccessLevel.PACKAGE)
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
     * 行设置
     * @param consumer 设置方法
     * @return {@link DslRow}
     */
    public DslRow accept(Consumer<Row> consumer) {
        consumer.accept(this.row);
        return this;
    }

    @Override
    public DslRow cell(Object o, Consumer<DslCell> consumer) {
        DslCell cell = this.getOrCreateCell(this.cellNumber.getAndIncrement());
        Optional.ofNullable(o).ifPresent(cell::text);
        Optional.ofNullable(consumer).ifPresent(it -> it.accept(cell));

        this.cellNumber.getAndAdd(cell.colspan - 1);

        if (cell.colspan > 1 || cell.rowspan > 1) {
            this.row.getSheet().addMergedRegion(cell.getCellRangeAddress());
        }

        return this;
    }


    @Override
    public DslRow headCell(Object o, Consumer<DslCell> consumer) {
        return
            this.cell(o, c -> {
                c.headStyle();
                Optional.ofNullable(consumer).ifPresent(it -> it.accept(c));
            });
    }

    @Override
    public DslRow dataCell(Object o, Consumer<DslCell> consumer) {
        return
            this.cell(o, c -> {
                c.dataStyle();
                Optional.ofNullable(consumer).ifPresent(it -> it.accept(c));
            });
    }

    /**
     * 表头添加红星
     * @param o 表头单元格对象
     * @return {@link DslRow}
     */
    public DslRow headRedAsterCell(Object o) {
        return this.headCell(c -> c.redAster(o));
    }

    /**
     * 添加多个带红星的表头
     * @param objects 多个带红星表头单元格
     * @return {@link DslRow}
     */
    public DslRow headRedAsterCells(Object... objects) {
        if (Objects.nonNull(objects) && objects.length > 0) {
            for (Object object : objects) {
                this.headRedAsterCell(object);
            }
        }

        return this;
    }

    /**
     * 表头单元格拆分 仅支持拆分为二 自行添加空格补齐
     * @param first  第一个字符串
     * @param second 第二个字符串
     * @return {@link DslRow}
     */
    public DslRow diagonalHeadCell(String first, String second, boolean isDiagonalUp) {
        return this.cell(cell -> cell.diagonalHeadStyle(isDiagonalUp).text(first, second));
    }

    /**
     * 默认左上至右下斜线表头单元格
     * @param first  第一部分字符串
     * @param second 第二部分字符串
     * @return {@link DslRow}
     */
    public DslRow diagonalDownHeadCell(String first, String second) {
        return this.diagonalHeadCell(first, second, false);
    }

    /**
     * 右上至左下斜线表头单元格
     * @param first  第一部分字符串
     * @param second 第二部分字符串
     * @return {@link DslRow}
     */
    public DslRow diagonalUpHeadCell(String first, String second) {
        return this.diagonalHeadCell(first, second, true);
    }


    /**
     * 表头单元格拆分 仅支持拆分为二 自行添加空格补齐
     * @param first  第一个字符串
     * @param second 第二个字符串
     * @return {@link DslRow}
     */
    public DslRow diagonalDataCell(String first, String second, boolean isDiagonalUp) {
        return this.cell(cell -> cell.diagonalDataStyle(isDiagonalUp).text(first, second));
    }

    /**
     * 左上至右下数据单元格斜线
     * @param first  第一部分字符串
     * @param second 第二部分字符串
     * @return {@link DslRow}
     */
    public DslRow diagonalDownDataCell(String first, String second) {
        return this.diagonalDataCell(first, second, false);
    }

    /**
     * 右上至左下数据单元格斜线
     * @param first  第一部分字符串
     * @param second 第二部分字符串
     * @return {@link DslRow}
     */
    public DslRow diagonalUpDataCell(String first, String second) {
        return this.diagonalDataCell(first, second, true);
    }

    /**
     * 图片单元格
     * @param file   文件
     * @param width  图片宽度
     * @param height 图片高度
     * @return {@link DslRow}
     */
    @Override
    public DslRow pictureCell(File file, int width, int height) {
        return this.cell(c -> c.picture(file, width, height));
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
