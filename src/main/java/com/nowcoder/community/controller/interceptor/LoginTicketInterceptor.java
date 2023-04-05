package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取ticket
        String ticket= CookieUtil.getValue(request,"ticket");
        if(ticket!=null) {
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效
            if(loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())) { //超时时间晚于当前时间
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //这个User 之后模板中要用，或者controller中也可能用，所以我们要暂存这个user
                //让本次请求持有用户，本次请求随时随地都能拿到这个用户。但是如果把这个用户存到容器/工具里，这个容器是个简单变量，服务器是多线程服务多个浏览器的，这样访问可能会出现并发问题
                //要考虑线程隔离，每个线程单独存一份。ThreadLocal工具能实现多个线程隔离存对象，要想办法把这个user存到ThreadLocal中
                //把用户存到当前线程对应的map里，只要这个请求没有处理完，线程就一直没有结束，ThreadLocal中的数据一直都是存在的  只有请求处理完，服务器向浏览器做出响应后，线程才结束
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    //模板引擎要用这个user，所以在模板引擎之前要把这个user存入model
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null && modelAndView!=null) {
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
