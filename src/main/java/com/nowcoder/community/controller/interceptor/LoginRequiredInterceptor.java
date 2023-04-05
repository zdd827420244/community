package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception { //其实是所有请求都拦截，然后从中找出带注解才return true允许访问
        if(handler instanceof HandlerMethod) { //检测拦截的是否是方法  springmvc提供的，如果拦截的是方法的话，这个就会是一个HandlerMethod类型
            HandlerMethod handlerMethod=(HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);//尝试去取这个类型的注解
            if(loginRequired!=null && hostHolder.getUser()==null) {
                response.sendRedirect(request.getContextPath()+"/login");  //用response重定向，这个方法声明返回boolean，不能直接返回页面路径了 其实controller return "redirect:"的底层逻辑就是通过response这么写的
                return false;
            }

        }
        return true;
    }
}
