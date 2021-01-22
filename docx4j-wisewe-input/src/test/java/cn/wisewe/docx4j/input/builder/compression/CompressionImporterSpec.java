package cn.wisewe.docx4j.input.builder.compression;

import cn.wisewe.docx4j.input.FileUtil;
import cn.wisewe.docx4j.input.builder.Person;
import cn.wisewe.docx4j.input.builder.sheet.ImportResult;
import cn.wisewe.docx4j.input.builder.sheet.SpreadSheetImporter;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * {@link CompressionImporter}单元测试
 * @author xiehai
 * @date 2021/01/12 14:41
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class CompressionImporterSpec {
    @Test
    public void empty() throws FileNotFoundException {
        CompressionImporter.create(new FileInputStream(FileUtil.brotherPath(this.getClass(), "/empty.zip")))
            // 目录处理
            .folder(s -> System.out.printf("folder %s\n", s))
            // 文件处理
            .file((s, is) -> System.out.printf("file %s\n", s))
            .resolve();
    }

    @Test
    public void simple() throws FileNotFoundException {
        CompressionImporter.create(new FileInputStream(FileUtil.brotherPath(this.getClass(), "/complex.zip")))
            // 目录处理
            .folder(s -> System.out.printf("folder %s\n", s))
            // 文件处理
            .file((s, is) -> System.out.printf("file %s\n", s))
            .resolve();
    }

    @Test
    public void read() throws FileNotFoundException {
        CompressionImporter.create(new FileInputStream(FileUtil.brotherPath(this.getClass(), "/complex.zip")))
            // 文件处理
            .file((s, is) -> {
                // 解析压缩包中的excel文件
                if (FileUtil.suffix(s).equals("xlsx")) {
                    ImportResult<Person> result =
                        SpreadSheetImporter.create(is, false)
                            .skip(1)
                            .sheet(0)
                            .resolve(Person.class);
                    System.out.println(result.hasInvalid());
                    System.out.println(result.getSkip());
                    System.out.println(result.getInvalidRecords());
                    System.out.println(result.getValidRecords());
                }

                // 继续解压压缩文件
                if (FileUtil.suffix(s).equals("zip")) {
                    CompressionImporter.create(is, false)
                        // 目录处理
                        .folder(ns -> System.out.printf("folder %s\n", ns))
                        // 文件处理
                        .file((ns, nis) -> System.out.printf("file %s\n", ns))
                        .resolve();
                }
            })
            .resolve();
    }
}
