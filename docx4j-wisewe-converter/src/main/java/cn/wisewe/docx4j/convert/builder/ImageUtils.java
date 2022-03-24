package cn.wisewe.docx4j.convert.builder;

import java.util.Base64;

/**
 * 图片工具类
 * @author xiehai
 * @date 2022/03/24 13:31
 */
public interface ImageUtils {
    /**
     * base64编码器
     */
    Base64.Encoder ENCODER = Base64.getEncoder();
    
    /**
     * 图片转base64
     * @param bytes 字节数组
     * @return 图片base64字符串
     */
    static String base64(byte[] bytes) {
        return String.format("data:;base64,%s", ENCODER.encodeToString(bytes));
    }
}
