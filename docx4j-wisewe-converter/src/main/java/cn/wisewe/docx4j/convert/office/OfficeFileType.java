package cn.wisewe.docx4j.convert.office;

import cn.wisewe.docx4j.convert.ConvertException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 文件类型枚举
 * @author xiehai
 * @date 2022/03/23 18:28
 * @see org.docx4j.openpackaging.packages.OpcPackage#load(org.docx4j.events.PackageIdentifier, java.io.InputStream, java.lang.String)
 */
enum OfficeFileType {
    /**
     * 压缩包
     */
    ZIP {
        @Override
        protected boolean match(byte[] bytes) {
            final int first = 80, second = 75;
            return bytes[0] == first && bytes[1] == second;
        }
    },
    /**
     * 二进制
     */
    COMPOUND {
        @Override
        protected boolean match(byte[] bytes) {
            final int first = -48, second = -49;
            return bytes[0] == first && bytes[1] == second;
        }
    },
    /**
     * xml 不能以字节数组区分 必须放在最后
     */
    FLAT_OPC {
        @Override
        protected boolean match(byte[] bytes) {
            return true;
        }
    };

    /**
     * 是否匹配文件类型
     * @param bytes 字节数组
     * @return true/false
     */
    protected abstract boolean match(byte[] bytes);

    public static OfficeFileType type(BufferedInputStream bufferedInputStream) {
        bufferedInputStream.mark(0);
        byte[] firstTwoBytes = new byte[2];
        int read;
        try {
            read = bufferedInputStream.read(firstTwoBytes);
            bufferedInputStream.reset();
        } catch (IOException e) {
            throw new ConvertException(e);
        }

        if (read != firstTwoBytes.length) {
            throw new ConvertException("file byte less than 2 bytes");
        }

        return
            Arrays.stream(OfficeFileType.values())
                .filter(it -> it.match(firstTwoBytes))
                .findFirst()
                .orElseThrow(() -> new ConvertException("unknown file type"));
    }
}
