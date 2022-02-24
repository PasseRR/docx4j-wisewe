package cn.wisewe.docx4j.output.builder.sheet;

/**
 * 支持自定义样式的类型
 * @author xiehai
 * @date 2021/01/18 17:51
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public enum CustomStyleType implements StyleType {
    /**
     * 表头单元格
     */
    HEAD,
    /**
     * 拆分表头单元格
     */
    @Deprecated
    SEPARATED_HEAD,
    /**
     * 数据单元格
     */
    DATA,
    /**
     * 拆分数据单元格
     */
    @Deprecated
    SEPARATED_DATA;
    /**
     * 扩展样式表头斜线左下到右上
     */
    static final StyleType SEPARATED_HEAD_UP = () -> "SEPARATED_HEAD_UP";
    /**
     * 扩展样式表头斜线左上到右下
     */
    static final StyleType SEPARATED_HEAD_DOWN = () -> "SEPARATED_HEAD_DOWN";
    /**
     * 扩展样式数据单元格斜线左下到右上
     */
    static final StyleType SEPARATED_DATA_UP = () -> "SEPARATED_DATA_UP";
    /**
     * 扩展样式数据单元格斜线左上到右下
     */
    static final StyleType SEPARATED_DATA_DOWN = () -> "SEPARATED_DATA_DOWN";
}

