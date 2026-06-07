package com.example.demo.dto;

import java.io.Serializable;

public record InterviewQuestion(
        String question,
        String conceptEvaluated,
        String structuralHint
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
