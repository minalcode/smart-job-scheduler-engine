package com.artificial.smart_job_scheduler_engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SchedulerConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService jobExecutor() {

        return Executors.newFixedThreadPool(3);
    }
}