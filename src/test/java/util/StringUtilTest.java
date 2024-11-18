package util;

import org.junit.Test;

public class StringUtilTest {
    @Test
    public void replaceTest1() {
        String oldStr = "org/apache/commons/codec/language/DoubleMetaphone2Test";
        String newStr = "";
        newStr = oldStr.replaceAll("/",".");
        System.out.println(newStr);
    }
    @Test
    public void replaceTest2() {
        String oldStr = "DoubleMetaphone2Test";
        String newStr = "";
        newStr = oldStr.replaceAll("/",".");
        System.out.println(newStr);
    }
}
