package com.artificial.smart_job_scheduler_engine.config;

import com.artificial.smart_job_scheduler_engine.enums.JobPriority;

import org.springframework.context.annotation.Configuration;

@Configuration
public class JobPriorityConfig {

    public int getPriorityValue(JobPriority priority) {

        return switch (priority) {
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
        };
    }
}