package cn.wisewe.docx4j.input.builder;

import cn.wisewe.docx4j.input.builder.sheet.CellMeta;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 测试数据
 * @author xiehai
 * @date 2021/01/11 15:52
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@Data
public class Person {
    @CellMeta(name = "姓名", index = 0)
    @Size(min = 1, max = 8, message = "姓名长度需要在{min}到{max}之间")
    String name;
    @Range(min = 1, max = 200, message = "年龄需要在{min}到{max}之间")
    @CellMeta(name = "年龄", index = 1, message = "应该为整数")
    Integer age;
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "[男|女]", message = "性别只能为男或女")
    @CellMeta(name = "性别", index = 2)
    String sex;
    @CellMeta(name = "收入", index = 3, message = "应该为小数")
    BigDecimal income;
    @CellMeta(name = "出生日期", index = 4, message = "应该为yyyy-MM-dd格式")
    LocalDate birthDate;
    @CellMeta(name = "登录时间", index = 5, message = "应该为yyyy-MM-dd HH:mm:ss格式")
    LocalDateTime loginDatetime;
}
