package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void testLogger(){
        // 打印日志名字
        System.out.println(logger.getName());

        // debug级别的日志
        logger.debug("debug logger");
        // 普通级别的日志
        logger.info("info logger");
        // 警告级别的日志
        logger.warn("warn logger");
        // 错误级别的日志
        logger.error("error logger");
    }

}
