package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * 段落样式用于word文档构建
 * 所有样式来源于<code>styles.xml</code>
 * @author xiehai
 * @date 2020/12/25 13:19
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public enum ParagraphStyle {
    /**
     * 标题一样式
     */
    HEADING_1("1", "标题一"),
    /**
     * 标题二样式
     */
    HEADING_2("2", "标题二"),
    /**
     * 标题三样式
     */
    HEADING_3("3", "标题三"),
    /**
     * 标题五样式
     */
    HEADING_5("5", "标题五"),
    /**
     * 标题七样式
     */
    HEADING_7("7", "标题七"),
    /**
     * 标题九样式
     */
    HEADING_9("9", "标题九"),
    /**
     * 居中副标题
     */
    SUB_HEADING("a4", "副标题");

    /**
     * 样式id
     */
    String id;
    /**
     * 样式名称
     */
    String name;
}
