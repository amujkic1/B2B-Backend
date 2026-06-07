package com.example.demo.dto;

import java.io.Serializable;
import java.util.List;

public record InterviewQuestionSet(
        Long jobId,
        String jobTitle,
        String company,
        List<InterviewQuestion> questions
) implements Serializable {
    private static final long serialVersionUID = 1L;
}