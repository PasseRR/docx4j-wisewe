package cn.wisewe.docx4j.convert.office;

/**
 * office转换接口标识
 * @author xiehai
 * @date 2022/03/25 12:48
 */
public interface OfficeConverter<T extends OfficeConvertHandler> {
    /**
     * 文档转换
     * @param type 转换类型
     */
    void convert(T type);
}
