package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.utils.HttpResponseUtil;
import cn.wisewe.docx4j.output.utils.HttpServletUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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
public class SpreadSheetBuilder {
    Workbook workbook;

    private SpreadSheetBuilder(Workbook workbook) {
        this.workbook = workbook;
    }

    /**
     * 自定义{@link Workbook}构造
     * @param supplier 电子表格构造器
     * @return {@link SpreadSheetBuilder}
     */
    public static SpreadSheetBuilder create(Supplier<Workbook> supplier) {
        return new SpreadSheetBuilder(supplier.get());
    }

    /**
     * 创建电子表格
     * @return {@link SpreadSheetBuilder}
     */
    public static SpreadSheetBuilder create() {
        return SpreadSheetBuilder.create(XSSFWorkbook::new);
    }

    /**
     * 通过给定输入流及给定{@link Workbook}构造器
     * @param inputStream 输入流
     * @param function    根据输入流创建电子表格方法
     * @return {@link SpreadSheetBuilder}
     */
    public static SpreadSheetBuilder create(InputStream inputStream, Function<InputStream, Workbook> function) {
        return SpreadSheetBuilder.create(() -> function.apply(inputStream));
    }

    /**
     * 通过给定输入流
     * @param inputStream 给定输入流
     * @return {@link SpreadSheetBuilder}
     */
    public static SpreadSheetBuilder create(InputStream inputStream) {
        return
            SpreadSheetBuilder.create(inputStream, is -> {
                try {
                    return new XSSFWorkbook(is);
                } catch (IOException e) {
                    throw new SpreadSheetException(e.getMessage(), e);
                }
            });
    }

    /**
     * 快速构建
     * @param consumer 电子表格消费者
     * @return {@link SpreadSheetBuilder}
     */
    public static SpreadSheetBuilder fastCreate(Consumer<DslWorkbook> consumer) {
        return SpreadSheetBuilder.create().workbook(consumer);
    }

    /**
     * workbook操作
     * @param consumer workbook消费
     * @return {@link SpreadSheetBuilder}
     */
    public SpreadSheetBuilder workbook(Consumer<DslWorkbook> consumer) {
        consumer.accept(new DslWorkbook(this.workbook));

        return this;
    }

    /**
     * 将电子表格写到servlet输出流并指定文件后缀
     * @param fileName      文件名
     * @param spreadSheetFileType 后缀名
     */
    public void writeToServletResponse(String fileName, SpreadSheetFileType spreadSheetFileType) {
        HttpServletResponse response = HttpServletUtil.getCurrentResponse();
        try {
            // http文件名处理
            HttpResponseUtil.handleOutputFileName(spreadSheetFileType.fullName(fileName), response);

            this.writeTo(response.getOutputStream(), false);
        } catch (IOException e) {
            throw new SpreadSheetException(e.getMessage(), e);
        }
    }

    /**
     * 将电子表格写到servlet输出流
     * @param fileName 电子表格下载文件名名称
     */
    public void writeToServletResponse(String fileName) {
        // 默认后缀xlsx
        this.writeToServletResponse(fileName, SpreadSheetFileType.XLSX);
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
            throw new SpreadSheetException(e.getMessage(), e);
        } finally {
            // 电子表格流关闭
            IOUtils.closeQuietly(this.workbook);
            // 若需要关闭输入流则关闭 如ServletOutputStream则不能关闭
            if (closeable) {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }
}
