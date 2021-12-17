package cn.wisewe.docx4j.output.builder.sheet.spi;

/**
 * 排序样式定义spi
 * @author xiehai
 * @date 2021/12/17 13:44
 */
public interface OrderedStyleDefinition extends StyleDefinition {
    /**
     * 优先级
     * @return 数字越大优先级越低
     */
    int order();
}
