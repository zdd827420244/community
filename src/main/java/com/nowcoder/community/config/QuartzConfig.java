package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// 配置 初始化-> 数据库 -> 之后调用数据库就行，这个配置就没用了
@Configuration
public class QuartzConfig {

    // BeanFactory 是spring ioc里容器的顶层接口
    // FactoryBean可简化Bean的实例化过程:  初始化FactoryBean比初始化Bean要容易一些
    // 1.spring通过FactoryBean封装了Bean的实例化过程.
    // 2.可以将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.其他的Bean得到的是FactoryBean所管理的对象实例，拿下文的例子，把JobDetailFactoryBean注入到tigger里，得到的不是JobDetailFactoryBean，而是JobDetail

    // 配置JobDetail
    //@Bean  把bean注释掉，这俩bean就不会被初始化，不会生成数据，也就不会被调度
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);//声明管理的是哪个job类型
        factoryBean.setName("alphaJob");//给job任务起个名字
        factoryBean.setGroup("alphaJobGroup");//给任务起个组，多个任务可同属于一个组
        factoryBean.setDurability(true);//任务是持久保存么
        factoryBean.setRequestsRecovery(true);//任务是否可恢复
        return factoryBean;
    }

    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean（这个能处理更复杂的定时，比如每周周五晚上12点干嘛）)
    //@Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {  //trigger实际上是依赖于jobdetail的
        //spring容器，如果任务多了，JobDetailFactoryBean或者说JobDetail有多个实例，这时，这个参数名是alphaJobDetail，上面的bean名也是alphaJobDetail，spring会优先把重名的bean传进来
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000); //频率
        factoryBean.setJobDataMap(new JobDataMap()); //trigger底层要存一些job状态，这里是指定存储对象类型
        return factoryBean;
    }
    //配置完，一启动，quartz就会自动把配置输出到数据库里，然后quartz底层调度器就会根据数据库中的配置去调度
    //quartz里也有线程池，其线程池的配置也可以写到application.properties中

   /* // 刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }*/

}
