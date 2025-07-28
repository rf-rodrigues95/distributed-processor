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
        //Both RedisTemplate and StringRedisTemplate will use the default connection
        return new LettuceConnectionFactory(); //default: localhost:6379 
    }

    @Bean
    public ConcurrentMetadataStore redisMetadataStore(LettuceConnectionFactory factory) {
        return new RedisMetadataStore(factory);
    }
}
