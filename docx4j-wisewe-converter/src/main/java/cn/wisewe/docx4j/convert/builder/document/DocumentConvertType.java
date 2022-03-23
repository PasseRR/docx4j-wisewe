package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.builder.Convertable;

import java.io.BufferedInputStream;
import java.io.OutputStream;

/**
 * 支持转换类型
 * @author xiehai
 * @date 2022/03/23 16:03
 */
public enum DocumentConvertType implements Convertable {
    /**
     * pdf转换
     */
    PDF {
        @Override
        public void convert(BufferedInputStream inputStream, OutputStream outputStream) {
            DocumentType.convertPdf(inputStream, outputStream);
        }
    }
}
