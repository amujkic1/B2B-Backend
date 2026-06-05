package com.example.demo.services;

import com.example.demo.enums.ResumeAnalysisStatus;
import com.example.demo.messaging.producer.ResumeAnalysisProducer;
import com.example.demo.models.JobTarget;
import com.example.demo.models.ResumeAnalysisJob;
import com.example.demo.models.User;
import com.example.demo.repositories.JobTargetRepository;
import com.example.demo.repositories.ResumeAnalysisJobRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ResumeAnalysisService {

    private final UserRepository userRepository;
    private final JobTargetRepository jobTargetRepository;
    private final ResumeAnalysisJobRepository resumeAnalysisJobRepository;
    private final ResumeAnalysisProducer resumeAnalysisProducer;

    public ResumeAnalysisService(
            UserRepository userRepository,
            JobTargetRepository jobTargetRepository,
            ResumeAnalysisJobRepository resumeAnalysisJobRepository,
            ResumeAnalysisProducer resumeAnalysisProducer
    ) {
        this.userRepository = userRepository;
        this.jobTargetRepository = jobTargetRepository;
        this.resumeAnalysisJobRepository = resumeAnalysisJobRepository;
        this.resumeAnalysisProducer = resumeAnalysisProducer;
    }

    public ResumeAnalysisJob startAnalysis(String email, Long jobId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getResumeUrl() == null) {
            throw new RuntimeException("User has no resume uploaded");
        }

        JobTarget jobTarget = jobTargetRepository.findByIdAndUserId(jobId, user.getId())
                .orElseThrow(() -> new RuntimeException("Job profile target not found"));

        ResumeAnalysisJob job = new ResumeAnalysisJob();
        job.setUserId(user.getId());
        job.setJobTargetId(jobTarget.getId());
        job.setResumeUrl(user.getResumeUrl());
        job.setStatus(ResumeAnalysisStatus.QUEUED);

        job = resumeAnalysisJobRepository.save(job);

        resumeAnalysisProducer.sendAnalysisRequest(job.getId());

        return job;
    }

    public ResumeAnalysisJob getAnalysis(Long analysisId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ResumeAnalysisJob job = resumeAnalysisJobRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analysis job not found"));

        if (!job.getUserId().equals(user.getId())) {
            throw new RuntimeException("Forbidden");
        }

        return job;
    }
}