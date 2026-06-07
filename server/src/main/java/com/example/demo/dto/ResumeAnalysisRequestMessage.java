package com.example.demo.dto;

import java.io.Serializable;

public record ResumeAnalysisRequestMessage(
        Long analysisId
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
