package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues();

        RedisCacheConfiguration interviewQuestionsConfig = config
                .entryTtl(Duration.ofDays(7));  // Cache interview questions for 7 days

        RedisCacheConfiguration resumeGradeConfig = config
                .entryTtl(Duration.ofHours(24)); // Cache resume grades for 24 hours

        return RedisCacheManager.create(connectionFactory);
    }
}