package cn.wisewe.docx4j.output.builder.compression;

import cn.wisewe.docx4j.output.OutputConstants;
import cn.wisewe.docx4j.output.builder.Person;
import cn.wisewe.docx4j.output.builder.SpecDataFactory;
import cn.wisewe.docx4j.output.builder.document.DocumentExporter;
import cn.wisewe.docx4j.output.builder.document.DocumentFileType;
import cn.wisewe.docx4j.output.builder.document.ParagraphStyle;
import cn.wisewe.docx4j.output.builder.portable.DefaultTextWatermarkHandler;
import cn.wisewe.docx4j.output.builder.portable.Fonts;
import cn.wisewe.docx4j.output.builder.portable.PortableExporter;
import cn.wisewe.docx4j.output.builder.portable.PortableFileType;
import cn.wisewe.docx4j.output.builder.sheet.SpreadSheetExporter;
import cn.wisewe.docx4j.output.builder.sheet.SpreadSheetFileType;
import cn.wisewe.docx4j.output.utils.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * {@link CompressionExporter}测试用例
 * @author xiehai
 * @date 2021/01/05 20:03
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class CompressionExporterSpec {
    @Test
    public void empty() throws FileNotFoundException {
        CompressionExporter.create()
            .writeTo(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "empty.zip")));
    }

    @Test
    public void folders() throws FileNotFoundException {
        CompressionExporter.create()
            // 文件目录
            .folder(new File(this.getClass().getResource(OutputConstants.SLASH).getPath()))
            // 自定义目录
            .folder("abc", (fn, b) ->
                b.folders(IntStream.range(1, 6).boxed().collect(Collectors.toList()), t -> fn + t, (n, nfn, t) -> {})
            )
            .folder("cde", (fn, b) ->
                b.folders(IntStream.range(6, 10).boxed().collect(Collectors.toList()), t -> fn + t, (n, nfn, t) -> {})
            )
            .writeTo(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "folders.zip")));
    }

    @Test
    public void files() throws FileNotFoundException {
        CompressionExporter.create()
            // 已有文件
            .file(new File(FileUtil.rootPath(this.getClass(), "/a.jpeg")))
            // 空的word文件
            .file(DocumentFileType.DOCX.fullName("a"), os -> DocumentExporter.create().writeTo(os, false))
            // 空的excel文件
            .file(SpreadSheetFileType.XLSX.fullName("b"), os -> SpreadSheetExporter.create().writeTo(os, false))
            // 空的pdf文件
            .file(PortableFileType.PDF.fullName("c"), os -> PortableExporter.fastCreate().writeTo(os, false))
            .writeTo(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "files.zip")));
    }

    @Test
    public void complex() throws FileNotFoundException {
        // 文件类型示例
        // 将数据按照性别分组 合并处理性别列 模拟sql分组 但不保证列表数据顺序
        List<String> sexList =
            SpecDataFactory.tableData().stream().map(Person::getSex).distinct().collect(Collectors.toList());
        Map<String, List<Person>> groupBySex =
            SpecDataFactory.tableData().stream().collect(Collectors.groupingBy(Person::getSex));
        CompressionExporter.create()
            // 直接添加文件
            .any(new File(FileUtil.rootPath(this.getClass(), "/a.jpeg")))
            .file(new File(FileUtil.rootPath(this.getClass(), "/b.png")))
            // 直接添加目录 test class path root
            .any(new File(this.getClass().getResource(OutputConstants.SLASH).getPath()))
            .folder(new File(this.getClass().getResource(OutputConstants.SLASH + "cn").getPath()))
            // 动态文件夹及文件
            .folders(sexList, it -> it, (u, fn, b) -> {
                // 对应性别的docx文档
                b.folder(fn + DocumentFileType.DOCX.name(), (sfn, nb) ->
                    nb.files(groupBySex.get(u), p -> DocumentFileType.DOCX.fullName(sfn + p.getName()), (p, os) ->
                        DocumentExporter.create()
                            .header("我是页眉")
                            .footer("我是页脚")
                            .headingParagraph(p.getName() + "个人信息", ParagraphStyle.SUB_HEADING)
                            .table(2, 3, t ->
                                t.row(r -> r.headCells("姓名", "年龄", "性别"))
                                    .row(r -> r.dataCells(p::getName, p::getAge, p::getSex))
                            )
                            .writeTo(os, false)
                    )
                );
                // 对应性别的xlsx文档
                b.folder(fn + SpreadSheetFileType.XLSX.name(), (sfn, nb) ->
                    nb.files(groupBySex.get(u), p -> SpreadSheetFileType.XLSX.fullName(sfn + p.getName()), (p, os) ->
                        SpreadSheetExporter.create()
                            .workbook(wb ->
                                wb.sheet(s ->
                                    // 表头行
                                    s.row(r -> r.headCells("姓名", "年龄", "性别"))
                                        // 数据行
                                        .row(r -> r.dataCells(p::getName, p::getAge, p::getSex))
                                )
                            )
                            .writeTo(os, false)
                    )
                );
                // 对应性别的pdf文档
                b.folder(fn + PortableFileType.PDF.name(), (sfn, nb) ->
                    nb.files(groupBySex.get(u), p -> PortableFileType.PDF.fullName(sfn + p.getName()), (p, os) ->
                        PortableExporter.create()
                            .event(new DefaultTextWatermarkHandler(p.getName()))
                            .open()
                            .headingParagraph(p.getName() + "个人信息", Fonts.HEADING_1)
                            .textParagraph(String.format("姓名:%s", p.getName()))
                            .textParagraph(String.format("年龄:%s", p.getAge()))
                            .textParagraph(String.format("性别:%s", p.getSex()))
                            .writeTo(os, false)
                    )
                );
                // 空压缩包
                b.folder(fn + CompressionFileType.ZIP.name(), (sfn, nb) ->
                    nb.files(groupBySex.get(u), p -> CompressionFileType.ZIP.fullName(sfn + p.getName()), (p, os) ->
                        CompressionExporter.create().writeTo(os, false)
                    )
                );
            })
            .writeTo(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "complex.zip")));
    }
}
