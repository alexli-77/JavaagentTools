import org.junit.Test;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import spoon.support.compiler.SpoonPom;
import util.WukongGenLauncher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class generateNewTests {
    public List<CtType<?>> getTypesToProcess(CtModel ctModel) {
        List<CtType<?>> types = ctModel.getAllTypes().stream().
                filter(ctType -> ctType.isClass() || ctType.isEnum()).
                collect(Collectors.toList());
        List<CtType<?>> typesToProcess = new ArrayList<>(types);
        for (CtType<?> type : types) {
            typesToProcess.addAll(type.getNestedTypes());
        }
        return typesToProcess;
    }
    @Test
    public void getCtypes() {
        WukongGenLauncher panktiGenLauncher = new WukongGenLauncher();
        MavenLauncher launcher = panktiGenLauncher.getMavenLauncher("/Users/files/code/TestProjects/singleTestMethod/");
        SpoonPom projectPom = launcher.getPomFile();

        CtModel model = panktiGenLauncher.buildSpoonModel(launcher);
        System.out.println("POM found at: " + projectPom.getPath());
        System.out.println("Number of Maven modules: " + projectPom.getModel().getModules().size());
        List<CtType<?>> types = getTypesToProcess(model);
        for(CtType<?> t : types) {
            System.out.println(t.toString());
        }

    }
}
