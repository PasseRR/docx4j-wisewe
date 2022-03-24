package cn.wisewe.docx4j.convert.builder.document;

import cn.wisewe.docx4j.convert.builder.Convertable;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;

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
        public BiConsumer<BufferedInputStream, OutputStream> consumer() {
            return DocumentType::convertPdf;
        }
    },
    /**
     * html转换
     */
    HTML {
        @Override
        public BiConsumer<BufferedInputStream, OutputStream> consumer() {
            return DocumentType::convertHtml;
        }
    }
}
