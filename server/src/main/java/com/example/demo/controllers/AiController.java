package com.example.demo.controllers;

import com.example.demo.dto.JobScrapeRequest;
import com.example.demo.dto.ResumeGradeReport;
import com.example.demo.dto.ScrapedJobResponse;
import com.example.demo.services.JobScrapingService;
import com.example.demo.services.AiEvaluationService;
import com.example.demo.repositories.JobTargetRepository;
import com.example.demo.models.JobTarget;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
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

    public AiController(JobScrapingService scrapingService, AiEvaluationService evaluationService,
                        JobTargetRepository jobTargetRepository, UserRepository userRepository) {
        this.jobScrapingService = scrapingService;
        this.aiEvaluationService = evaluationService;
        this.jobTargetRepository = jobTargetRepository;
        this.userRepository = userRepository;
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
}