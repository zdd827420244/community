package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration//一般普通配置类会用这个注解
public class AConfig {
    //假设要把java自带的simpledateformat装配到容器中
    @Bean //定义第三方的bean，要加一个Bean注解
    public SimpleDateFormat simpleDateFormat(){  //要装配SimpleDateFormat，返回类型就得是SimpleDateFormat
        //注意，方法名就是bean的名字，bean的名字是通过方法名来命名的
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //这段话表明这个方法返回的对象将被装配到容器里，bean名就是方法名
    }
}
