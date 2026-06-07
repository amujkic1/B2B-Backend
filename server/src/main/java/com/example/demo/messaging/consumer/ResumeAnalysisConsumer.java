package com.example.demo.messaging.consumer;

import com.example.demo.dto.ResumeAnalysisRequestMessage;
import com.example.demo.dto.ResumeGradeReport;
import com.example.demo.enums.ResumeAnalysisStatus;
import com.example.demo.mapper.ResumeAnalysisMapper;
import com.example.demo.models.JobTarget;
import com.example.demo.models.ResumeAnalysisJob;
import com.example.demo.models.ResumeGradeResult;
import com.example.demo.repositories.JobTargetRepository;
import com.example.demo.repositories.ResumeAnalysisJobRepository;
import com.example.demo.repositories.ResumeGradeResultRepository;
import com.example.demo.services.AiEvaluationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ResumeAnalysisConsumer {

    private final ResumeAnalysisJobRepository resumeAnalysisJobRepository;
    private final JobTargetRepository jobTargetRepository;
    private final AiEvaluationService aiEvaluationService;
    private final ResumeAnalysisMapper mapper;
    private final ResumeGradeResultRepository resumeGradeResultRepository;

    public ResumeAnalysisConsumer(
            ResumeAnalysisJobRepository resumeAnalysisJobRepository,
            JobTargetRepository jobTargetRepository,
            AiEvaluationService aiEvaluationService, ResumeAnalysisMapper mapper, ResumeGradeResultRepository resumeGradeResultRepository
    ) {
        this.resumeAnalysisJobRepository = resumeAnalysisJobRepository;
        this.jobTargetRepository = jobTargetRepository;
        this.aiEvaluationService = aiEvaluationService;
        this.mapper = mapper;
        this.resumeGradeResultRepository = resumeGradeResultRepository;
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

            ResumeGradeResult result = mapper.toEntity(report);
            result.setAnalysisJob(analysisJob);
            resumeGradeResultRepository.save(result);

            analysisJob.setStatus(ResumeAnalysisStatus.COMPLETED);
            analysisJob.setResult(result);
            analysisJob.setErrorMessage(null);
            resumeAnalysisJobRepository.save(analysisJob);

        } catch (Exception e) {
            analysisJob.setStatus(ResumeAnalysisStatus.FAILED);
            analysisJob.setErrorMessage(e.getMessage());
            resumeAnalysisJobRepository.save(analysisJob);
        }
    }

}