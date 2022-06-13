package cn.wisewe.docx4j.convert.builder;

import java.io.InputStream;

/**
 * aspose 工具类
 * @author xiehai
 * @date 2022/06/13 19:45
 */
public class Asposes {
    private static final InputStream LICENSE = Asposes.class.getResourceAsStream("/license.xml");

    /**
     * 获取license
     * @return {@link InputStream}
     */
    public static InputStream license() {
        return LICENSE;
    }
}
