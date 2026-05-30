package com.example.demo.controllers;

import com.example.demo.dto.ScrapedJobResponse;
import com.example.demo.mapper.JobTargetMapper;
import com.example.demo.models.JobTarget;
import com.example.demo.models.User;
import com.example.demo.repositories.JobTargetRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
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
}
