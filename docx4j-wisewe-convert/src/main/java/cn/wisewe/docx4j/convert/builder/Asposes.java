package cn.wisewe.docx4j.convert.builder;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * aspose 工具类
 * @author xiehai
 * @date 2022/06/13 19:45
 */
public class Asposes {
    private static final InputStream LICENSE = Asposes.class.getResourceAsStream("/license.xml");

    /**
     * 尝试加载许可证
     * @param hasLicense 是否已经加载过许可证
     * @param lock       锁
     * @param consumer   针对许可证的消费
     * @throws Exception 异常
     */
    public static void tryLoadLicense(AtomicBoolean hasLicense, Supplier<Class<?>> lock,
                                      LicenseConsumer consumer) throws Exception {
        if (!hasLicense.get()) {
            synchronized (lock.get()) {
                if (!hasLicense.get()) {
                    consumer.accept(LICENSE);
                    // 设置为已加载许可证
                    hasLicense.set(true);
                }
            }
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
