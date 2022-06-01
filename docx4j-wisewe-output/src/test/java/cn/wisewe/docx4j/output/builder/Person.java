package cn.wisewe.docx4j.output.builder;

import cn.wisewe.docx4j.output.utils.FileUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.File;

/**
 * 用于测试excel导出的实体
 * @author xiehai
 * @date 2020/12/24 20:56
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Person {
    String name;
    Integer age;
    String sex;
    String picture;

    public File picture() {
        return new File(FileUtil.rootPath(this.getClass(), this.picture));
    }
}
