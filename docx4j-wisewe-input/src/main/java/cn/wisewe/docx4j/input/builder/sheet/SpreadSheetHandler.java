package cn.wisewe.docx4j.input.builder.sheet;

import cn.wisewe.docx4j.input.utils.ReflectUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 表格数据处理器
 * @author xiehai
 * @date 2021/01/08 17:38
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
class SpreadSheetHandler<T> {
    /**
     * 需要跳过记录数
     */
    int skip;
    /**
     * 快速失败标识
     */
    boolean failFast;
    /**
     * 数据校验器
     */
    Validator validator;
    /**
     * 记录Class类型
     */
    Class<T> clazz;
    /**
     * 解析结果
     */
    ImportResult<T> importResult;
    /**
     * 列位置缓存
     */
    Map<CellMeta, Field> fields;
    /**
     * 最大列
     */
    AtomicInteger maxColumn;

    SpreadSheetHandler(Class<T> clazz, int skip, boolean failFast) {
        this.clazz = clazz;
        this.skip = skip;
        this.failFast = failFast;
        // 数据校验
        this.validator =
            Validation.byProvider(HibernateValidator.class)
                .configure()
                // 快速失败
                .failFast(this.failFast)
                .buildValidatorFactory()
                .getValidator();
        this.importResult = new ImportResult<>();
        this.fields = new HashMap<>(8);
        this.maxColumn = new AtomicInteger();
        // 解析注解缓存
        ReflectUtil.getNonStaticFields(this.clazz)
            .forEach(it -> {
                CellMeta cellMeta = ReflectUtil.getAnnotation(it, CellMeta.class);
                if (Objects.nonNull(cellMeta)) {
                    // 更新最大列
                    maxColumn.set(Integer.max(cellMeta.index(), maxColumn.get()));
                    this.fields.put(cellMeta, it);
                }
            });
    }

    /**
     * 处理行数据
     * @param sheet excel表格
     */
    SpreadSheetHandler<T> handle(Sheet sheet) throws IllegalAccessException, InstantiationException {
        for (Row row : sheet) {
            // 行索引
            int index = row.getRowNum();
            // 需要跳过的行
            if (index < this.skip) {
                this.importResult.skip(index);
                continue;
            }
            // 空行
            if (SpreadSheetHandler.isEmptyRow(row, this.maxColumn.get())) {
                this.importResult.addEmpty(index);
                continue;
            }

            this.handleRow(row);
        }

        return this;
    }

    /**
     * 解析结果
     * @return {@link ImportResult}
     */
    ImportResult<T> result() {
        return this.importResult;
    }

    /**
     * 行数据处理
     * @param row {@link Row}
     * @throws IllegalAccessException IllegalAccessException
     * @throws InstantiationException InstantiationException
     */
    private void handleRow(Row row) throws IllegalAccessException, InstantiationException {
        // 数据格式器
        DataFormatter formatter = new DataFormatter();
        T entity = this.clazz.newInstance();
        // 导入原始信息
        Map<Integer, String> origin = new HashMap<>(8);
        // 每行不合法信息
        List<RowMessage> invalidMessages = new ArrayList<>();

        // 按照列顺序读取数据
        for (Map.Entry<CellMeta, Field> it : this.fields.entrySet()) {
            CellMeta meta = it.getKey();
            Cell cell = row.getCell(meta.index(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Field field = it.getValue();
            String text;
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                // 根据单元格日期格式样式 格式化日期字符串
                text = new CellDateFormatter(cell.getCellStyle().getDataFormatString()).format(cell.getDateCellValue());
            } else {
                // 去掉首尾空白
                text = formatter.formatCellValue(cell).trim();
            }
            origin.put(meta.index(), text);
            CellSupportTypes.CellResult result = CellSupportTypes.convert(field.getType(), text, meta);
            if (!result.isOk) {
                invalidMessages.add(new RowMessage(meta.index(), result.message));
                // 快速失败模式
                if (this.failFast) {
                    break;
                }
            }

            try {
                // 设置字段值
                Method setter = ReflectUtil.getFieldSetter(field.getDeclaringClass(), field.getName());
                if (Objects.nonNull(setter)) {
                    setter.invoke(entity, result.value);
                }
            } catch (Exception e) {
                throw new SpreadSheetImportException(e);
            }
        }

        // 非快速失败或者当前验证消息为空
        if (!this.failFast || invalidMessages.isEmpty()) {
            invalidMessages.addAll(
                new ArrayList<>(this.validator.validate(entity)).stream()
                    .map(it -> new RowMessage(index(this.clazz, it), it.getMessage()))
                    .collect(Collectors.toList())
            );
        }

        this.importResult.addRecord(
            row.getRowNum(),
            entity,
            origin,
            invalidMessages.stream()
                .sorted(Comparator.comparingInt(RowMessage::getRow))
                .map(RowMessage::getMessage)
                .collect(Collectors.toList())
        );
    }

    /**
     * excel行是否是空行数据
     * @param row {@link Row}
     * @return true空行 false非空
     */
    static boolean isEmptyRow(Row row, int maxColumn) {
        if (Objects.isNull(row)) {
            return true;
        }

        int count = 0;
        for (Cell cell : row) {
            if (count++ > maxColumn) {
                break;
            }

            // 非空单元格都当作数据单元格
            if (Objects.nonNull(cell) && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }

        return true;
    }

    /**
     * 校验列索引
     * @param clazz 校验实体类型
     * @param a     校验结果
     * @param <T>   实体泛型
     * @return 比较数值
     */
    static <T> int index(Class<T> clazz, ConstraintViolation<T> a) {
        Iterator<Path.Node> ai = a.getPropertyPath().iterator();
        Path.Node ac = ai.next();
        // 仅排序属性上有CellMeta注解的属性
        if (ac.getKind() == ElementKind.PROPERTY) {
            try {
                Field field = clazz.getDeclaredField(ac.getName());
                CellMeta meta = field.getAnnotation(CellMeta.class);
                if (Objects.nonNull(meta)) {
                    return meta.index();
                }
            } catch (Exception ignore) {
            }
        }

        // 其他情况都排在最后
        return Integer.MAX_VALUE;
    }
}
