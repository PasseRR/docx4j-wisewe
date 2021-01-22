package cn.wisewe.docx4j.input.builder.sheet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * excel sheet解析结果
 * @author xiehai
 * @date 2021/01/08 17:53
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ImportResult<T> {
    /**
     * 跳过解析记录数
     */
    final List<Integer> skip = new ArrayList<>();
    /**
     * 空行记录数
     */
    final List<Integer> empty = new ArrayList<>();
    /**
     * 校验合法记录数 无序
     */
    final Map<Integer, T> validRecords = new HashMap<>();
    /**
     * 校验非法记录数 列有序
     */
    final Map<Integer, List<String>> invalidRecords = new TreeMap<>();

    ImportResult() {
    }

    /**
     * 添加不合法记录
     * @param index    索引
     * @param messages 不合法原因
     */
    public void addInvalidRecord(int index, List<String> messages) {
        this.invalidRecords.merge(index, messages, (o, n) -> {
            o.addAll(n);
            return o;
        });
    }

    /**
     * 遍历有效的数据 遍历删除
     * @param consumer 合法元素移除标识
     * @return {@link ImportResult <T>}
     */
    public ImportResult<T> remove(BiConsumer<T, List<String>> consumer) {
        this.validRecords.entrySet()
            .removeIf(it -> {
                List<String> messages = new ArrayList<>();
                consumer.accept(it.getValue(), messages);

                return
                    Optional.of(messages)
                        // 若存在错误信息
                        .filter(m -> !m.isEmpty())
                        // 则加入错误信息列表
                        .map(m -> {
                            this.addInvalidRecord(it.getKey(), m);
                            return true;
                        })
                        // 合法数据
                        .orElse(false);
            });

        return this;
    }

    /**
     * 移除重复数据
     * @param function 重复标识
     * @param <R>      重复标识类型
     * @return {@link ImportResult <T>}
     */
    public <R> ImportResult<T> removeIfRepeated(Function<T, R> function, String message) {
        Set<R> set = new HashSet<>();
        return
            this.remove((t, messages) -> {
                if (!set.add(function.apply(t))) {
                    messages.add(message);
                }
            });
    }

    /**
     * 满足给定的条件执行过程
     * @param predicate 任意条件
     * @param consumer  合法记录消费
     * @return {@link ImportSummary}
     */
    public ImportSummary onAny(Predicate<ImportResult<T>> predicate, Consumer<List<T>> consumer) {
        if (predicate.test(this)) {
            consumer.accept(new ArrayList<>(this.validRecords.values()));
        }

        return this.getSummary();
    }

    /**
     * 当且仅当不存在错误数据且存在有效数据时执行
     * @param consumer 合法记录消费
     * @return {@link ImportSummary}
     */
    public ImportSummary onAllValid(Consumer<List<T>> consumer) {
        // 当且仅当不存在错误数据且存在有效数据时执行
        return this.onAny(t -> !t.hasInvalid() && t.hasValid(), consumer);
    }

    /**
     * 当且仅当存在有效数据时执行
     * @param consumer 合法记录消费
     * @return {@link ImportSummary}
     */
    public ImportSummary onValid(Consumer<List<T>> consumer) {
        // 仅当存在有效数据时执行
        return this.onAny(ImportResult::hasValid, consumer);
    }

    /**
     * 是否存在校验不通过的数据
     * @return true/false
     */
    public boolean hasInvalid() {
        return !this.invalidRecords.isEmpty();
    }

    /**
     * 是否存在有效数据 可能是个空excel
     * @return true/false
     */
    public boolean hasValid() {
        return !this.validRecords.isEmpty();
    }

    /**
     * 汇总信息
     * @return {@link ImportSummary}
     */
    public ImportSummary getSummary() {
        return new ImportSummary(this);
    }

    /**
     * 添加跳过行
     * @param index 索引
     */
    void skip(int index) {
        this.skip.add(index);
    }

    /**
     * 条件空行
     * @param index 索引
     */
    void addEmpty(int index) {
        this.empty.add(index);
    }

    /**
     * 添加合法记录
     * @param index 索引
     * @param t     记录值
     */
    void addValidRecord(int index, T t) {
        this.validRecords.put(index, t);
    }

    /**
     * 添加记录
     * @param index    行索引
     * @param t        实体
     * @param messages 非法信息
     */
    void addRecord(int index, T t, List<String> messages) {
        if (messages.isEmpty()) {
            this.addValidRecord(index, t);
        } else {
            this.addInvalidRecord(index, messages);
        }
    }
}
