package cn.wisewe.docx4j.input.builder.sheet;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 行错误信息及原始信息
 * @author xiehai
 * @date 2021/05/06 09:46
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@EqualsAndHashCode(callSuper = true)
public class RowDetail extends RowMessage {
    /**
     * 原始数据 <列索引, 列原始值>
     */
    Map<Integer, String> data;

    /**
     * 从0开始列单元格值
     * @param to 索引闭区间
     * @return 单元格值列表
     */
    public List<String> cellValues(int to) {
        return this.cellValues(0, to);
    }

    /**
     * 从from开始to截止单元格值
     * @param from 索引开区间
     * @param to   索引闭区间
     * @return 单元格值列表
     */
    public List<String> cellValues(int from, int to) {
        return IntStream.rangeClosed(from, to).mapToObj(this.data::get).collect(Collectors.toList());
    }
}
