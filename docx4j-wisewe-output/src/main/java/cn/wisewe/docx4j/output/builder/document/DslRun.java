package cn.wisewe.docx4j.output.builder.document;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * 文本{@link XWPFRun} dsl
 * @author xiehai
 * @date 2020/12/25 17:13
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DslRun {
    XWPFRun run;

    DslRun(XWPFRun run) {
        this.run = run;
    }

    /**
     * 文本更多设置
     * @param consumer 文本消费
     * @return {@link DslRun}
     */
    public DslRun more(Consumer<XWPFRun> consumer) {
        consumer.accept(this.run);
        return this;
    }

    /**
     * 快速设置文本
     * @param text 文字
     * @return {@link DslRun}
     */
    public DslRun text(String text) {
        return this.more(r -> r.setText(text));
    }

    /**
     * 添加图片
     * @param file   图片文件
     * @param width  图片宽度
     * @param height 图片高度
     * @return {@link DslRun}
     */
    public DslRun picture(File file, int width, int height) {
        try {
            this.run.addPicture(
                new FileInputStream(file),
                DocumentPictureType.getFormat(file.getName()),
                file.getName(),
                Units.toEMU(width),
                Units.toEMU(height)
            );
        } catch (InvalidFormatException | IOException e) {
            throw new DocumentExportException(e);
        }

        return this;
    }
}
