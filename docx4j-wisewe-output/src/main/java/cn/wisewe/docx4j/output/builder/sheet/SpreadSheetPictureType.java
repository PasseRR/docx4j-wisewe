package cn.wisewe.docx4j.output.builder.sheet;

import cn.wisewe.docx4j.output.utils.FileUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 电子表格支持的图片类型
 * @author xiehai
 * @date 2020/12/29 15:05
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
public enum SpreadSheetPictureType {
    /**
     * emf
     */
    EMF("emf", Workbook.PICTURE_TYPE_EMF),
    WMF("wmf", Workbook.PICTURE_TYPE_WMF),
    PICT("pict", Workbook.PICTURE_TYPE_PICT),
    /**
     * jpeg
     */
    JPEG("jpeg", Workbook.PICTURE_TYPE_JPEG),
    JPG("jpg", Workbook.PICTURE_TYPE_JPEG),
    PNG("png", Workbook.PICTURE_TYPE_PNG),
    DIB("dib", Workbook.PICTURE_TYPE_DIB);
    /**
     * 图片后缀
     */
    String suffix;
    /**
     * 图片对应格式
     */
    int format;

    /**
     * 获取图片文件poi格式类型
     * @param fileName 文件名
     * @return 格式类型
     */
    public static int getFormat(String fileName) {
        return
            Optional.ofNullable(fileName)
                .map(FileUtil::suffix)
                .filter(it -> !it.isEmpty())
                .map(String::toLowerCase)
                .flatMap(it ->
                    Stream.of(SpreadSheetPictureType.values())
                        .filter(type -> Objects.equals(type.suffix, it))
                        .findFirst()
                        .map(SpreadSheetPictureType::getFormat)
                )
                .orElseThrow(() -> new SpreadSheetExportException("spread sheet not support picture file:" + fileName));
    }

    /**
     * 是否支持图片类型
     * @param fileName 文件名
     * @return true/false
     */
    public static boolean isSupport(String fileName) {
        try {
            SpreadSheetPictureType.getFormat(fileName);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
}
