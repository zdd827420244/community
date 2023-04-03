package com.nowcoder.community.service;

import com.nowcoder.community.dao.ADao;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")//如果不想单例，想每次getBean都新建一个实例  加个注解@Scope，也就是作用范围，整个容器有一个还是有多个  默认参数singleton，单例
//多例 prototype，这时每次访问Bean，都会生成一个新的实例
public class AService {

    @Autowired
    private ADao aDao;

    public AService(){
        System.out.println("实例化AService");

    }
    @PostConstruct  //要想让容器帮你管理这个方法（实际上就是让容器在适当的时候调用这个方法），要加上这个注解.注解表明这个方法会在构造器之后调用 而初始化方法就是在构造后调用进行赋值，所以这就是初始化
    public void init() {
        System.out.println("初始化AService");
    }


    @PreDestroy //注解表明在销毁对象之前去调用这个方法
    public void destory(){
        System.out.println("销毁AService");
    }

    public String find(){
        return aDao.select();
    }
}
