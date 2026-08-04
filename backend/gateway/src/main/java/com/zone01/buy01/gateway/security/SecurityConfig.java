package com.zone01.buy01.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

/**
 * IMPORTANT: This gateway does NOT use Spring Security's authentication
 * model. Auth is handled entirely by JwtAuthenticationFilter, a Gateway
 * GlobalFilter that runs AFTER this filter chain and validates the JWT
 * itself, injecting X-User-Id / X-User-Role headers for downstream
 * services.
 *
 * If this chain requires .authenticated() on any path, Spring Security
 * rejects the request with 401 right here - before Gateway routing or
 * JwtAuthenticationFilter ever runs - because nothing in this app
 * populates the reactive SecurityContext. That was the bug: every
 * protected request was dying in this filter chain and never reaching
 * routing, JwtAuthenticationFilter, or the downstream service.
 *
 * So: permit everything at the Spring Security layer, and let the
 * custom GlobalFilter + RoleAuthorizationGatewayFilterFactory do the
 * actual access control.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .build();
    }
}