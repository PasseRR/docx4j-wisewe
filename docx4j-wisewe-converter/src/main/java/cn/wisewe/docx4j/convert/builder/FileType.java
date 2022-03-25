package cn.wisewe.docx4j.convert.builder;

import cn.wisewe.docx4j.convert.ConvertException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 文件类型枚举
 * @author xiehai
 * @date 2022/03/23 18:28
 */
enum FileType {
    /**
     * 压缩包
     */
    ZIP {
        @Override
        boolean match(byte[] bytes) {
            final int first = 80, second = 75;
            return bytes[0] == first && bytes[1] == second;
        }
    },
    /**
     * 二进制
     */
    COMPOUND {
        @Override
        boolean match(byte[] bytes) {
            final int first = -48, second = -49;
            return bytes[0] == first && bytes[1] == second;
        }
    },
    /**
     * xml
     */
    FLAT_OPC {
        @Override
        boolean match(byte[] bytes) {
            return true;
        }
    };

    abstract boolean match(byte[] bytes);

    public static FileType type(BufferedInputStream bufferedInputStream) {
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
            Arrays.stream(FileType.values())
                .filter(it -> it.match(firstTwoBytes))
                .findFirst()
                .orElseThrow(() -> new ConvertException("unknown file type"));
    }
}
