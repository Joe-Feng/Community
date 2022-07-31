package com.xcf.community.mapperTest;

import com.xcf.community.CommunityApplication;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;



/**
 * @author Joe
 * @version 1.0.0
 * @ClassName LoggerTest.java
 * @Description
 * @createTime 2022年05月11日 16:25:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class LoggerTest {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void loggerTest(){
        System.out.println(logger.getName());

        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warn log");
        logger.error("error log");
    }

}
