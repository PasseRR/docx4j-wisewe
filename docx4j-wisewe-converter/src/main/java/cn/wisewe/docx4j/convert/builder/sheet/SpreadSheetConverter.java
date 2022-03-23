package cn.wisewe.docx4j.convert.builder.sheet;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

/**
 * excel转换器
 * @author xiehai
 * @date 2022/03/22 11:14
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpreadSheetConverter {
    Workbook workbook;

    SpreadSheetConverter(Workbook workbook) {
        this.workbook = workbook;
    }

    public static SpreadSheetConverter create(File file) {
        try {
            return new SpreadSheetConverter(WorkbookFactory.create(file));
        } catch (IOException e) {
            throw new SpreadSheetConvertException(e);
        }
    }

    public void toHtml(File file) {

    }
}
