package cn.wisewe.docx4j.convert.builder;

import java.io.OutputStream;
import java.util.function.BiConsumer;

/**
 * 可转换的
 * @param <T> 输入流类型
 * @author xiehai
 * @date 2022/03/23 16:04
 */
public interface Convertable<T> {
    /**
     * 文件转换
     * @return 文件转换方法
     */
    BiConsumer<T, OutputStream> consumer();
}
