package cn.wisewe.docx4j.convert.builder;

import cn.wisewe.aspose.Asposes;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * aspose 工具类
 * @author xiehai
 * @date 2022/06/13 19:45
 */
public class Licenses {

    /**
     * 尝试加载许可证
     * @param hasLicensed 是否已经加载过许可证
     * @param consumer    针对许可证的消费
     * @throws Exception 异常
     */
    public static void tryLoadLicense(AtomicBoolean hasLicensed, Asposes.LicenseConsumer consumer) throws Exception {
        // 在高并发下 允许重复加载许可
        if (!hasLicensed.get()) {
            Asposes.license(consumer);
            // 设置为已加载许可证
            hasLicensed.set(true);
        }
    }
}
