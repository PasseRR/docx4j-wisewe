package cn.wisewe.docx4j.convert.crack;

import cn.wisewe.docx4j.convert.FileUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.junit.Test;

/**
 * pdf crack
 * @author xiehai
 * @date 2022/06/13 19:32
 */
public class PdfSpec extends AsposeCrackSpec {
    @Test
    public void crack() throws Exception {
        //这一步是完整的jar包路径,选择自己解压的jar目录
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(super.path("aspose-pdf", "21.11", "jdk17"));
        //获取指定的class文件对象
        CtClass zzZJJClass = ClassPool.getDefault().getCtClass("com.aspose.pdf.l9f");
        //从class对象中解析获取所有方法
        CtMethod[] methodA = zzZJJClass.getDeclaredMethods();
        for (CtMethod ctMethod : methodA) {
            //获取方法获取参数类型
            CtClass[] ps = ctMethod.getParameterTypes();
            //筛选同名方法，入参是Document
            if (ps.length == 1 && ctMethod.getName().equals("lI") && ps[0].getName().equals("java.io.InputStream")) {
                //替换指定方法的方法体
                ctMethod.setBody(
                    "{this.l0if = com.aspose.pdf.l10if.lf;com.aspose.pdf.internal.imaging.internal.p71.Helper.help1();lI(this);}");
            }
        }
        //这一步就是将破译完的代码放在桌面上
        zzZJJClass.writeFile(FileUtil.classPath(PdfSpec.class));
    }
}
