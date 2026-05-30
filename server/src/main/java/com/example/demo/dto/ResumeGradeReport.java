package com.example.demo.dto;

import java.util.List;

public record ResumeGradeReport(
        int matchingScore, // Score from 0 to 100
        String summary,
        List<String> matchedSkills,
        List<String> criticalGaps,
        List<String> actionableImprovements
) {}