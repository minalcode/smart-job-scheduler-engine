package com.artificial.smart_job_scheduler_engine.Service;

import com.artificial.smart_job_scheduler_engine.Model.Job;
import com.artificial.smart_job_scheduler_engine.Repository.JobRepository;
import com.artificial.smart_job_scheduler_engine.enums.JobStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class JobExecutionService {

    private final JobRepository jobRepository;
    private final JobRetryService jobRetryService;

    public JobExecutionService(
            JobRepository jobRepository,
            JobRetryService jobRetryService) {

        this.jobRepository = jobRepository;
        this.jobRetryService = jobRetryService;
    }

    // Claim job before execution

    @Transactional
    public boolean claimJob(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Job not found"));

        if (job.getStatus() != JobStatus.PENDING) {
            return false;
        }

        job.setStatus(JobStatus.RUNNING);

        jobRepository.save(job);

        return true;
    }

    // Execute job

    public void executeJob(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElse(null);

        if (job == null) {
            return;
        }

        try {

            // Check deadline

            if (job.getDeadline() != null &&
                    LocalDateTime.now().isAfter(job.getDeadline())) {

                job.setStatus(JobStatus.FAILED);
                job.setLastError("Job deadline exceeded");
                job.setCompletedAt(LocalDateTime.now());

                jobRepository.save(job);
                return;
            }

            // Simulate job processing

            System.out.println(
                    "Executing Job: " + job.getTitle()
            );

            Thread.sleep(2000);

            // Simulate failure for testing retry mechanism

            if (job.isSimulateFailure()) {
                throw new RuntimeException("Simulated job failure");
            }

            // Mark job completed

            job.setStatus(JobStatus.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());
            job.setLastError(null);

            jobRepository.save(job);

            System.out.println(
                    "Job Completed: " + job.getTitle()
            );

        } catch (InterruptedException ex) {

            Thread.currentThread().interrupt();

            jobRetryService.handleFailure(
                    jobId, "Job execution interrupted"
            );

        } catch (Exception ex) {

            jobRetryService.handleFailure(
                    jobId, ex.getMessage()
            );
        }
    }
}