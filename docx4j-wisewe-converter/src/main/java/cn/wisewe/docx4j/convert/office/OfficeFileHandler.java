package cn.wisewe.docx4j.convert.office;

import cn.wisewe.docx4j.convert.base.FileHandler;

/**
 * 支持转换的接口标识
 * @param <T> 文档类型
 * @author xiehai
 * @date 2022/03/25 12:45
 */
public interface OfficeFileHandler<T> extends FileHandler {
    /**
     * 文档转换实例
     * @return {@link OfficeDocumentHandler}
     */
    @Override
    OfficeDocumentHandler<T> getHandler();
}
