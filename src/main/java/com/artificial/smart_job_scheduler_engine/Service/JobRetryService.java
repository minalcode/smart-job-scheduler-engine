package com.artificial.smart_job_scheduler_engine.Service;

import com.artificial.smart_job_scheduler_engine.Model.Job;
import com.artificial.smart_job_scheduler_engine.Repository.JobRepository;
import com.artificial.smart_job_scheduler_engine.enums.JobStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class JobRetryService {

    private final JobRepository jobRepository;

    public JobRetryService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public void handleFailure(Long jobId, String errorMessage) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Job not found"));

        job.setRetryCount(job.getRetryCount());
        job.setLastError(errorMessage);

        if (job.getRetryCount() < job.getMaxRetries()) {

            job.setRetryCount(job.getRetryCount() + 1);

            // Exponential backoff: 2, 4, 8, 16 seconds

            long delaySeconds = (long) Math.pow(
                    2, job.getRetryCount()
            );

            job.setNextRetryAt(
                    LocalDateTime.now().plusSeconds(delaySeconds)
            );

            job.setStatus(JobStatus.PENDING);

        } else {

            job.setStatus(JobStatus.FAILED);
            job.setCompletedAt(LocalDateTime.now());
        }

        jobRepository.save(job);
    }
}