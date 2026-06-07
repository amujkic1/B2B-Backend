package com.example.demo.messaging.producer;

import com.example.demo.dto.ResumeAnalysisRequestMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResumeAnalysisProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${resume.analysis.exchange}")
    private String exchangeName;

    @Value("${resume.analysis.routing-key}")
    private String routingKey;

    public ResumeAnalysisProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAnalysisRequest(Long analysisId) {
        ResumeAnalysisRequestMessage message = new ResumeAnalysisRequestMessage(analysisId);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }
}
