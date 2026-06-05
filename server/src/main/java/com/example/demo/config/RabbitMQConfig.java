package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQConfig {

    @Value("${resume.analysis.queue}")
    private String queueName;

    @Value("${resume.analysis.exchange}")
    private String exchangeName;

    @Value("${resume.analysis.routing-key}")
    private String routingKey;

    @Bean
    public Queue resumeAnalysisQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public DirectExchange resumeAnalysisExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding resumeAnalysisBinding(Queue resumeAnalysisQueue, DirectExchange resumeAnalysisExchange) {
        return BindingBuilder.bind(resumeAnalysisQueue)
                .to(resumeAnalysisExchange)
                .with(routingKey);
    }

    @Bean
    public SimpleMessageConverter simpleMessageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("com.example.demo.dto.*"));
        return converter;
    }

}