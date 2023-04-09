package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration //配置类
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {  //希望把哪个对象装配到容器中，就要返回这个对象   方法名就是之后bean的名字
        //要让template访问数据库，要让它具备访问数据库的能力，template得能创建连接，连接是由连接工厂创建的，所以得把连接工厂注入进来
        //在定义bean时，方法声明这样的参数，spring就会自动把参数bean注入进来，也就是这个工厂bean已经被容器装配了，spring自动会注入
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);//把工厂设置给template，这样template就具备了访问数据库的能力

        //配置主要配置的是序列化的方式，因为我们写的是java程序，得到的是java数据，要把数据存到redis数据库里，要指定一种序列化/数据转换的方式

        // 设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string()); //RedisSerializer类中有序列化方式统一的定义 RedisSerializer.string()返回一个能序列化字符串的序列化器
        // 设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());//因为json格式的数据是格式化的，恢复过来很好识别
        // 设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet(); //让以上template配置生效
        return template;
    }

}
