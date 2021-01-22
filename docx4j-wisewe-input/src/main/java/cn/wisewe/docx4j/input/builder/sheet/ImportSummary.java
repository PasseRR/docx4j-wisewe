package cn.wisewe.docx4j.input.builder.sheet;

import cn.wisewe.docx4j.input.InputConstants;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

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
public class ImportSummary {
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

    ImportSummary(ImportResult<?> importResult) {
        this.valid = importResult.getValidRecords().size();
        this.invalid = importResult.getInvalidRecords().size();
        this.total = this.valid + this.invalid;
        importResult.getInvalidRecords()
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
                                    .collect(Collectors.joining(InputConstants.SEMICOLON))
                            )
                            // 仅存在一个错误
                            .orElseGet(() -> value.get(0))
                    )
                )
            );
    }
}
