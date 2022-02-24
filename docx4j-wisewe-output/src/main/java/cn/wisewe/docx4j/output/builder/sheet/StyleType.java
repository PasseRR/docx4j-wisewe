package cn.wisewe.docx4j.output.builder.sheet;

/**
 * 样式类型 用于扩展样式缓存
 * @author xiehai
 * @date 2022/02/24 10:09
 */
@FunctionalInterface
interface StyleType {
    /**
     * 类型名称
     * @return 名称
     */
    String name();
}
