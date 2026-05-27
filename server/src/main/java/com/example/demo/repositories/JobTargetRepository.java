package com.example.demo.repositories;

import com.example.demo.models.JobTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTargetRepository extends JpaRepository<JobTarget, Long> {
    public JobTarget findByUserId(Long userId);
}
