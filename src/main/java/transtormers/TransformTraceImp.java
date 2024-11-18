package transtormers;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class TransformTraceImp implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (className.equals("ListTest")) {
            System.out.println("classname : " + className);
            try {
                String javaClassName = className.replace("/", ".");
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(javaClassName);
                CtMethod[] methods = cc.getMethods();
                for (CtMethod method : methods) {
                    method.insertBefore("System.out.println(\"Method " + method.getName() + " called on ArrayList\");");
                }
                return cc.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
