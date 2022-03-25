package cn.wisewe.docx4j.convert.fop;

import cn.wisewe.docx4j.convert.ConvertException;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfiguration;
import org.apache.fop.configuration.DefaultConfigurationBuilder;

import java.io.File;
import java.io.OutputStream;

/**
 * fop工具类
 * @author xiehai
 * @date 2022/03/25 15:07
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class FopUtils {
    private static final FopFactory FOP_FACTORY;
    /**
     * 默认字体名字
     */
    private static final String SIM_SUN = "SimSun";

    static {
        // FOP 字体初始化配置
        try {
            DefaultConfiguration build =
                new DefaultConfigurationBuilder()
                    .build(FopUtils.class.getResourceAsStream("fop.xml"));
            FOP_FACTORY = new FopFactoryBuilder(new File(".").toURI()).setConfiguration(build).build();
        } catch (ConfigurationException e) {
            throw new ConvertException(e);
        }
    }

    /**
     * 默认字体名字
     * @return 字体名字
     */
    public static String defaultFont() {
        return SIM_SUN;
    }

    /**
     * PDF{@link Fop}工厂方法
     * @param outputStream {@link OutputStream}
     * @return {@link Fop}
     */
    public static Fop newFopInstance(OutputStream outputStream) {
        try {
            return FOP_FACTORY.newFop(MimeConstants.MIME_PDF, FOP_FACTORY.newFOUserAgent(), outputStream);
        } catch (FOPException e) {
            throw new ConvertException(e);
        }
    }
}
