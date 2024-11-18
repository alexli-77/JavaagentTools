package transtormers;

import javassist.*;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.log4j.PropertyConfigurator;
import org.example.AgentApplication;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
public class TransformImp implements ClassFileTransformer {
    static final Logger logger = Logger.getLogger(TransformImp.class);
    private Set<String> testClassesSet;
    private String config;
    private ClassPool pool;
//    private Set<String> strings;
    public TransformImp(String config, ClassPool pool, Set<String> classListSet) {
        this.config = config;
        this.pool = pool;
        this.testClassesSet = classListSet;
    }
    private final static String source = "{\n"
            + "    long begin = System.currentTimeMillis();\n"
            + "    Object result;\n"
            + "    try {\n"
            + "        result = ($w) %s$agent($$);\n"
            + "    } finally {\n"
            + "        long end = System.currentTimeMillis();\n"
            + "        System.out.println(\"%s方法执行时间为: \" + (end - begin) + \"ms\");"
            + "    }\n"
            + "    return ($r) result;"
            + "}\n";
    private static String fileName = "/Users/files/code/TestProjects/javaagent-test/file.txt";

    /***
     *
     * @param loader                the defining loader of the class to be transformed,
     *                              may be {@code null} if the bootstrap loader
     * @param className             the name of the class in the internal form of fully
     *                              qualified class and interface names as defined in
     *                              <i>The Java Virtual Machine Specification</i>.
     *                              For example, <code>"java/util/List"</code>.
     * @param classBeingRedefined   if this is triggered by a redefine or retransform,
     *                              the class being redefined or retransformed;
     *                              if this is a class load, {@code null}
     * @param protectionDomain      the protection domain of the class being defined or redefined
     * @param classfileBuffer       the input byte buffer in class file format - must not be modified
     *
     * @return
     * @throws IllegalClassFormatException
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            List<String> strings = new ArrayList<>();
            String classNameTrans = className.replaceAll("/",".");
            if (!testClassesSet.contains(classNameTrans)) {
//                logger.error("test methods is not exist : " + classNameTrans);
                return null;
            }
//            logger.debug("classNameTrans is here : 1" );
            CtClass cc = pool.get(classNameTrans);
            if (cc == null) {
//                logger.error("Class pool is failed : " + classNameTrans);
                return null;
            }
//            logger.debug("classNameTrans is here : 2" );
            // Get the specific method from the class
            CtMethod[] ctMethods = new CtMethod[0];
            try {
                ctMethods = cc.getDeclaredMethods();
            } catch (Exception e) {
                logger.error("ctMethods has no declaredMethods : " + classNameTrans);
                e.printStackTrace();
            }

            if (ctMethods == null || ctMethods.length == 0) {
                logger.error("The test class has no method: " + classNameTrans);
                return null;
            }
//            logger.debug("classNameTrans is here : 3" );

            for (CtMethod ctMethod : ctMethods) {
                String ctClassName = ctMethod.getDeclaringClass().getName();
                String ctMethodName = ctMethod.getName();
                // Use an ExprEditor to edit method calls within the method
                ctMethod.instrument(new ExprEditor() {
                    @Override
                    public void edit(MethodCall m) throws CannotCompileException {
                        try {
                            CtMethod ctMethod1 = m.getMethod();
                            ctMethod1.instrument(new ExprEditor() {
                                public void edit(MethodCall m1) throws CannotCompileException {
                                    /***
                                     * the arrayList we want is invoked in the client methods not in the test methods
                                     */
                                    if (!ctMethod1.getDeclaringClass().getName().equals("java.util.ArrayList") && m1.getClassName().equals("java.util.ArrayList")) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("[test class] : ")
                                                .append(ctClassName)
                                                .append(" [test Method] : ")
                                                .append(ctMethodName)
                                                .append(" [Clent class] : ")
                                                .append(ctMethod1.getDeclaringClass().getName())
                                                .append(" [ArrayList Method] : ")
                                                .append(m1.getMethodName())
                                                .append(" [MethodCall line number]: ")
                                                .append(m1.getLineNumber())
                                                .append(" [in] ")
                                                .append(ctMethod1.getName());
                                        logger.error(stringBuilder.toString());
                                        strings.add(stringBuilder.toString());
                                    }
                                    // Insert logging before and after the method call
                                }
                            });
                        } catch (NotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            if (!strings.isEmpty()) {
                logger.error("ArraysList invokes : " + strings.size() + " times in " + classNameTrans);
            } else {
                logger.error("0 invocation of ArrayList");
            }

            try {
                File file = new File("/Users/files/code/TestProjects/javaagent-test/target/json.json");
                if (file.exists() && file.length() != 0) {
                    if (!file.delete()) {
                        throw new IOException("Delete file failure,path:" + file.getAbsolutePath());
                    }
                }
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/files/code/TestProjects/javaagent-test/target/json.json",true));
                for (String s : strings) {
                    bufferedWriter.write(s);
                    bufferedWriter.write('\n');
                }
                bufferedWriter.flush();
            } catch (IOException e) {
                logger.error("IOException is here : " + className);
                throw new RuntimeException(e);
            }
            return cc.toBytecode();
        } catch (NotFoundException e) {
            logger.error("NotFoundException is here : " + className);
            return null;
        } catch (CannotCompileException | IOException e) {
            logger.error("CannotCompileException is here : " + className);
            throw new RuntimeException(e);
        }
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
    public static synchronized void writeInvocationCountToFile() {
        try {
            FileWriter objectFileWriter = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(objectFileWriter);
            writer.write("Hi i'm writer\"MethodName: \"");
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CtMethod newMethod(CtMethod oldMethod) throws CannotCompileException, NotFoundException {
        CtMethod copy = CtNewMethod.copy(oldMethod, oldMethod.getDeclaringClass(), null);
        copy.setName(oldMethod.getName() + "$agent");
        oldMethod.getDeclaringClass().addMethod(copy);
        oldMethod.setBody(String.format(source, oldMethod.getName(), oldMethod.getName()));
        return copy;
    }
}
