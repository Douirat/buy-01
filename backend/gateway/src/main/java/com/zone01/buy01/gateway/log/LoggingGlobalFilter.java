package com.zone01.buy01.gateway.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * MONITORING & LOGGING
 * ---------------------
 * Every request gets a correlation ID (X-Trace-Id) generated once at the
 * edge and propagated to every downstream service, so a single request
 * can be traced across the whole microservices chain in your logs / in
 * a tool like Zipkin or the ELK stack.
 * Also logs method, path, status and latency for every call that passes
 * through the gateway - this is your single choke point for observability
 * instead of instrumenting every service individually.
 */
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        long start = System.currentTimeMillis();

        ServerHttpRequest mutated = request.mutate()
                .header("X-Trace-Id", traceId)
                .build();

        log.info("[{}] --> {} {}", traceId, request.getMethod(), request.getURI().getPath());

        return chain.filter(exchange.mutate().request(mutated).build())
                .doFinally(signal -> {
                    long duration = System.currentTimeMillis() - start;
                    log.info("[{}] <-- {} {} status={} duration={}ms",
                            traceId,
                            request.getMethod(),
                            request.getURI().getPath(),
                            exchange.getResponse().getStatusCode(),
                            duration);
                });
    }

    @Override
    public int getOrder() {
        // Runs before the JWT filter so the trace ID exists even on 401s.
        return -2;
    }
}
