package com.artificial.smart_job_scheduler_engine.Controller;

import com.artificial.smart_job_scheduler_engine.Model.Job;
import com.artificial.smart_job_scheduler_engine.Repository.JobRepository;
import com.artificial.smart_job_scheduler_engine.dto.JobRequestDto;
import com.artificial.smart_job_scheduler_engine.dto.JobResponseDto;
import com.artificial.smart_job_scheduler_engine.enums.JobStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository jobRepository;

    public JobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Submit new job

    @PostMapping
    public ResponseEntity<JobResponseDto> submitJob(
            @RequestBody JobRequestDto dto) {

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Job title is required");
        }

        if (dto.getPriority() == null) {
            throw new IllegalArgumentException("Job priority is required");
        }

        if (dto.getMaxRetries() < 0 || dto.getMaxRetries() > 10) {
            throw new IllegalArgumentException(
                    "Maximum retries must be between 0 and 10"
            );
        }

        if (dto.getDeadline() != null &&
                !dto.getDeadline().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Deadline must be in the future"
            );
        }

        Job job = new Job();

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setPriority(dto.getPriority());
        job.setStatus(JobStatus.PENDING);
        job.setRetryCount(0);
        job.setMaxRetries(dto.getMaxRetries());
        job.setDeadline(dto.getDeadline());
        job.setNextRetryAt(null);
        job.setCreatedAt(LocalDateTime.now());
        job.setSimulateFailure(dto.isSimulateFailure());

        Job savedJob = jobRepository.save(job);

        return ResponseEntity.ok(convertToResponse(savedJob));
    }

    // Get job by ID

    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDto> getJob(
            @PathVariable Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Job not found"));

        return ResponseEntity.ok(convertToResponse(job));
    }

    // Get all jobs

    @GetMapping
    public ResponseEntity<List<JobResponseDto>> getAllJobs() {

        List<JobResponseDto> jobs = jobRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();

        return ResponseEntity.ok(jobs);
    }

    private JobResponseDto convertToResponse(Job job) {

        return new JobResponseDto(
                job.getId(),
                job.getTitle(),
                job.getPriority(),
                job.getStatus(),
                job.getRetryCount(),
                job.getMaxRetries(),
                job.getDeadline(),
                job.getNextRetryAt(),
                job.getCreatedAt(),
                job.getCompletedAt(),
                job.getLastError()
        );
    }
}