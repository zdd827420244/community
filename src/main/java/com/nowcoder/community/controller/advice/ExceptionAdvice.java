package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class) //这样只扫描带有controller注解的bean  里面也可以写多个注解类
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class}) //表示处理所有异常，也可以在大括号里写多个异常
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常: " + e.getMessage());//这只是一个概括
        for (StackTraceElement element : e.getStackTrace()) {  //把异常非常详细的栈的信息都输出出来，遍历这个栈  每个element记录一条异常信息
            logger.error(element.toString());
        }

        //要判断浏览器请求是主动请求还是异步请求  主动请求返回错误页面没问题，异步请求是要返回json串的
        //通过request判断  这是固定方式，记下来就行
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) { //XMLHttpRequest说明是以xml的形式来访问的，希望返回的是xml  异步返回才会希望返回xml，当然也可以返回json串  普通返回希望返回的是html
            response.setContentType("application/plain;charset=utf-8");//异步请求，要响应一个字符串  写application/json，向浏览器返回一个json字符串，浏览器会自动帮我们转成json对象  plain表示向浏览器返回一个普通字符串，但这个普通字符串是json格式的，浏览器需要人为手动将它转成json对象
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常!"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");//普通请求，重定向到那个页面
        }
    }

}
