package com.bstek.common.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bstek.ureport.model.Report;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CaffeineCacheConfig {

    @Bean
    public Cache<String, Report> caffeineCache() {
        return Caffeine.newBuilder().expireAfterWrite(60*3, TimeUnit.SECONDS).build();
    }
    
    @Bean("caffeineCacheNonce")
    public Cache<String, String> caffeineCacheNonce() {
        return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();
    }
}