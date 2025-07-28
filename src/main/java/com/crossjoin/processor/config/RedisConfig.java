package com.crossjoin.processor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.redis.metadata.RedisMetadataStore;

@Configuration
public class RedisConfig {
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(); //6379 default
    }

    @Bean
    public ConcurrentMetadataStore redisMetadataStore(LettuceConnectionFactory factory) {
        return new RedisMetadataStore(factory);
    }
}
