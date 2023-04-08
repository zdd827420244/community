package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect  //表示这是一个方面/切面组件，不是一个普通组件
public class AAspect {

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")  //定义切点，针对bean，要把代码织入具体哪些bean的哪些位置  第一个*是方法的返回类型，是任意类型  后面*分别是所有类所有方法，也就是所有service组件中的所有方法，..表示所有参数
    public void pointcut() {  //方法内部不需要写任何逻辑  //也可以不用*，不用..，这样能筛选特定的方法

    }

    //通知
    @Before("pointcut()")  //在连接点的开始做什么事  里面参数说明是针对哪些连接点，或针对哪个切点是有效的
    public void before() {
        System.out.println("before");
    }

    @After("pointcut()")  ///在连接点的结束做什么事
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("pointcut()") //在连接点返回值后做什么事
    public void afterRetuning() {
        System.out.println("afterRetuning");
    }

    @AfterThrowing("pointcut()")  //在连接点抛出异常后做什么事
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @Around("pointcut()") ////在连接点的前后做什么事
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {  //得有返回值和参数  参数就是连接点，也就是目标植入的那个部位
        System.out.println("around before");
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }

}
