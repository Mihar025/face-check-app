package com.zikpak.conf.coffein;


import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableCaching
public class CaffeineCacheConfig implements CachingConfigurer {

    @Bean
    public CacheManager cacheManager() {
        log.info("🚀 Initializing Caffeine In-Memory Cache Manager");

        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "workSites",
                "workSite",
                "userProfile",
                "workerAttendance",
                "users",
                "usersPhoto",
                "s3Photos"

        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10_000)  // Максимум 10,000 записей
                .expireAfterWrite(20, TimeUnit.MINUTES)  // Жизнь 1 час
                .recordStats());

        log.info("✅ Caffeine Cache configured with 7 cache regions");

        return cacheManager;
    }

    /**
     * Обработчик ошибок - приложение работает даже если кеш упадёт
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception,
                                            org.springframework.cache.Cache cache,
                                            Object key) {
                log.error("❌ Cache GET error for cache '{}' and key '{}': {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception,
                                            org.springframework.cache.Cache cache,
                                            Object key,
                                            Object value) {
                log.error("❌ Cache PUT error for cache '{}' and key '{}': {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception,
                                              org.springframework.cache.Cache cache,
                                              Object key) {
                log.error("❌ Cache EVICT error for cache '{}' and key '{}': {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception,
                                              org.springframework.cache.Cache cache) {
                log.error("❌ Cache CLEAR error for cache '{}': {}",
                        cache.getName(), exception.getMessage());
            }
        };
    }
}