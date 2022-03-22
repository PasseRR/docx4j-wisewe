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
    public void xls() {
        SpreadSheetConverter.create(new File(FileUtil.brotherPath(this.getClass(), "/2003.xls")))
            .toHtml(new File(FileUtil.brotherPath(this.getClass(), "2003.html")));
    }
    
    @Test
    public void xlsx(){
        SpreadSheetConverter.create(new File(FileUtil.brotherPath(this.getClass(), "/2010.xlsx")))
            .toHtml(new File(FileUtil.brotherPath(this.getClass(), "2010.html")));
    }
}
