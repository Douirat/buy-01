package com.zone01.buy01.gateway.cashing;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * CACHING
 * --------
 * Not every request needs to hit a downstream service. Read-heavy,
 * rarely-changing data (e.g. product catalog listings) can be cached
 * at the gateway layer with a short TTL, cutting load on backend
 * services and improving latency for repeated identical GETs.
 *
 * In Spring Cloud Gateway (reactive) this is typically wired up as a
 * LocalResponseCache filter per-route (built-in since Spring Cloud
 * Gateway 4.x) rather than Spring's @Cacheable, since the gateway is
 * request/response-based, not method-based. This CacheManager bean is
 * provided for any custom caching filters (e.g. caching resolved JWT
 * claims to avoid re-parsing tokens on every request from the same
 * client within a short window).
 */
@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("jwtClaims", "routeResponses");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .maximumSize(10_000));
        return manager;
    }
}