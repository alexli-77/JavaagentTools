package org.example;
import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import transtormers.TransformClassloaderImp;
import transtormers.TransformImp;
import transtormers.TransformTraceImp;
import transtormers.WukongTransformImp;
import utils.FileUtil;

public class AgentApplication {
    static final Logger logger = Logger.getLogger(AgentApplication.class);
    static String classListFilePath = "/Users/files/code/github/Wukong/Wukong-extract/src/test/outputs/testClasses";
    private static Instrumentation instrumentation;
//    public static void premain(String agentOps, Instrumentation inst) {
//        System.out.println("=========premain方法执行========");
//        instrumentation = inst;
//        instrumentation.addTransformer(new TransformClassloaderImp());
//    }


    public static void premain(String agentOps, Instrumentation inst) {
        String log4jConfPath = "/Users/files/code/TestProjects/javaagent-test/src/main/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
        logger.debug("=========premain方法执行========");
        FileUtil fileUtil = new FileUtil(classListFilePath);
        Set<String> classList = fileUtil.readFileAsSet();
        final ClassPool pool = ClassPool.getDefault();
        instrumentation = inst;
        instrumentation.addTransformer(new TransformImp(agentOps, pool, classList));
    }

    /**
     * JVM hook to dynamically load javaagent at runtime.
     *
     * The agent class may have an agentmain method for use when the agent is
     * started after VM startup.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String args, Instrumentation inst) throws Exception {
        logger.info("agentmain method invoked with args: {} and inst: {}");
        System.out.println("agentmain method invoked with args: {} and inst: {} " + args + " " + inst);
        instrumentation = inst;
        instrumentation.addTransformer(new WukongTransformImp());
    }

    /**
     * Programmatic hook to dynamically load javaagent at runtime.
     */
    public static void initialize() {
        if (instrumentation == null) {
            MyJavaAgentLoader.loadAgent();
        }
    }



}
