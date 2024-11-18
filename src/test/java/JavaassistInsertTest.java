import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class JavaassistInsertTest {
    public static void main(String[] args) throws Exception {

        // Create an instance of the modified class and invoke the method

        // Obtain the default ClassPool
        ClassPool pool = ClassPool.getDefault();

        // Get the CtClass object for the class containing the method
        CtClass ctClass = pool.get("ListTest");

        // Get the specific method from the class
        CtMethod ctMethod = ctClass.getDeclaredMethod("testListA");
        // Use an ExprEditor to edit method calls within the method
        ctMethod.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("sampleA")) {
                    try {
                        CtMethod ctMethod1 = m.getMethod();
                        ctMethod1.instrument(new ExprEditor() {
                            public void edit(MethodCall m1) throws CannotCompileException {
                                // Insert logging before and after the method call
                                System.out.println("class name : " + m1.getClassName() + " methodname: " + m1.getMethodName() + " line number: " + m1.getLineNumber());

//                                if (m1.getMethodName().equals("add")) {
//                                    System.out.println("class name : " + m1.getClassName() + " methodname: " + m1.getMethodName() + " line number: " + m1.getLineNumber());
//                                    try {
//                                        CtMethod ctMethod2 = m1.getMethod();
//                                        ctMethod2.insertBefore("System.out.println(\"起飞之前准备降落伞\");");
//                                        ctMethod2.insertAfter("System.out.println(\"成功落地。。。。\");");
//                                    } catch (NotFoundException e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                }
                                if (m1.getClassName().contains("ArrayList")) {
                                    System.out.println("class name : " + m1.getClassName() + " methodname: " + m1.getMethodName() + " line number: " + m1.getLineNumber());
                                    try {
                                        CtMethod ctMethod2 = m1.getMethod();
                                        ctMethod2.insertBefore("{ System.out.println(\"起飞之前准备降落伞\"); }");
                                        ctMethod2.insertAfter("{ System.out.println(\"成功落地。。。。\"); }");
                                    } catch (NotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        });
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        // Load the modified class into JVM
        ctClass.toBytecode();
//        ctClass.toClass();
        ctClass.detach();
    }
}
