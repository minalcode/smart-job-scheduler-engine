package com.artificial.smart_job_scheduler_engine.Repository;

import com.artificial.smart_job_scheduler_engine.Model.Job;
import com.artificial.smart_job_scheduler_engine.enums.JobStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(JobStatus status);
}