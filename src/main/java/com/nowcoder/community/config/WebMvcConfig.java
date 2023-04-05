package com.nowcoder.community.config;


import com.nowcoder.community.controller.interceptor.AInterceptor;
import com.nowcoder.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //之前配置类一般都是为了引入/声明一个第三方的Bean  但拦截器的逻辑不一样，它首先要求实现一个接口

    @Autowired
    private AInterceptor aInterceptor;//要配置拦截器，先注入进来，再配

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    //实现接口，接口里也有许多default方法，按需实现
    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //利用spring传入的registry对象去注册拦截器
        registry.addInterceptor(aInterceptor)//这样时，拦截器会拦截所有请求   //因为静态资源的访问是localhost:8080/community/css/global.css，跟动态模板一样 动态模板路径不需要加templates，静态资源路径不用加static
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")//如果有些请求不希望拦截，可以加上这个 也就是排除哪些路径，这些路径不用拦截器拦截，比如一些静态资源，没有任何业务逻辑，随便访问，无需拦截
                .addPathPatterns("/register","/login");//如果希望一部分请求被拦截，可以加上这个  表示明确要拦截哪些路径

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }

}
