package cn.wisewe.docx4j.output;

/**
 * 导出常量
 * @author xiehai
 * @date 2020/12/24 12:56
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public interface OutputConstants {
    /**
     * 空字符串{@value}
     */
    String EMPTY = "";
    /**
     * 空格字符串{@value}
     */
    String SPACE = " ";
    /**
     * 全角空格
     */
    String DOUBLE_BYTE_SPACE = "　";
    /**
     * 点字符串{@value}
     */
    String POINT = ".";
    /**
     * 斜杠{@value}
     */
    String SLASH = "/";
    /**
     * 换行符{@value}
     */
    String BREAK_LINE = "\n";
    /**
     * word页眉页脚当前页码{@value}
     */
    String DOCX_PAGES = "PAGE \\* MERGEFORMAT";
    /**
     * word页眉页脚总页码{@value}
     */
    String DOCX_NUM_PAGES = "NUMPAGES \\* MERGEFORMAT";
}
