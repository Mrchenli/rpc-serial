package io.mrchenli.logback;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackTest {

    private static Logger logger = LoggerFactory.getLogger(LogbackTest.class);

    @Test
    public void testTrace(){
        logger.trace("hello trace");
    }
    @Test
    public void testDebug(){
        logger.debug("hello debug");
    }
    @Test
    public void testInfo(){
        logger.info("hello info");
    }
    @Test
    public void testWarn(){
        logger.warn("watch out warn");
    }
    @Test
    public void testError(){
        logger.error("dangerous error occurs");
    }

}
