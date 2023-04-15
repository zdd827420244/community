package com.nowcoder.community;

import jakarta.annotation.PostConstruct;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication //这个注解所标识的类，表明这个类是个配置文件 一般程序入口才会用这个注解，一般配置类不会用。 正式运行肯定要运行该类，一运行就是以它为配置类开始运行
public class CommunityApplication {

	@PostConstruct
	public void init() {
		// 解决netty启动冲突问题
		// see Netty4Utils.setAvailableProcessors()
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}
	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
		//运行时，底层帮我们自动启动了tomcat应用服务器  spring boot里jar包内嵌了一个tomcat，服务器启动了，就能用浏览器访问了
		//该方法底层同时还自动帮我们创建了Spring容器  Web项目中，Spring容器不需要我们去主动创建，它是自动创建的
		//容器会自动扫描某些包下的某些Bean，将其装配到容器里
		// Spring应用在启动时需要配置，这个类是配置文件，是run方法的参数
		//@SpringBootApplication这个注解实际上是由好多其他注解组成的  比如@SpringBootConfiguration:表示这个类是个配置文件
		//@EnableAutoConfiguration表示要启动自动配置  所以实际上我们几乎不用做配置也能启动起来这个服务  因为它启动了自动配置
		//@ComponentScan组件扫描  自动扫描某些包下的某些Bean，装配到容器里  它会扫描配置类所在的包以及子包下的bean，本文所在包是community，
		// 当然，这个bean上必须有@Controller这样的注解才能被扫描到  和@Controller等价的注解还有@Service @Component @Repository
		//只要加这四个注解，Bean就能被扫描  实际上，另外三个注解的实现机制都是因为他们本身是由@Component实现的。
		//Controller Service Repository其实功能都一样，主要是语义上的区别。如果开发处理业务的组件，最好用@Service   开发处理请求的组件，@Controller  开发数据库访问组件，@Repository
		//如果全能，这个类处理请求  处理业务  访问数据库都能用，可以用@Component
	}

}
