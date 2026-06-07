package com.example.demo.services;

import com.example.demo.dto.InterviewQuestionSet;
import com.example.demo.models.JobTarget;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class InterviewQuestionService {

    private final ChatClient chatClient;

    public InterviewQuestionService(ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    @Cacheable(value = "interview-questions", key = "#jobTarget.id")
    public InterviewQuestionSet generateQuestions(JobTarget jobTarget) {

        try {
            String systemPrompt = """
                You are a senior technical interviewer.
                Generate concise, practical mock interview questions tailored to the job profile.
                Return only structured output that matches the requested schema.
                """;

            String userPrompt = """
                Generate 5 to 8 mock interview questions for this role.

                --- TARGET JOB PROFILE ---
                Title: {jobTitle}
                Company: {company}
                Description: {jobDesc}
                Requirements: {jobReqs}

                Rules:
                - Include a mix of technical, behavioral, and role-specific questions
                - Avoid generic interview filler
                - Each question must be directly relevant to the role
                - For each question, provide:
                  - question
                  - conceptEvaluated
                  - structuralHint
                """;

                return this.chatClient.prompt()
                        .system(systemPrompt)
                        .user(userSpec -> userSpec
                                .text(userPrompt)
                                .param("jobTitle", jobTarget.getTitle())
                                .param("company", jobTarget.getCompany())
                                .param("jobDesc", jobTarget.getDescription())
                                .param("jobReqs", jobTarget.getRequirements()))
                        .call()
                        .entity(InterviewQuestionSet.class);
        } catch (Exception e) {
            throw new RuntimeException("AI interview question generation failed: " + e.getMessage(), e);
        }
    }
}
