package com.qfang.hackbot.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Performance configuration for caching and async processing.
 * 
 * This configuration optimizes application performance by:
 * 1. Setting up high-performance Caffeine cache with TTL and size limits
 * 2. Configuring thread pool for async operations to prevent blocking
 */
@Configuration
public class PerformanceConfig implements CachingConfigurer {

    /**
     * Configure Caffeine cache manager for high-performance caching.
     * - Maximum 1000 entries per cache
     * - TTL of 10 minutes
     * - Automatic eviction of expired entries
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("questions", "answers");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }

    /**
     * Configure thread pool for async operations.
     * - Core pool size: 2
     * - Max pool size: 10
     * - Queue capacity: 500
     * This prevents thread starvation and enables non-blocking operations.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
