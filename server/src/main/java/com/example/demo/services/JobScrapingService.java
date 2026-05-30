package com.example.demo.services;

import com.example.demo.dto.ScrapedJobResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class JobScrapingService {

    private final ChatClient chatClient;

    public JobScrapingService(ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    public ScrapedJobResponse scrapeAndStructureJob(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(6000)
                    .get();
            String rawMessyText = doc.body().text();

            String promptText = """
                You are a data extraction assistant. Below is an unformatted text dump scraped from a job board web page.
                Extract the core details and filter out unrelated text like cookie notices, headers, or footers.
                
                Messy Raw Text:
                {rawText}
                """;

            return this.chatClient.prompt()
                    .user(userSpec -> userSpec
                            .text(promptText)
                            .param("rawText", rawMessyText))
                    .call()
                    .entity(ScrapedJobResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("AI Web Scraping failed: " + e.getMessage());
        }
    }
}