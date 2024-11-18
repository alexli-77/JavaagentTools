import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import utils.FileUtil;
import utils.JsonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static utils.JsonUtil.convertJsonNode2List;

public class JacksonTest {
    private String signature;
    private String result;
    private String methodName;
    private String className;
    private String resultType;
    private String paramValue;
    public Class<?> parseParamType(String pType) {
        String subType = pType.substring(pType.indexOf("("),pType.indexOf(")")+1);
        if (subType.isEmpty())
            return null;
        switch (subType) {
            case "(Ljava/lang/Object;)":
                return Object.class;
            case "(Z)":
                return boolean.class;
            case "(B)":
                return byte.class;
            case "(C)":
                return char.class;
            case "(D)":
                return double.class;
            case "(F)":
                return float.class;
            case "(I)":
                return int.class;
            case "(J)":
                return long.class;
            case "(S)":
                return short.class;
            default:
                throw new IllegalStateException("Unexpected value: " + subType);
        }
    }

    public void readJsonFile(){
        FileUtil fileUtil = new FileUtil("/Users/files/code/TestProjects/javaagent-test/target/json.json");
        String str = fileUtil.readLine();
        System.out.println(str);
        JsonNode jsonNode = JsonUtil.toObject(str);
        if (jsonNode.isArray()) {
            for (JsonNode jsonNode1 : jsonNode) {
                signature = String.valueOf(jsonNode1.get("signature"));
                Class<?> c = parseParamType(signature);
                System.out.println(c.toString());
                result = String.valueOf(jsonNode1.get("result"));
                methodName = String.valueOf(jsonNode1.get("methodName"));
                className = String.valueOf(jsonNode1.get("className"));
                resultType = String.valueOf(jsonNode1.get("resultType"));
                paramValue = String.valueOf(jsonNode1.get("paramValue"));
            }
        }
    }


}
