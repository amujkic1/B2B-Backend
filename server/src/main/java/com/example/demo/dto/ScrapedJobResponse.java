package com.example.demo.dto;

public record ScrapedJobResponse(
        String title,
        String company,
        String description,
        String requirements
) {}
