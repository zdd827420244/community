package com.nowcoder.community.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailClient { //它并不发邮件，它是委托新浪发邮件，所以它相当于客户端
    private static final Logger logger= LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content){
        try {
            MimeMessage message=mailSender.createMimeMessage();  //create的是空的，它只是帮我们创建了一个模板，要往里面填入内容 要用帮助类帮我们构建更详细的内容
            MimeMessageHelper helper=new MimeMessageHelper(message); //把message传进来，helper就能帮忙构建message里面的内容了
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true); //不加第二个参数，默认这个文本只是普通文字  加了后，表明支持html文本，把html转成字符串传进来它也能够识别
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败"+e.getMessage());
        }


    }



}
