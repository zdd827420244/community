package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //拦截器拦截的就是方法，显然是描述方法的
@Retention(RetentionPolicy.RUNTIME) //注解运行时才有效
public @interface LoginRequired { //标记方法是否需要登录才能访问
    //这个自定义注解起标识的作用，所以里面为空
}

