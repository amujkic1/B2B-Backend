package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "resume_grade_results")
@Getter
@Setter
public class ResumeGradeResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_job_id", nullable = false, unique = true)
    private ResumeAnalysisJob analysisJob;

    @Column(nullable = false)
    private int matchingScore;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resume_matched_skills", joinColumns = @JoinColumn(name = "result_id"))
    @Column(name = "skill")
    private List<String> matchedSkills;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resume_critical_gaps", joinColumns = @JoinColumn(name = "result_id"))
    @Column(name = "gap")
    private List<String> criticalGaps;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resume_actionable_improvements", joinColumns = @JoinColumn(name = "result_id"))
    @Column(name = "improvement")
    private List<String> actionableImprovements;
}