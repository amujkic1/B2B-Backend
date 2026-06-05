package com.example.demo.messaging.consumer;

import com.example.demo.dto.ResumeAnalysisRequestMessage;
import com.example.demo.dto.ResumeGradeReport;
import com.example.demo.enums.ResumeAnalysisStatus;
import com.example.demo.models.JobTarget;
import com.example.demo.models.ResumeAnalysisJob;
import com.example.demo.repositories.JobTargetRepository;
import com.example.demo.repositories.ResumeAnalysisJobRepository;
import com.example.demo.services.AiEvaluationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ResumeAnalysisConsumer {

    private final ResumeAnalysisJobRepository resumeAnalysisJobRepository;
    private final JobTargetRepository jobTargetRepository;
    private final AiEvaluationService aiEvaluationService;

    public ResumeAnalysisConsumer(
            ResumeAnalysisJobRepository resumeAnalysisJobRepository,
            JobTargetRepository jobTargetRepository,
            AiEvaluationService aiEvaluationService
    ) {
        this.resumeAnalysisJobRepository = resumeAnalysisJobRepository;
        this.jobTargetRepository = jobTargetRepository;
        this.aiEvaluationService = aiEvaluationService;
    }

    @RabbitListener(queues = "${resume.analysis.queue}")
    public void consume(ResumeAnalysisRequestMessage message) {
        ResumeAnalysisJob analysisJob = resumeAnalysisJobRepository.findById(message.analysisId())
                .orElseThrow(() -> new RuntimeException("Analysis job not found"));

        try {
            analysisJob.setStatus(ResumeAnalysisStatus.PROCESSING);
            resumeAnalysisJobRepository.save(analysisJob);

            JobTarget jobTarget = jobTargetRepository.findById(analysisJob.getJobTargetId())
                    .orElseThrow(() -> new RuntimeException("Job target not found"));

            ResumeGradeReport report = aiEvaluationService.gradeResume(
                    analysisJob.getResumeUrl(),
                    jobTarget
            );

            analysisJob.setStatus(ResumeAnalysisStatus.COMPLETED);
            analysisJob.setReportJson(toJson(report));
            analysisJob.setErrorMessage(null);
            resumeAnalysisJobRepository.save(analysisJob);

        } catch (Exception e) {
            analysisJob.setStatus(ResumeAnalysisStatus.FAILED);
            analysisJob.setErrorMessage(e.getMessage());
            resumeAnalysisJobRepository.save(analysisJob);
        }
    }

    private String toJson(ResumeGradeReport report) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize report", e);
        }
    }
}