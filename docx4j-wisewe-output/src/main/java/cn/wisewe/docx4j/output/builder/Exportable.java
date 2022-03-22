package cn.wisewe.docx4j.output.builder;

import cn.wisewe.docx4j.output.OutputException;
import cn.wisewe.docx4j.output.utils.HttpResponseUtil;
import cn.wisewe.docx4j.output.utils.HttpServletUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 文档可导出标志
 * @author xiehai
 * @date 2022/03/22 09:45
 */
public interface Exportable {
    /**
     * 默认导出文件类型
     * @return {@link OutputFileType}
     */
    OutputFileType defaultFileType();

    /**
     * 将文档输出到给定{@link OutputStream} 并按照关闭标识决定是否关闭流
     * @param outputStream {@link OutputStream}
     * @param closeable    是否关闭流
     */
    void writeTo(OutputStream outputStream, boolean closeable);

    /**
     * 将文档输出到给定{@link OutputStream} 并在完成后关闭流
     * @param outputStream {@link OutputStream}
     */
    default void writeTo(OutputStream outputStream) {
        this.writeTo(outputStream, true);
    }

    /**
     * 输出到{@link HttpServletResponse#getOutputStream()}且不关闭流
     * @param fileName 文件名(不包含文件后缀)
     */
    default void writeToServletResponse(String fileName) {
        this.writeToServletResponse(this.defaultFileType(), fileName);
    }

    /**
     * 给定文件类型的文件输出到{@link HttpServletResponse#getOutputStream()}且不关闭流
     * @param type     文件类型
     * @param fileName 文件名称
     */
    default void writeToServletResponse(OutputFileType type, String fileName) {
        HttpServletResponse response = HttpServletUtil.getCurrentResponse();
        try {
            // http文件名处理
            HttpResponseUtil.handleOutputFileName(type.fullName(fileName), response);
            this.writeTo(response.getOutputStream(), false);
        } catch (IOException e) {
            throw new OutputException(e);
        }
    }
}
