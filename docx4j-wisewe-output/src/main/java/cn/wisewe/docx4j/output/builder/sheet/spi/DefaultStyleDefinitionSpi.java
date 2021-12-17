package cn.wisewe.docx4j.output.builder.sheet.spi;

/**
 * 默认样式spi
 * @author xiehai
 * @date 2021/12/17 14:24
 */
public class DefaultStyleDefinitionSpi implements OrderedStyleDefinition {
    @Override
    public int order() {
        return 0;
    }
}
