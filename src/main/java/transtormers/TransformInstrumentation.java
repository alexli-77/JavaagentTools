package transtormers;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JsonUtil;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TransformInstrumentation implements ClassFileTransformer {
    static final Logger logger = LoggerFactory.getLogger(TransformImp.class);
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        Map<String, Object> nestedObject = new HashMap<>();
        String targetMethod = "testList";
        if (className.equals("ListTest")) {
            System.out.println("classname : " + className);
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(className);
                // Get the specific method from the class
                CtMethod ctMethod = cc.getDeclaredMethod("testListA");
                // Use an ExprEditor to edit method calls within the method
                ctMethod.instrument(new ExprEditor() {
                    @Override
                    public void edit(MethodCall m) throws CannotCompileException {
                        if (m.getMethodName().equals("add") ) {
                            nestedObject.put("className", m.getClassName());
                            nestedObject.put("parentFQN", targetMethod);
                            nestedObject.put("methodName", m.getMethodName());
                            nestedObject.put("signature", m.getSignature());
                            System.out.println(m.getClassName() + "." + m.getMethodName() + " line number: " + m.getLineNumber() + " " + m.getSignature());
                            // Insert logging before and after the method call
                            m.replace("{" +
                                    "$_ = $proceed($$);" +
                                    "System.out.println(\"Calling add with: \" + $1); }" +
                                    "java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(" +
                                    "new java.io.FileWriter(\"/Users/files/code/TestProjects/javaagent-test/target/text.txt\", false));" +
                                    "bufferedWriter.write(\"Alex_\" + $sig + \"_\" + $1 + \"_\" +  $_ + \"_\" + $type);bufferedWriter.newLine();bufferedWriter.flush();");
                        }
                    }
                });
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/files/code/TestProjects/javaagent-test/target/text.txt"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strings = line.split("_");
//                        nestedObject.put("paramType",strings[1].substring(0,strings[1].indexOf(";")));
                        nestedObject.put("paramValue",strings[2]);
                        nestedObject.put("result",strings[3]);
                        nestedObject.put("resultType",strings[4]);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return cc.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
