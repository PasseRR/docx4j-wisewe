package cn.wisewe.docx4j.convert.builder.portable;

import cn.wisewe.docx4j.convert.FileUtil;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * {@link PortableConverter}单元测试
 * @author xiehai
 * @date 2022/04/17 19:15
 */
public class PortableConverterSpec {
    @Test
    public void pdf2Html() throws FileNotFoundException {
        PortableConverter.create()
            .input(new FileInputStream(FileUtil.brotherPath(this.getClass(), "pdf.pdf")))
            .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "pdf.html")))
            .convert(PortableConvertType.HTML);
    }

    @Test
    public void pdf2Docx() throws FileNotFoundException {
        PortableConverter.create()
            .input(new FileInputStream(FileUtil.brotherPath(this.getClass(), "pdf.pdf")))
            .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "pdf.docx")))
            .convert(PortableConvertType.DOCX);
    }

    @Test
    public void pdf2Doc() throws FileNotFoundException {
        PortableConverter.create()
            .input(new FileInputStream(FileUtil.brotherPath(this.getClass(), "pdf.pdf")))
            .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "pdf.doc")))
            .convert(PortableConvertType.DOC);
    }
}
