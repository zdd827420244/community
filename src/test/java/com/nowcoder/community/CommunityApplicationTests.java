package com.nowcoder.community;

import com.nowcoder.community.dao.ADao;
import com.nowcoder.community.dao.ADaoHibernateImpl;
import com.nowcoder.community.service.AService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//但我们测试肯定也想和正式运行一样，想让CommunityApplication作为配置类，怎么启用呢  加这个注释
class CommunityApplicationTests implements ApplicationContextAware {
//测试代码
	//容器是自动创建，怎么得到这个容器呢  哪个类想得到容器，就得实现ApplicationContextAware接口  实现接口就得实现下面这个setApplicationContext方法
	private ApplicationContext applicationContext;//变量，用来记录Spring容器。启动后，自动set之后就可以使用这个容器了


	@Test
	void contextLoads() {

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		//传入的这个参数实际上就是Spring容器  ApplicationContext接口实际上最终继承自BeanFactory接口 ApplicationContext子接口相比于父接口实现了更多东西，更强大
		//Spring容器会在扫描组件时，检测这个类实现了ApplicationContextAware接口，会调用这个set方法，把自身传进来  我们只要引用记录这个容器，之后就能访问容器了
		this.applicationContext=applicationContext;
	}
	@Test
	public void testApplicationContext() {
		System.out.println(applicationContext);
		//如何使用这个容器呢  可以通过这个容器管理bean
		ADao aDao=applicationContext.getBean(ADao.class);//getBean可以通过名字，也可以通过类型获取，一般通过类型  用ADaoHibernateImpl.class更好
		System.out.println(aDao.select());//但要注意，如果用接口的话，如果接口有多个实现类bean，这时你getBean就会报错
		//解决方案，在你想要的Bean类上加一个注解@Primary，这样你getBean时这个Bean有更高的优先级返还给你
		//Spring就是通过这种方式降低Bean之间的耦合度  调用方调用接口，调用方和实现类不会发生任何直接关系
		//每个bean都是有名字的  bean的默认名字就是它的类名，首字母小写，但是你要是觉得这样名字太长，也可以自定义bean的名字
		aDao=applicationContext.getBean("AHibernate",ADao.class);//但是如果通过名字获取，得到的是Object对象，因为你不知道是什么类型的
		//要么强制类型转换，要么再加一个参数ADao.class，意思就是得到的Bean转型成ADao类型
		System.out.println(aDao.select());
		//除了帮我们创建bean，容器其实还有很多帮我们管理bean的手段   初始化  销毁bean
	}
	@Test
	public void testBeanManagement(){
		AService aService=applicationContext.getBean(AService.class);
		System.out.println(aService); //打印对象，前面是对象类型，@后面是哈希值
		aService=applicationContext.getBean(AService.class);
		System.out.println(aService); //被Spring容器管理的Bean，默认是单例的，所以构建、初始化、销毁都只有一次，两次输出hash值一样，说明是同一个对象
		//如果不想单例，想每次getBean都新建一个实例  加个注解@Scope，也就是作用范围
		//单例是启动时初始化，多例是每次getBean时初始化一个
		//以上都是Spring管理自己写的类，可以自己加注解什么的，但是如果想在容器内装配第三方的bean，类是别人写的，是jar包里的，没源码
		//解决方法自己写一个配置类，在配置类当中通过bean注解进行声明，来解决

	}
	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat=applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
		//以上就是一般使用容器的方式，主动获取容器，然后getBean去得到想要的Bean来用  这种主动获取的方式比较笨拙
		//还是要使用更方便的  也就是依赖注入
	}
	//当前Bean要使用ADao，不需要通过容器getBean，我只要声明我要给当前的Bean注入ADao就行  依赖注入 只要声明一个属性，写一个注解，这个Bean就可以直接拿来用了，而且还不用实例化
	@Autowired//注入的注解，加在一个成员变量之前就行  这个注解也可以加载set方法前、构造器前，完成注入
	@Qualifier("AHibernate")//如果不想采用默认优先级，就想要AHibernateDao，就加这个注解，里面是Bean的名字
	private ADao aDao;//Spring容器把ADao bean注入到这个属性aDao中   用接口，降低了耦合度

	@Autowired//
	private ADaoHibernateImpl aDaoHibernate;//

	@Autowired//
	private SimpleDateFormat simpleDateFormat;//

	@Test
	public void testDI(){
		System.out.println(aDao);//这时获得的是MyBatis，因为加过@Primary优先级高
		System.out.println(aDaoHibernate);
		System.out.println(simpleDateFormat);
	}

}
