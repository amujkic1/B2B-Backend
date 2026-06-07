package com.example.demo.repositories;

import com.example.demo.models.ResumeGradeResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeGradeResultRepository extends JpaRepository<ResumeGradeResult, Long> {
    Optional<ResumeGradeResult> findByAnalysisJobId(Long analysisJobId);
}