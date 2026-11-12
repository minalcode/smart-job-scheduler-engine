package com.artificial.smart_job_scheduler_engine.Model;

import com.artificial.smart_job_scheduler_engine.enums.JobPriority;
import com.artificial.smart_job_scheduler_engine.enums.JobStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private JobPriority priority;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private int retryCount;

    private int maxRetries;

    private LocalDateTime deadline;

    private LocalDateTime nextRetryAt;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private boolean simulateFailure;

    @Column(length = 1000)
    private String lastError;
}