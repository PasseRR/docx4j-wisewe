package cn.wisewe.docx4j.output.builder.document;

import cn.wisewe.docx4j.output.builder.BaseDslRow;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * 表格行{@link XWPFTableRow} dsl
 * @author xiehai
 * @date 2020/12/28 19:41
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslTableRow extends BaseDslRow<DslTableRow, DslTableCell> {
    XWPFTableRow row;
    int rowIndex;
    AtomicInteger cellIndex;

    DslTableRow(XWPFTableRow row, int rowIndex) {
        this.row = row;
        this.rowIndex = rowIndex;
        this.cellIndex = new AtomicInteger();
    }

    /**
     * 表格行更多设置
     * @param consumer 设置方法
     * @return {@link DslTableRow}
     */
    public DslTableRow accept(Consumer<XWPFTableRow> consumer) {
        consumer.accept(this.row);
        return this;
    }

    @Override
    public DslTableRow cell(Object o, Consumer<DslTableCell> consumer) {
        // 单元格索引
        int index = this.cellIndex.getAndIncrement();
        XWPFTableCell tableCell = this.row.getCell(index);
        DslTableCell dslCell = new DslTableCell(tableCell);
        Optional.ofNullable(o).ifPresent(dslCell::text);
        Optional.ofNullable(consumer).ifPresent(it -> it.accept(dslCell));
        // 列合并
        if (dslCell.colspan > 1) {
            tableCell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            IntStream.range(index + 1, index + dslCell.colspan)
                .forEach(it ->
                    this.row.getCell(it)
                        .getCTTc()
                        .addNewTcPr()
                        .addNewHMerge()
                        .setVal(STMerge.CONTINUE)
                );
        }

        // 行合并
        if (dslCell.rowspan > 1) {
            tableCell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            IntStream.range(this.rowIndex + 1, this.rowIndex + dslCell.rowspan)
                .forEach(it ->
                    this.row.getTable()
                        .getRow(it)
                        .getCell(index)
                        .getCTTc()
                        .addNewTcPr()
                        .addNewVMerge()
                        .setVal(STMerge.CONTINUE)
                );
        }

        return this;
    }


    @Override
    public DslTableRow headCell(Object o, Consumer<DslTableCell> consumer) {
        return
            this.cell(null, c -> {
                c.boldText(o);
                Optional.ofNullable(consumer).ifPresent(it -> it.accept(c));
            });
    }

    /**
     * 单元格添加图片
     * @param file   图片文件
     * @param width  宽度
     * @param height 高度
     * @return {@link DslTableRow}
     */
    public DslTableRow pictureCell(File file, int width, int height) {
        return this.cell(c -> c.paragraph(p -> p.run(r -> r.picture(file, width, height))));
    }

    @Override
    public DslTableRow dataCell(Object o, Consumer<DslTableCell> consumer) {
        return this.cell(o, consumer);
    }
}
