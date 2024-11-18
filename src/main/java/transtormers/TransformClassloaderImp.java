package transtormers;

import javassist.*;
import javassist.bytecode.SourceFileAttribute;
import javassist.bytecode.stackmap.TypeData;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import utils.JsonUtil;

import java.io.*;
import java.util.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class TransformClassloaderImp implements ClassFileTransformer {

    List<Map<String, Object>> nestedList = new ArrayList<>();

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        Map<String, Object> nestedObject = new HashMap<>();
        String targetMethod = "testList";
        if (className.equals("ListTest")) {
            System.out.println("classname : " + className);
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(className);
                // Get the specific method from the class
                CtMethod ctMethod = cc.getDeclaredMethod("testList");
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
                nestedList.add(nestedObject);
                String jsonOutput = JsonUtil.toJsonString(nestedList);
                try {
                    File file = new File("/Users/files/code/TestProjects/javaagent-test/target/json.json");
                    if (file.exists() && file.length() != 0) {
                        if (!file.delete()) {
                            throw new IOException("Delete file failure,path:" + file.getAbsolutePath());
                        }
                    }
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/files/code/TestProjects/javaagent-test/target/json.json",true));
                    bufferedWriter.write(jsonOutput);
                    bufferedWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                cc.debugWriteFile("/Users/files/code/TestProjects/javaagent-test/target/java.txt");
                System.out.println(Arrays.toString(cc.toBytecode()));
                return cc.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Map<String, String> addBlock(Map<String, String> nestedObject, String key, String value) {



        return nestedObject;
    }

}
