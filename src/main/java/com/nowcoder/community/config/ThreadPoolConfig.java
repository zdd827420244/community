package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling //定时任务默认不启动，必须加这个注解才启用
@EnableAsync//加这个注解，是让@Async这个注解生效
public class ThreadPoolConfig {
}
