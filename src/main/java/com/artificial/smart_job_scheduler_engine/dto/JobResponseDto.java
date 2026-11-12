package com.artificial.smart_job_scheduler_engine.dto;

import com.artificial.smart_job_scheduler_engine.enums.JobPriority;
import com.artificial.smart_job_scheduler_engine.enums.JobStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobResponseDto {

    private Long id;

    private String title;

    private JobPriority priority;

    private JobStatus status;

    private int retryCount;

    private int maxRetries;

    private LocalDateTime deadline;

    private LocalDateTime nextRetryAt;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private String lastError;
}