package com.example.demo.dto;

import com.example.demo.enums.ResumeAnalysisStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResumeAnalysisResponse {
    private Long id;
    private Long userId;
    private Long jobTargetId;
    private ResumeAnalysisStatus status;
    private ResumeGradeReport result;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}