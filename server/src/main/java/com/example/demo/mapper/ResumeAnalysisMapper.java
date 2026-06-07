package com.example.demo.mapper;

import com.example.demo.dto.ResumeAnalysisResponse;
import com.example.demo.dto.ResumeGradeReport;
import com.example.demo.models.ResumeAnalysisJob;
import com.example.demo.models.ResumeGradeResult;
import org.springframework.stereotype.Component;

@Component
public class ResumeAnalysisMapper {

    public ResumeAnalysisResponse toResponse(ResumeAnalysisJob job) {
        ResumeGradeReport report = null;
        if (job.getResult() != null) {
            report = toResumeGradeReport(job.getResult());
        }

        return ResumeAnalysisResponse.builder()
                .id(job.getId())
                .userId(job.getUserId())
                .jobTargetId(job.getJobTargetId())
                .status(job.getStatus())
                .result(report)
                .errorMessage(job.getErrorMessage())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    public ResumeGradeReport toResumeGradeReport(ResumeGradeResult result) {
        return new ResumeGradeReport(
                result.getMatchingScore(),
                result.getSummary(),
                result.getMatchedSkills(),
                result.getCriticalGaps(),
                result.getActionableImprovements()
        );
    }

    public ResumeGradeResult toEntity(ResumeGradeReport report) {
        ResumeGradeResult result = new ResumeGradeResult();
        result.setMatchingScore(report.matchingScore());
        result.setSummary(report.summary());
        result.setMatchedSkills(report.matchedSkills());
        result.setCriticalGaps(report.criticalGaps());
        result.setActionableImprovements(report.actionableImprovements());
        return result;
    }
}