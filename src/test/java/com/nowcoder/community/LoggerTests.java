package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);//传入一个类，这个类名就logger的名字  这样不同的logger在不同的类下，就会有不同的歌区别
    @Test
    public void testLogger(){
        System.out.println(logger.getName());
        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warn log");
        logger.error("error log"); //打印完日志后，就要在配置文件中声明一下要启用什么级别的日志，这样logback才知道要打印什么级别的信息
        //logback其实有自己的配置文件，但是经过springboot整合后，在application.properties中配就行
        //logger默认的是打印到控制台上的(实际上控制台是一直都打印的，即便你声明打印到文件了，控制台也会打印出日志)  但是一关就没了，并且用作应用程序后，也不会去下载idea工具并查看控制台
        //logger打印到文件里的方法，可以在配置文件中声明
    }

}
