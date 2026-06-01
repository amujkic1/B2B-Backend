package com.example.demo.dto;

import java.util.List;

public record InterviewQuestionSet(
        Long jobId,
        String jobTitle,
        String company,
        List<InterviewQuestion> questions
) {}