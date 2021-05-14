package cn.wisewe.docx4j.input.constants;

import java.time.format.DateTimeFormatter;

/**
 * 日期时间格式
 * @author xiehai
 * @date 2021/05/13 20:14
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public interface DatetimeConstants {
    String XLS_YYYY_MM_DD = "yyyy/M/D";
    String XLS_HH_MM_SS = "H:m:s";
    String XLS_YYYY_MM_DD_HH_MM_SS = String.format("%s %s", XLS_YYYY_MM_DD, XLS_HH_MM_SS);
    /**
     * excel日期格式化
     */
    DateTimeFormatter DTF_XLS_YYYY_MM_DD = DateTimeFormatter.ofPattern(XLS_YYYY_MM_DD);
    /**
     * excel时间格式化
     */
    DateTimeFormatter DTF_XLS_HH_MM_SS = DateTimeFormatter.ofPattern(XLS_HH_MM_SS);
    /**
     * excel日期时间格式化
     */
    DateTimeFormatter DTF_XLS_YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern(XLS_YYYY_MM_DD_HH_MM_SS);
}
