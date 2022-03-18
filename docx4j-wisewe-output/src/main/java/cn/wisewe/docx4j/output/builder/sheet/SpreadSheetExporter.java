package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.utils.HttpResponseUtil;
import cn.wisewe.docx4j.output.utils.HttpServletUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 电子表格构造器
 * @author xiehai
 * @date 2020/12/24 10:05
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpreadSheetExporter {
    Workbook workbook;

    private SpreadSheetExporter(Workbook workbook) {
        this.workbook = workbook;
    }

    /**
     * 自定义{@link Workbook}构造
     * @param supplier 电子表格构造器
     * @return {@link SpreadSheetExporter}
     */
    public static SpreadSheetExporter create(Supplier<Workbook> supplier) {
        return new SpreadSheetExporter(supplier.get());
    }

    /**
     * 创建电子表格
     * @return {@link SpreadSheetExporter}
     */
    public static SpreadSheetExporter create() {
        return SpreadSheetExporter.create(XSSFWorkbook::new);
    }

    /**
     * 通过给定输入流及给定{@link Workbook}构造器
     * @param inputStream 输入流
     * @param function    根据输入流创建电子表格方法
     * @return {@link SpreadSheetExporter}
     */
    public static SpreadSheetExporter create(InputStream inputStream, Function<InputStream, Workbook> function) {
        return SpreadSheetExporter.create(() -> function.apply(inputStream));
    }

    /**
     * 通过给定输入流
     * @param inputStream 给定输入流
     * @return {@link SpreadSheetExporter}
     */
    public static SpreadSheetExporter create(InputStream inputStream) {
        return
            SpreadSheetExporter.create(inputStream, is -> {
                try {
                    return new XSSFWorkbook(is);
                } catch (IOException e) {
                    throw new SpreadSheetExportException(e);
                }
            });
    }

    /**
     * 快速构建
     * @param consumer 电子表格消费者
     * @return {@link SpreadSheetExporter}
     */
    public static SpreadSheetExporter fastCreate(Consumer<DslWorkbook> consumer) {
        return SpreadSheetExporter.create().workbook(consumer);
    }

    /**
     * 电子表格配置
     * @param consumer 配置方法
     * @return {@link SpreadSheetExporter}
     */
    public SpreadSheetExporter accept(Consumer<Workbook> consumer) {
        consumer.accept(this.workbook);
        return this;
    }

    /**
     * workbook操作
     * @param consumer workbook消费
     * @return {@link SpreadSheetExporter}
     */
    public SpreadSheetExporter workbook(Consumer<DslWorkbook> consumer) {
        consumer.accept(new DslWorkbook(this.workbook));

        return this;
    }

    /**
     * 自定义样式
     * @param styleType 自定义样式类型
     * @param function  生成样式方法
     * @return {@link SpreadSheetExporter}
     */
    public SpreadSheetExporter customStyle(CustomStyleType styleType, Function<Workbook, CellStyle> function) {
        StylesHolder.addCustom(styleType, function);
        return this;
    }

    /**
     * 将电子表格写到servlet输出流并指定文件后缀
     * @param fileName 文件名
     */
    public void writeToServletResponse(String fileName) {
        HttpServletResponse response = HttpServletUtil.getCurrentResponse();
        try {
            // http文件名处理
            HttpResponseUtil.handleOutputFileName(
                SpreadSheetFileType.getTypeByWorkbook(this.workbook).fullName(fileName),
                response
            );

            this.writeTo(response.getOutputStream(), false);
        } catch (IOException e) {
            throw new SpreadSheetExportException(e);
        }
    }

    /**
     * 将电子表格写到给定输出流并关闭流
     * @param outputStream 输出流
     * @param closeable    是否需要关闭输出流
     */
    public void writeTo(OutputStream outputStream, boolean closeable) {
        this.doWrite(outputStream, closeable);
    }

    /**
     * 将电子表格写到给定输出流并关闭流
     * @param outputStream 输出流
     */
    public void writeTo(OutputStream outputStream) {
        this.writeTo(outputStream, true);
    }

    /**
     * 将电子表格写到输出流
     * @param outputStream 输出流
     * @param closeable    是否需要关闭输出流
     */
    protected void doWrite(OutputStream outputStream, boolean closeable) {
        try {
            // 若是空excel 自动添加一个sheet
            if (this.workbook.getNumberOfSheets() == 0) {
                this.workbook(wb -> wb.sheet(s -> {}));
            }

            this.workbook.write(outputStream);
        } catch (IOException e) {
            throw new SpreadSheetExportException(e);
        } finally {
            // 电子表格流关闭
            IOUtils.closeQuietly(this.workbook);
            // 若需要关闭输入流则关闭 如ServletOutputStream则不能关闭
            if (closeable) {
                IOUtils.closeQuietly(outputStream);
            }
            // 清空自定义样式
            StylesHolder.clean();
        }
    }
}
