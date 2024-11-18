import javassist.ClassPool;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JavaassistDebugTest {
    @Test
    public void methodInterceptTest() throws InterruptedException, IOException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass targetClass = classPool.get();
    }
}
