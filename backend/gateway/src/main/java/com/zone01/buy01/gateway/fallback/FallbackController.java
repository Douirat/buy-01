package com.buy01.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Invoked by the CircuitBreaker filter (see application.yml fallbackUri)
 * whenever a downstream service is down/slow/tripped open, instead of
 * letting the client hang or see a raw 500/connection-refused error.
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback/users")
    public ResponseEntity<Map<String, Object>> userFallback() {
        return degraded("User service is temporarily unavailable. Please try again shortly.");
    }

    @RequestMapping("/fallback/products")
    public ResponseEntity<Map<String, Object>> productFallback() {
        return degraded("Product service is temporarily unavailable. Please try again shortly.");
    }

    @RequestMapping("/fallback/media")
    public ResponseEntity<Map<String, Object>> mediaFallback() {
        return degraded("Media service is temporarily unavailable. Please try again shortly.");
    }

    private ResponseEntity<Map<String, Object>> degraded(String message) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "SERVICE_UNAVAILABLE", "message", message));
    }
}