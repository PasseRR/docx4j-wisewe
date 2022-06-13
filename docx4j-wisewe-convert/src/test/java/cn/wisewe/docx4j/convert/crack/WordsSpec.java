package cn.wisewe.docx4j.convert.crack;

import cn.wisewe.docx4j.convert.FileUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.junit.Test;

/**
 * words crack
 * @author xiehai
 * @date 2022/06/13 13:10
 */
public class WordsSpec extends AsposeCrackSpec {
    /**
     * https://juejin.cn/post/7034387646168186894
     * @throws Exception 任意异常
     */
    @Test
    public void crack() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(super.path("aspose-words", "21.11", "jdk17"));
        //获取指定的class文件对象
        CtClass zzZJJClass = pool.getCtClass("com.aspose.words.zzXDb");
        //从class对象中解析获取指定的方法
        CtMethod[] methodA = zzZJJClass.getDeclaredMethods("zzY0J");
        //遍历重载的方法
        for (CtMethod ctMethod : methodA) {
            //替换指定方法的方法体
            ctMethod.setBody(
                "{this.zzZ3l = new java.util.Date(Long.MAX_VALUE);this.zzWSL = com.aspose.words.zzYeQ.zzXgr;zzWiV = this;}");
        }
        //这一步就是将破译完的代码放在桌面上
        zzZJJClass.writeFile(FileUtil.classPath(WordsSpec.class));

        //获取指定的class文件对象
        CtClass zzZJJClassB = pool.getCtClass("com.aspose.words.zzYKk");
        zzZJJClassB.getDeclaredMethod("zzWy3").setBody("{return 256;}");
        //这一步就是将破译完的代码放在桌面上
        zzZJJClassB.writeFile(FileUtil.classPath(WordsSpec.class));
        // 删除META-INF下的RSA和SF文件
    }
}
