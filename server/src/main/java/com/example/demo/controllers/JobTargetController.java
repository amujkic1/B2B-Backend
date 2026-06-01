package com.example.demo.controllers;

import com.example.demo.dto.ScrapedJobResponse;
import com.example.demo.mapper.JobTargetMapper;
import com.example.demo.models.JobTarget;
import com.example.demo.models.User;
import com.example.demo.repositories.JobTargetRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

public class JobTargetController {

    private final JobTargetRepository jobTargetRepository;
    private final UserRepository userRepository;
    private final JobTargetMapper jobTargetMapper;

    public JobTargetController(JobTargetRepository jobTargetRepository, UserRepository userRepository, JobTargetMapper jobTargetMapper) {
        this.jobTargetRepository = jobTargetRepository;
        this.userRepository = userRepository;
        this.jobTargetMapper = jobTargetMapper;
    }

    @PostMapping("/targets")
    public ResponseEntity<JobTarget> saveJobTarget(@RequestBody ScrapedJobResponse response, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobTarget jobTarget = jobTargetMapper.toEntity(response);
        jobTarget.setUser(user);

        JobTarget saved = jobTargetRepository.save(jobTarget);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/targets/{id}")
    @CacheEvict(value = {"interview-questions", "resume-grades"}, key = "#id")
    public ResponseEntity<JobTarget> updateJobTarget(@PathVariable Long id, @RequestBody ScrapedJobResponse response, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobTarget existingJobTarget = jobTargetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Job Target not found"));

        if(!existingJobTarget.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        existingJobTarget.setTitle(response.title());
        existingJobTarget.setCompany(response.company());
        existingJobTarget.setDescription(response.description());
        existingJobTarget.setRequirements(String.join("\n", response.requirements()));

        JobTarget updated = jobTargetRepository.save(existingJobTarget);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/targets/{id}")
    @CacheEvict(value = {"interview-questions", "resume-grades"}, key = "#id")
    public ResponseEntity<Void> deleteJobTarget(@PathVariable Long id) {
        jobTargetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
