package cn.wisewe.docx4j.convert.crack;

import cn.wisewe.docx4j.convert.FileUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.junit.Test;

/**
 * cells crack
 * @author xiehai
 * @date 2022/06/14 09:39
 */
public class CellsSpec extends AsposeCrackSpec {
    @Test
    public void crack() throws Exception {
        //这一步是完整的jar包路径,选择自己解压的jar目录
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(super.path("aspose-cells", "21.11"));
        //获取指定的class文件对象
        CtClass zzZJJClass = pool.getCtClass("com.aspose.cells.License");
        //从class对象中解析获取所有方法
        CtMethod[] methodA = zzZJJClass.getDeclaredMethods();
        for (CtMethod ctMethod : methodA) {
            //获取方法获取参数类型
            CtClass[] ps = ctMethod.getParameterTypes();
            //筛选同名方法，入参是Document
            if (ps.length == 1 && ctMethod.getName().equals("a") && ps[0].getName().equals("org.w3c.dom.Document")) {
                System.out.println("ps[0].getName==" + ps[0].getName());
                //替换指定方法的方法体
                ctMethod.setBody("{a = this;com.aspose.cells.zblc.a();}");
            }
        }
        // 这一步就是将破译完的class文件放在指定位置
        zzZJJClass.writeFile(FileUtil.classPath(CellsSpec.class));
    }
}
