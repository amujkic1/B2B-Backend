package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.mapper.ResumeAnalysisMapper;
import com.example.demo.models.ResumeAnalysisJob;
import com.example.demo.services.InterviewQuestionService;
import com.example.demo.services.JobScrapingService;
import com.example.demo.services.AiEvaluationService;
import com.example.demo.repositories.JobTargetRepository;
import com.example.demo.models.JobTarget;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.ResumeAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final JobScrapingService jobScrapingService;
    private final AiEvaluationService aiEvaluationService;
    private final JobTargetRepository jobTargetRepository;
    private final UserRepository userRepository;
    private final InterviewQuestionService interviewQuestionService;
    private final ResumeAnalysisService resumeAnalysisService;
    private final ResumeAnalysisMapper resumeAnalysisMapper;

    public AiController(JobScrapingService scrapingService, AiEvaluationService evaluationService,
                        JobTargetRepository jobTargetRepository, UserRepository userRepository,
                        InterviewQuestionService interviewQuestionService,
                        ResumeAnalysisService resumeAnalysisService,
                        ResumeAnalysisMapper resumeAnalysisMapper) {
        this.jobScrapingService = scrapingService;
        this.aiEvaluationService = evaluationService;
        this.jobTargetRepository = jobTargetRepository;
        this.userRepository = userRepository;
        this.interviewQuestionService = interviewQuestionService;
        this.resumeAnalysisService = resumeAnalysisService;
        this.resumeAnalysisMapper = resumeAnalysisMapper;
    }

    @PostMapping("/scrape")
    public ResponseEntity<ScrapedJobResponse> scrapeJob(@RequestBody JobScrapeRequest request) {
        ScrapedJobResponse response = jobScrapingService.scrapeAndStructureJob(request.url());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/grade/{jobId}")
    public ResponseEntity<ResumeGradeReport> gradeUserResume(@PathVariable Long jobId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobTarget jobTarget = jobTargetRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job Profile target not found"));

        if (user.getResumeUrl() == null) {
            return ResponseEntity.badRequest().body(null); // Must upload resume first
        }

        ResumeGradeReport report = aiEvaluationService.gradeResume(user.getResumeUrl(), jobTarget);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/interview-questions/{jobId}")
    public ResponseEntity<InterviewQuestionSet> generateInterviewQuestions(
            @PathVariable Long jobId,
            Principal principal
    ) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobTarget jobTarget = jobTargetRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job Profile target not found"));

        InterviewQuestionSet questionSet = interviewQuestionService.generateQuestions(jobTarget);
        return ResponseEntity.ok(questionSet);
    }

    @PostMapping("/grade/{jobId}/async")
    public ResponseEntity<ResumeAnalysisResponse> gradeUserResumeAsync(@PathVariable Long jobId, Principal principal) {
        ResumeAnalysisJob job = resumeAnalysisService.startAnalysis(principal.getName(), jobId);
        return ResponseEntity.accepted().body(resumeAnalysisMapper.toResponse(job));
    }

    @GetMapping("/grade/result/{analysisId}")
    public ResponseEntity<ResumeAnalysisResponse> getAnalysisResult(@PathVariable Long analysisId, Principal principal) {
        ResumeAnalysisJob job = resumeAnalysisService.getAnalysis(analysisId, principal.getName());
        return ResponseEntity.ok(resumeAnalysisMapper.toResponse(job));
    }
}