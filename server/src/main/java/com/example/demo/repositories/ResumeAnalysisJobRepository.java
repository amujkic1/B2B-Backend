package com.example.demo.repositories;

import com.example.demo.enums.ResumeAnalysisStatus;
import com.example.demo.models.ResumeAnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeAnalysisJobRepository extends JpaRepository<ResumeAnalysisJob, Long> {
    List<ResumeAnalysisJob> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    List<ResumeAnalysisJob> findAllByStatus(ResumeAnalysisStatus status);
}