package com.example.demo.models;

import com.example.demo.enums.ResumeAnalysisStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analysis_jobs")
@Getter
@Setter
public class ResumeAnalysisJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "job_target_id", nullable = false)
    private Long jobTargetId;

    @Column(name = "resume_url", nullable = false, columnDefinition = "TEXT")
    private String resumeUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResumeAnalysisStatus status = ResumeAnalysisStatus.QUEUED;

    @OneToOne(mappedBy = "analysisJob", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ResumeGradeResult result;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}