package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.builder.Person;
import cn.wisewe.docx4j.output.builder.SpecDataFactory;
import cn.wisewe.docx4j.output.utils.FileUtil;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * {@link SpreadSheetBuilder}单元测试
 * @author xiehai
 * @date 2020/12/24 14:12
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class SpreadSheetBuilderSpec {
    @Test
    public void empty() throws FileNotFoundException {
        SpreadSheetBuilder.create()
            .writeTo(new FileOutputStream(FileUtil.brotherPath(SpreadSheetBuilderSpec.class, "empty.xlsx")));
    }

    @Test
    public void simple() throws FileNotFoundException {
        SpreadSheetBuilder.create()
            .workbook(wb ->
                wb.sheet(s ->
                    // 表头行
                    s.row(r -> r.headCells("姓名", "年龄", "性别"))
                        // 数据行
                        .rows(
                            SpecDataFactory.tableData(),
                            (it, row) -> row.dataCells(it::getName, it::getAge, it::getSex)
                        )
                        // 行列冻结
                        .freeze(1, 1)
                )
            )
            .writeTo(new FileOutputStream(FileUtil.brotherPath(SpreadSheetBuilderSpec.class, "simple.xlsx")));
    }

    @Test
    public void dynamicSheet() throws FileNotFoundException {
        SpreadSheetBuilder.create()
            .workbook(wb ->
                // 动态sheet
                wb.sheets(SpecDataFactory.tableData(), it -> it.getName() + "的Sheet", (it, s) ->
                    // 表头行
                    s.row(r -> r.headCells("姓名", "年龄", "性别"))
                        .row(r -> r.dataCells(it::getName, it::getAge, it::getSex))
                )
            )
            .writeTo(new FileOutputStream(FileUtil.brotherPath(SpreadSheetBuilderSpec.class, "dynamic-sheet.xlsx")));
    }

    @Test
    public void mergeHead() throws FileNotFoundException {
        SpreadSheetBuilder.create()
            .workbook(wb ->
                wb.sheet(s ->
                    // 表头行
                    s.row(r -> r.headCell(c -> c.rowspan(2).text("姓名")).headCell(c -> c.colspan(2).text("其他信息")))
                        .row(r -> r.headCells("姓名", "年龄", "性别"))
                        // 数据行
                        .rows(
                            SpecDataFactory.tableData(),
                            (it, row) -> row.dataCells(it::getName, it::getAge, it::getSex)
                        )
                )
            )
            .writeTo(new FileOutputStream(FileUtil.brotherPath(SpreadSheetBuilderSpec.class, "merge-head.xlsx")));
    }

    @Test
    public void mergeData() throws FileNotFoundException {
        // 将数据按照性别分组 合并处理性别列 模拟sql分组 但不保证列表数据顺序
        Map<String, List<Person>> groupBySex =
            SpecDataFactory.tableData().stream().collect(Collectors.groupingBy(Person::getSex));
        SpreadSheetBuilder.fastCreate(wb ->
            wb.sheet(s -> {
                // 表头行
                s.row(r -> r.headCells("姓名", "年龄", "性别"));
                // 按照性别渲染表格
                groupBySex.forEach((key, value) -> {
                    AtomicBoolean merged = new AtomicBoolean();
                    int rowspan = value.size();
                    // 数据行
                    s.rows(value, (t, row) ->
                        row.dataCell(t::getName)
                            .dataCell(t::getAge)
                            .dataCell(c -> {
                                c.text(t::getSex);
                                if (!merged.get()) {
                                    // 只合并第一行
                                    merged.set(Boolean.TRUE);
                                    c.rowspan(rowspan);
                                }
                            })
                    );
                });
            })
        ).writeTo(new FileOutputStream(FileUtil.brotherPath(SpreadSheetBuilderSpec.class, "merge-data.xlsx")));
    }

    @Test
    public void picture() throws FileNotFoundException {
        SpreadSheetBuilder.create()
            .workbook(wb ->
                wb.sheet(s ->
                    // 表头行
                    s.row(r -> r.headCells("姓名", "年龄", "性别", "图片"))
                        // 数据行
                        .rows(
                            SpecDataFactory.tableData(),
                            (it, row) ->
                                row.dataCells(it::getName, it::getAge, it::getSex)
                                    // 这里为了测试 设置为固定宽度和高度
                                    .pictureCell(it.picture(), 20, 20)
                        )
                        // 行列冻结
                        .freeze(1, 1)
                )
            )
            .writeTo(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "picture.xlsx")));
    }
}
