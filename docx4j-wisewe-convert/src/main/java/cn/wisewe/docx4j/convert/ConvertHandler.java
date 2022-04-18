package cn.wisewe.docx4j.convert;

import java.io.BufferedInputStream;
import java.io.OutputStream;

/**
 * 处理转换过程
 * @author xiehai
 * @date 2022/04/17 18:41
 */
public interface ConvertHandler {
    /**
     * 文件转换
     * @param inputStream  文件输入流
     * @param outputStream 文件输出流
     */
    void handle(BufferedInputStream inputStream, OutputStream outputStream);
}
