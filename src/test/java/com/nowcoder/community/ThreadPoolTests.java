package com.nowcoder.community;

import com.nowcoder.community.service.AService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);
    //logger在输出时自动会带上线程名和时间，适合在多线程环境下输出内容

    // JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);  //jdk的线程池都是通过Executors工厂实例化的

    // JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    //main里启动一个线程，main是会等这个线程结束的
    //而在test里启动一个线程，test不会等这个线程，二者是并发的，所以test如果后面没有逻辑的话会立即结束，所以还是让它sleep一会

    //Spring只要我们引入了核心，它自动就启动了线程池，我们就可以用，但我们需要在application.properties里进行一些配置
    //spring线程池不用我们初始化，它自动初始化好并放入容器进行管理了，我们只要注入就行
    // Spring普通线程池 它比JDK普通线程池更好，因为它能配核心、最大、队列容量，比JDK的更灵活
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    // Spring可执行定时任务的线程池  spring的Scheduler要想生效，我们要给他写一个配置类，否则无法正确的注入进来
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private AService alphaService;

    private void sleep(long m) {  //sleep会抛异常，为了之后调用方便点，我们干脆直接把sleep和异常处理封装到一起
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 1.JDK普通线程池
    @Test
    public void testExecutorService() {
        //线程池需要给他一个任务去执行，它会分配一个线程来执行这个任务  这个任务就是一个线程体，通常都是实现Runnable接口来实现这个任务/线程体
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ExecutorService");
            }//run里面就是这个任务要执行的具体逻辑
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }

        sleep(10000);
    }

    // 2.JDK定时任务线程池
    @Test
    public void testScheduledExecutorService() {
        Runnable task = new Runnable() { //也是要提供一个线程体
            @Override
            public void run() {
                logger.debug("Hello ScheduledExecutorService");
            }
        };

        //scheduleAtFixedRate以固定的频率去执行，每隔一段时间执行一次，执行多次。这个也可以延迟 延迟10000ms后执行  时间间隔是1000ms
        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);
        //scheduleWithFixedDelay以固定的延迟去执行，只执行一次，不过推迟一段时间后执行
        sleep(30000);
    }

    // 3.Spring普通线程池
    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }

        sleep(10000);
    }

    // 4.Spring定时任务线程池
    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskScheduler");
            }
        };

        Date startTime = new Date(System.currentTimeMillis() + 10000);
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);
        //第二个参数它这个不是延迟多少毫秒去执行了，而是要写一个具体的时间  它这个自动以毫秒为单位，不用指定单位了

        sleep(30000);
    }
//Spring这两个线程池还有简便的调用方式。我们可以在任意bean中的方法上加一个注解@Async，这样这个方法就可以作为一个线程体，在spring线程池中运行
    // 5.Spring普通线程池(简化)
    @Test
    public void testThreadPoolTaskExecutorSimple() {
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();  //底层spring会以多线程的方法，把execute1当做线程体，以多线程的方式调用它
        }

        sleep(10000);
    }

    // 6.Spring定时任务线程池(简化)
    @Test
    public void testThreadPoolTaskSchedulerSimple() {
        sleep(30000);
    }

}
