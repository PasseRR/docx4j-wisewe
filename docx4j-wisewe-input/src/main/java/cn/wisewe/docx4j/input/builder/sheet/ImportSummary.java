package cn.wisewe.docx4j.input.builder.sheet;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 导入汇总
 * @author xiehai
 * @date 2021/01/12 11:19
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ImportSummary implements Serializable {
    /**
     * 总记录数
     */
    int total;
    /**
     * 有效记录数
     */
    int valid;
    /**
     * 无效记录数
     */
    int invalid;
    /**
     * 非法记录
     */
    final List<RowMessage> invalidRecords = new ArrayList<>();

    <T> ImportSummary(ImportResult<T> result, String separator) {
        this.valid = result.getValidRecords().size();
        this.invalid = result.getInvalidRecordMessage().size();
        this.total = this.valid + this.invalid;
        result.getInvalidRecordMessage()
            .forEach((key, value) ->
                this.invalidRecords.add(
                    new RowMessage(
                        key + 1,
                        // 错误消息拼接
                        Optional.of(value)
                            .filter(it -> it.size() > 1)
                            // 一行数据存在多个错误
                            .map(it ->
                                IntStream.range(0, it.size())
                                    .mapToObj(row -> String.format("%d.%s", row + 1, it.get(row)))
                                    .collect(Collectors.joining(separator))
                            )
                            // 仅存在一个错误
                            .orElseGet(() -> value.get(0))
                    )
                )
            );
    }
}
