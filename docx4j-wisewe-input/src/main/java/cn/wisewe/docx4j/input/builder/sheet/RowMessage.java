package cn.wisewe.docx4j.input.builder.sheet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * 行记录解析结果
 * @author xiehai
 * @date 2021/01/11 10:19
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RowMessage implements Serializable {
    /**
     * 行号
     */
    int row;
    /**
     * 错误消息
     */
    String message;
}
