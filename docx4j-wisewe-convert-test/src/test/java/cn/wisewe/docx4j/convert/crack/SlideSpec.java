package cn.wisewe.docx4j.convert.crack;

import cn.wisewe.docx4j.convert.FileUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.junit.Test;

/**
 * slide crack
 * @author xiehai
 * @date 2022/06/14 09:36
 */
public class SlideSpec extends AsposeCrackSpec {
    @Test
    public void crack() throws Exception {
        //这一步是完整的jar包路径,选择自己解压的jar目录
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(super.path("aspose-slides", "21.10", "jdk16"));
        CtClass zzZJJClass = pool.getCtClass("com.aspose.slides.internal.of.public");
        CtMethod[] methodA = zzZJJClass.getDeclaredMethods();
        for (CtMethod ctMethod : methodA) {
            CtClass[] ps = ctMethod.getParameterTypes();
            if (ps.length == 3 && ctMethod.getName().equals("do")) {
                ctMethod.setBody("{}");
            }
        }
        //这一步就是将破译完的代码放在桌面上
        zzZJJClass.writeFile(FileUtil.classPath(SlideSpec.class));
    }
}
