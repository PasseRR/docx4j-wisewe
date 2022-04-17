package cn.wisewe.docx4j.convert.base;

/**
 * 文件类型转换处理器
 * @author xiehai
 * @date 2022/04/17 18:45
 */
public interface FileHandler {
    /**
     * 获取处理器
     * @return {@link ConvertHandler}
     */
    ConvertHandler getHandler();
}
