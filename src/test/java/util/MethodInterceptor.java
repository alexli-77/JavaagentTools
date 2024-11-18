package util;

import javassist.*;
public class MethodInterceptor implements CtMethod {
    public static void intercept(CtMethod method) throws CannotCompileException {
        method.addLocalVariable("starttime", CtClass.longType);
        method.insertBefore("sta");
        method.insertAfter("end");

    }
}
