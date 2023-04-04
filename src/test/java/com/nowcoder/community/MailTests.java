package com.nowcoder.community;


import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;  //thymeleaf也有一个核心类TemplateEngine，是被Spring容器管理的，所以直接注入进来就行


    @Test
    public void testTextMail(){
        mailClient.sendMail("827420244@qq.com","TEST","Welcome.");
        //如果是MVC框架，在controller中，我们只要返回模板，DS就自动帮我们去调用thymeleaf模板引擎了  这里发邮件不能这样做，我们要主动去调用thymeleaf模板引擎
    }

    @Test
    public void testHtmlMail(){
        //访问模板，需要给模板传一个参
        Context context=new Context();    //参数构造
        context.setVariable("username","sunday");//把要传给模板的变量存到这个对象里就行
        String content = templateEngine.process("/mail/demo", context); //调用模板引擎，来生成这个动态网页  得告知模板文件存在哪，还得把context数据传给他
        System.out.println(content);
        mailClient.sendMail("827420244@qq.com","HTML",content);

    }
}
