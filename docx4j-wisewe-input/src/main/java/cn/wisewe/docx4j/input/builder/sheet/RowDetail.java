package cn.wisewe.docx4j.input.builder.sheet;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

/**
 * 行错误信息及原始信息
 * @author xiehai
 * @date 2021/05/06 09:46
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@EqualsAndHashCode(callSuper = true)
public class RowDetail<T> extends RowMessage {
    T data;
}
