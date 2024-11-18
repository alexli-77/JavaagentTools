package transtormers;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class WukongTransformImp implements ClassFileTransformer {
    static final Logger logger = LoggerFactory.getLogger(TransformImp.class);

    private static String fileName = "/Users/files/code/TestProjects/javaagent-test/file.txt";
    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        logger.info("class file transformer invoked for className: {}", className);
        System.out.println("=========WukongTransformImp方法执行========");
        System.out.println("classname： " + className);
        {
            try {
                final ClassPool pool = ClassPool.getDefault();

                String javaClassName = className.replace("/", ".");
                // 拿到字节码
                CtClass ctClass = null;

                ctClass = pool.getCtClass(javaClassName);

                CtClass stringClass = pool.getCtClass("ListTest");

                // 如果这个类是一个接口. 那么我们就不进行代理了
                if (!ctClass.isInterface()) {

                    // 拿到里面的所有方法
                    for (CtMethod method : ctClass.getDeclaredMethods()) {
                        // 如果这个方法不是一个 abstract 方法并且也不上一个 native 方法, 我们才进行代理
                        if (!Modifier.isAbstract(method.getModifiers()) && !Modifier.isNative(method.getModifiers())) {

                            // 拿到方法的唯一标识. 类名称.方法名称(方法参数类型...)
                            String methodName = name(method);
//                    String methodName = method.getName();

                            // 插入 _startTime 到局部变量表
                            method.addLocalVariable("_startTime", CtClass.longType);
                            // 插入 _methodName 到局部变量表, 我们的名称尽量使用 _ 开头, 避免和原来的变量发生同名冲突
                            method.addLocalVariable("_methodName", stringClass);

                            // 直接插入代码块, 这里插入到方法执行前的位置
                            method.insertBefore("_startTime = System.currentTimeMillis();");
                            method.insertBefore("_methodName = \"" + methodName + "\";");

                            // 直接插入代码块, 这里插入到方法执行后的位置
                            method.insertAfter("System.out.println(\"方法 [\" + _methodName + \"] 运行耗时:\" + (System.currentTimeMillis() - _startTime)/1000 + \"ms\");");
                        }
                    }
                }
                // 返回修改后的字节码
                return ctClass.toBytecode();
            } catch (Exception e) {
                System.err.println("字节码增强失败, 当前失败类名字是:" + className + ", 具体失败原因:" + e.getMessage());
            }
            return classfileBuffer;
        }

//        if (className.equals("org.junit.Test.ListTest")) {
//
//            ClassWriter cw = new ClassWriter(0);
//            MethodVisitor mv;
//
//            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, "org.junit.Test.ListTest", null,
//                    "java/lang/Object", null);
//
//            cw.visitSource(null, null);
//
//            {
//                mv = cw.visitMethod(ACC_PUBLIC, "", "()V", null, null);
//                mv.visitCode();
//                Label l0 = new Label();
//                mv.visitLabel(l0);
//                mv.visitLineNumber(3, l0);
//                mv.visitVarInsn(ALOAD, 0);
//                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "", "()V");
//                mv.visitInsn(RETURN);
//                Label l1 = new Label();
//                mv.visitLabel(l1);
//                mv.visitLocalVariable("this", "org.junit.Test.ListTest;", null, l0, l1, 0);
//                mv.visitMaxs(1, 1);
//                mv.visitEnd();
//            }
//            {
//                mv = cw.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
//                mv.visitCode();
//                Label l0 = new Label();
//                mv.visitLabel(l0);
//                mv.visitLineNumber(6, l0);
//                mv.visitLdcInsn("bar");
//                mv.visitInsn(ARETURN);
//                Label l1 = new Label();
//                mv.visitLabel(l1);
//                mv.visitLocalVariable("this", "org.junit.Test.ListTest;", null, l0, l1, 0);
//                mv.visitMaxs(1, 1);
//                mv.visitEnd();
//            }
//            cw.visitEnd();
//
//            return cw.toByteArray();
//        }
//
//        return classfileBuffer;
    }
    public static String name(CtMethod method) {
        // 获取方法所在的实体类的名称
        String className = method.getDeclaringClass().getName();
        StringBuffer joiner = new StringBuffer(className).append("::").append(method.getName()).append("(");

        try {
            // 获取方法的所有参数类型
            CtClass[] parameterTypes = method.getParameterTypes();
            // 拼接内容 类路径::方法名(
            for (int i = 0; i < parameterTypes.length; i++) {
                CtClass parameterType = parameterTypes[i];
                joiner.append(parameterType.getName());
                if (i != parameterTypes.length - 1) {
                    joiner.append(",");
                }
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        // 拼接内容 )
        return joiner.append(")").toString();
    }
}
