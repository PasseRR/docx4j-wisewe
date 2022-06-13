package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.FileUtil;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * {@link SlideConverter} 单元测试
 * @author xiehai
 * @date 2022/03/25 18:08
 */
public class SlideConverterSpec {
    @Test
    public void pptToPdf() throws FileNotFoundException {
        SlideConverter.create()
            .input(new FileInputStream(FileUtil.brotherPath(this.getClass(), "2003.ppt")))
            .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "2003.pdf")))
            .convert(SlideConvertType.PDF);
    }

    @Test
    public void pptToHtml() throws FileNotFoundException {
        SlideConverter.create()
            .input(new FileInputStream(FileUtil.brotherPath(this.getClass(), "2003.ppt")))
            .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "2003.html")))
            .convert(SlideConvertType.HTML);
    }

    @Test
    public void pptxToPdf() throws FileNotFoundException {
        SlideConverter.create()
            .input(new FileInputStream(FileUtil.brotherPath(this.getClass(), "2007.pptx")))
            .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "2007.pdf")))
            .convert(SlideConvertType.PDF);
    }

    @Test
    public void pptxToHtml() throws FileNotFoundException {
        SlideConverter.create()
            .input(new FileInputStream(FileUtil.brotherPath(this.getClass(), "2007.pptx")))
            .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "2007.html")))
            .convert(SlideConvertType.HTML);
    }
}
