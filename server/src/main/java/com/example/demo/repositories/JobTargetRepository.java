package com.example.demo.repositories;

import com.example.demo.models.JobTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobTargetRepository extends JpaRepository<JobTarget, Long> {
    List<JobTarget> findAllByUserId(Long userId);
    Optional<JobTarget> findByIdAndUserId(Long id, Long userId);
}