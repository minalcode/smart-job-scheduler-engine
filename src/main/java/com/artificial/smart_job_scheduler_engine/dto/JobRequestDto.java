package com.artificial.smart_job_scheduler_engine.dto;

import com.artificial.smart_job_scheduler_engine.enums.JobPriority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestDto {

    private String title;

    private String description;

    private JobPriority priority;

    private LocalDateTime deadline;

    private int maxRetries;

    private boolean simulateFailure;
}