package com.artificial.smart_job_scheduler_engine.Service;

import com.artificial.smart_job_scheduler_engine.Model.Job;
import com.artificial.smart_job_scheduler_engine.Repository.JobRepository;
import com.artificial.smart_job_scheduler_engine.config.JobPriorityConfig;
import com.artificial.smart_job_scheduler_engine.enums.JobPriority;
import com.artificial.smart_job_scheduler_engine.enums.JobStatus;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;

@Service
public class JobSchedulerService {

    private final JobRepository jobRepository;
    private final JobExecutionService jobExecutionService;
    private final JobPriorityConfig priorityConfig;
    private final ExecutorService jobExecutor;

    public JobSchedulerService(
            JobRepository jobRepository,
            JobExecutionService jobExecutionService,
            JobPriorityConfig priorityConfig,
            ExecutorService jobExecutor) {

        this.jobRepository = jobRepository;
        this.jobExecutionService = jobExecutionService;
        this.priorityConfig = priorityConfig;
        this.jobExecutor = jobExecutor;
    }

    // Scheduler checks pending jobs every second

    @Scheduled(fixedDelay = 1000)
    public void scheduleJobs() {

        PriorityQueue<Job> queue = new PriorityQueue<>(
                Comparator
                        .comparingInt(
                                (Job job) -> priorityConfig.getPriorityValue(
                                        job.getPriority()
                                )
                        )
                        .thenComparing(Job::getCreatedAt)
        );

        // Fetch pending jobs

        for (Job job : jobRepository.findByStatus(JobStatus.PENDING)) {

            boolean retryReady = job.getNextRetryAt() == null ||
                    !job.getNextRetryAt().isAfter(LocalDateTime.now());

            if (retryReady) {
                queue.offer(job);
            }
        }

        // Process jobs according to priority

        while (!queue.isEmpty()) {

            Job job = queue.poll();

            boolean claimed = jobExecutionService.claimJob(job.getId());

            if (claimed) {

                jobExecutor.submit(() ->
                        jobExecutionService.executeJob(job.getId())
                );
            }
        }
    }
}