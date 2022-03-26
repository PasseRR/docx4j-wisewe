package cn.wisewe.docx4j.convert.fop;

import cn.wisewe.docx4j.convert.ConvertException;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 字体工具
 * @author xiehai
 * @date 2022/03/26 11:56
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class FontUtils {
    /**
     * 默认字体名字
     */
    private static final String SIM_SUN = "SimSun";
    /**
     * 默认字体
     */
    private static final Font FONT;

    static {
        try {
            FONT = Font.createFont(Font.PLAIN, simSunStream());
        } catch (FontFormatException | IOException e) {
            throw new ConvertException(e);
        }
    }

    /**
     * 默认字体名字
     * @return 字体名字
     */
    public static String defaultFontName() {
        return SIM_SUN;
    }

    /**
     * 默认字体
     * @return {@link Font}
     */
    public static Font defaultFont() {
        return FONT;
    }

    /**
     * 默认字体输入流
     * @return {@link InputStream}
     */
    private static InputStream simSunStream() {
        return FontUtils.class.getResourceAsStream(String.format("%s.ttc", SIM_SUN));
    }
}
