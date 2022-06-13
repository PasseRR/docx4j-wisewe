package cn.wisewe.docx4j.convert.crack;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * crack基类
 * @author xiehai
 * @date 2022/06/13 13:15
 */
class AsposeCrackSpec {
    protected String path(String artifactId, String version, String classifier) {
        String name = String.format("%s-%s", artifactId, version);
        if (Objects.nonNull(classifier)) {
            name += "-" + classifier;
        }
        name += ".jar";
        return Paths.get(base(), artifactId, version, name).toString();
    }

    protected String path(String artifactId, String version) {
        return this.path(artifactId, version, null);
    }

    protected static String base() {
        // 按需修改本地仓库路径
        return "D:\\.m2\\repository\\com\\aspose";
    }
}
