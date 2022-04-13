package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.FileUtil;
import org.junit.Test;

import java.io.File;

/**
 * {@link SpreadSheetConverter}单元测试
 * @author xiehai
 * @date 2022/03/22 11:30
 */
public class SpreadSheetConverterSpec {
    @Test
    public void xml() {
        SpreadSheetConverter.create()
            .input(new File(FileUtil.brotherPath(this.getClass(), "/xml.xml")))
            .output(new File(FileUtil.brotherPath(this.getClass(), "/xml.html")))
            .convert(SpreadSheetOfficeConvertType.HTML);
    }

    @Test
    public void xls() {
        SpreadSheetConverter.create()
            .input(new File(FileUtil.brotherPath(this.getClass(), "/2003.xls")))
            .output(new File(FileUtil.brotherPath(this.getClass(), "/2003.html")))
            .convert(SpreadSheetOfficeConvertType.HTML);
    }

    @Test
    public void xlsx() {
        SpreadSheetConverter.create()
            .input(new File(FileUtil.brotherPath(this.getClass(), "/2007.xlsx")))
            .output(new File(FileUtil.brotherPath(this.getClass(), "/2007.html")))
            .convert(SpreadSheetOfficeConvertType.HTML);
    }
}
