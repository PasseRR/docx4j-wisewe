package cn.wisewe.docx4j.output.builder.sheet;

/**
 * 支持自定义样式的类型
 * @author xiehai
 * @date 2021/01/18 17:51
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public enum CustomStyleType {
    /**
     * 表头单元格
     */
    HEAD,
    /**
     * 拆分表头单元格
     */
    SEPARATED_HEAD,
    /**
     * 数据单元格
     */
    DATA,
    /**
     * 拆分数据单元格
     */
    SEPARATED_DATA
}
