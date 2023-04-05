package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration //配置类注解
public class KaptchaConfig {

    @Bean //这个Bean将被Spring容器装配  装配的肯定是组件核心对象  Kaptcha核心组件是Producer接口，里面俩方法，分别是生成随机字符串，生成图片  //这里最后装配的是Producer对象
    public Producer kaptchaProducer(){
        Properties properties=new Properties(); //Properties本身就是为了封装.properties文件的，所以可以直接从.properties文件中读，也可以直接在这里实例化，往里面塞值
        properties.setProperty("kaptcha.image.width","100"); //Properties里面，和.properties文件一样，都是一堆key value值
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32"); //字号大小
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");//生成验证码的随机字符的范围
        properties.setProperty("kaptcha.textproducer.char.length","4");
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");//要采用哪个噪声/干扰类  就是生成图片上要加一些干扰 它本身生成的其实就带一点干扰了，所以这里不选用干扰也可以

        DefaultKaptcha kaptcha=new DefaultKaptcha();  //DefaultKaptcha是Producer的实现类
        Config config=new Config(properties);//Config依赖于Properties对象
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
