package org.example;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

public class MyJavaAgentLoader {
    static final Logger logger = Logger.getLogger(MyJavaAgentLoader.class);

    private static final String jarFilePath = "/Users/files/code/TestProjects/javaagent-test/target/javaagent-test-1.0-SNAPSHOT-jar-with-dependencies.jar";

    public static void loadAgent() {
        VirtualMachine vm;
        logger.info("dynamically loading javaagent");
        List<VirtualMachineDescriptor> latestList;
        latestList = VirtualMachine.list();
        try {
            vm = hasTargetVm(latestList);
            if (vm != null) {
                vm.loadAgent(jarFilePath, "");
            }
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static VirtualMachine hasTargetVm(List<VirtualMachineDescriptor> listAfter) throws IOException, AttachNotSupportedException, IOException, AttachNotSupportedException {
        for (VirtualMachineDescriptor vmd : listAfter) {
            if (vmd.displayName().endsWith("testList"))
                return VirtualMachine.attach(vmd);
        }
        return null;
    }
}
