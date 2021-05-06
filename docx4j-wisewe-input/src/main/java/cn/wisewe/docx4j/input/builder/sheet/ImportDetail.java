package cn.wisewe.docx4j.input.builder.sheet;

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
 * 导入结果详情
 * @author xiehai
 * @date 2021/05/06 09:45
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ImportDetail<T> {
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
     * 错误数据行信息
     */
    final List<RowDetail<T>> invalidRecords = new ArrayList<>();

    ImportDetail(ImportResult<T> result, String separator) {
        this.valid = result.getValidRecords().size();
        this.invalid = result.getInvalidRecordMessage().size();
        this.total = this.valid + this.invalid;
        // 使用错误消息保证顺序
        result.getInvalidRecordMessage().forEach((key, value) -> {
            RowDetail<T> detail = new RowDetail<>();
            detail.setRow(key);
            detail.setData(result.getInvalidRecords().get(key));

            // 错误消息拼接
            detail.setMessage(
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
            );

            this.invalidRecords.add(detail);
        });
    }
}
