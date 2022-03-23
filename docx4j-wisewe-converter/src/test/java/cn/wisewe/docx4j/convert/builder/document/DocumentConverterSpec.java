package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.FileUtil;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * {@link DocumentConverter}单元测试
 * @author xiehai
 * @date 2022/03/22 11:30
 */
public class DocumentConverterSpec {
    @Test
    public void doc() throws FileNotFoundException {
        DocumentConverter.create()
            .input(new FileInputStream(FileUtil.brotherPath(this.getClass(), "2003.doc")))
            .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "2003.pdf")))
            .convert(DocumentConvertType.PDF);
    }

    @Test
    public void docx() throws FileNotFoundException {
        DocumentConverter.create()
            .input(new FileInputStream(FileUtil.brotherPath(this.getClass(), "2007.docx")))
            .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(), "2007.pdf")))
            .convert(DocumentConvertType.PDF);
    }
}
