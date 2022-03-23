package cn.wisewe.docx4j.convert.builder;

import java.io.BufferedInputStream;
import java.io.OutputStream;

/**
 * 可转换的
 * @author xiehai
 * @date 2022/03/23 16:04
 */
public interface Convertable {
    /**
     * 文件转换
     * @param inputStream  待转换流
     * @param outputStream 转换后的流
     */
    void convert(BufferedInputStream inputStream, OutputStream outputStream);
}
