package com.zone01.buy01.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * RATE LIMITING & THROTTLING
 * ---------------------------
 * The actual limiter (RequestRateLimiter -> Redis token bucket) is
 * configured per-route in application.yml. These beans just decide
 * WHAT the bucket is keyed on:
 *  - ipKeyResolver: protects unauthenticated endpoints (e.g. login) from
 *    brute-force / credential-stuffing from a single IP.
 *  - userKeyResolver: once authenticated, limits are fairer per-user
 *    instead of per-IP (important behind NATs/shared IPs).
 */
@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress() != null
                        ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                        : "unknown"
        );
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            return Mono.just(userId != null ? userId : "anonymous");
        };
    }
}