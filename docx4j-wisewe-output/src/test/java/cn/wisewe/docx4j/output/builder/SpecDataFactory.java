package cn.wisewe.docx4j.output.builder;

import java.util.Arrays;
import java.util.List;

/**
 * 测试数据构造工厂
 * @author xiehai
 * @date 2020/12/24 20:57
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class SpecDataFactory {
    private SpecDataFactory() {

    }

    /**
     * 表格数据
     * @return {@link List}
     */
    public static List<Person> tableData() {
        return
            Arrays.asList(
                new Person("张三", 26, "女", "/a.jpg"),
                new Person("李四", 50, "男", "/b.png"),
                new Person("王五", 18, "女", "/a.jpg"),
                new Person("赵六", 2, "女", "/b.png"),
                new Person("燕七", 80, "男", "/a.jpeg")
            );
    }
}
