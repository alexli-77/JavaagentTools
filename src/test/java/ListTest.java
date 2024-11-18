import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.example.AgentApplication;
import org.example.ClientFunc;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListTest {

    static final Logger logger = Logger.getLogger(ListTest.class);

    @Test
    public void testListA(){
        ClientFunc clientFunc = new ClientFunc();
        System.out.println(clientFunc.sampleA(1));
        logger.debug("hala");
    }

    @Test
    public void testListB(){
        ClientFunc clientFunc = new ClientFunc();
        System.out.println(clientFunc.sampleB(1));
    }
}
