package cn.wisewe.docx4j.convert.office;

/**
 * 支持转换的接口标识
 * @author xiehai
 * @date 2022/03/25 12:45
 */
public interface OfficeConvertHandler {
    /**
     * 文档转换实例
     * @return {@link OfficeDocumentHandler}
     */
    OfficeDocumentHandler getHandler();
}
