package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class QuartzTests {

    @Autowired
    private Scheduler scheduler; //注入调度器

    @Test
    public void testDeleteJob() {
        //因为表里已经有数据了，所以只要启动，Quartz它就会自动运行，所以要把表内数据删掉
        try {
            boolean result = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup")); //删除job，要传入一个job的唯一索引key，jobkey由job名和组名构成，有这俩名字能唯一确定一个job
            //返回boolean  成功还是失败
            //注意，删掉的是job，也就是jobdetail，tigger没了。但调度器scheduler数据还是有在
            System.out.println(result);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
