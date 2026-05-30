package com.example.demo.services;

import com.example.demo.dto.ResumeGradeReport;
import com.example.demo.models.JobTarget;
import org.apache.tika.Tika;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel; // Add this import
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.net.URL;

@Service
public class AiEvaluationService {

    private final ChatClient chatClient;
    private final Tika tika = new Tika();

    public AiEvaluationService(ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    public ResumeGradeReport gradeResume(String resumeUrl, JobTarget jobTarget) {
        try {
            InputStream inputStream = new URL(resumeUrl).openStream();
            String extractedResumeText = tika.parseToString(inputStream);

            String systemPrompt = """
                You are an elite Technical Recruiter evaluating a candidate's resume text against a target job profile.
                Analyze gaps objectively and provide a structural grading breakdown.
                """;

            String userPrompt = """
                Evaluate the candidate's resume against the targeted job details.
                
                --- TARGET JOB PROFILE ---
                Title: {jobTitle}
                Company: {company}
                Description: {jobDesc}
                Requirements: {jobReqs}
                
                --- CANDIDATE RESUME ---
                {resumeText}
                """;

            return this.chatClient.prompt()
                    .system(systemPrompt)
                    .user(userSpec -> userSpec
                            .text(userPrompt)
                            .param("jobTitle", jobTarget.getTitle())
                            .param("company", jobTarget.getCompany())
                            .param("jobDesc", jobTarget.getDescription())
                            .param("jobReqs", jobTarget.getRequirements())
                            .param("resumeText", extractedResumeText))
                    .call()
                    .entity(ResumeGradeReport.class);

        } catch (Exception e) {
            throw new RuntimeException("AI Resume grading failed: " + e.getMessage(), e);
        }
    }
}