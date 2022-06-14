package cn.wisewe.docx4j.convert.builder;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * aspose 工具类
 * @author xiehai
 * @date 2022/06/13 19:45
 */
public class Asposes {

    /**
     * 尝试加载许可证
     * @param hasLicensed 是否已经加载过许可证
     * @param consumer    针对许可证的消费
     * @throws Exception 异常
     */
    public static void tryLoadLicense(AtomicBoolean hasLicensed, LicenseConsumer consumer) throws Exception {
        // 在高并发下 允许重复加载许可
        if (!hasLicensed.get()) {
            consumer.accept(Asposes.class.getResourceAsStream("/license.xml"));
            // 设置为已加载许可证
            hasLicensed.set(true);
        }
    }

    /**
     * 许可证消费
     */
    @FunctionalInterface
    public interface LicenseConsumer {
        /**
         * 接受许可证输入流消费
         * @param is {@link InputStream}
         * @throws Exception 异常
         */
        void accept(InputStream is) throws Exception;
    }
}
