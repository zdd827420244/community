package com.nowcoder.community.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AInterceptor implements HandlerInterceptor {  //拦截器要实现HandlerInterceptor，根据情况进行方法实现  方法有default标志，表现这个方法是个默认实现的方法，但是是空实现，所以我们实现接口时可以实现这个方法也可以不实现，不强制实现所有方法

    private static final Logger logger= LoggerFactory.getLogger(AInterceptor.class);//以当前类作为logger名字
    //在拦截请求的时候，在controller处理请求代码之前执行  相当于在controller之前先处理一下请求，所以参数有request response
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        logger.debug("preHandle:"+handler.toString());
        return HandlerInterceptor.super.preHandle(request, response, handler);
        //注意，这里如果返回false了，就是取消这个请求了，程序就不会往下执行，所以controller不会执行这个请求
        //根据输出结果可知，这个handler代表的是我们所拦截的目标方法  比如我们拦截/login了，handler就代表/login所访问的方法
    }

    //调用完controller之后，模板引擎执行之前执行的 controller之后，主要的请求逻辑已经处理完了，下一步就是去模板引擎，给页面返回要渲染的内容  而这个操作很可能要去处理一下模板引擎，会用到或修改ModelAndView
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        logger.debug("postHandle:"+handler.toString());
    }

    //在程序的最后执行，也就是在模板引擎执行完之后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        logger.debug("afterCompletion:"+handler.toString());
    }

    //代码写完后要对这个拦截器进行相应配置，否则也不知道拦截器应该拦截那些请求，写个配置类WebMvcConfig
}
