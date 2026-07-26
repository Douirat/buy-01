package com.zone01.buy01.user_service.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * GENERATES (signs) JWTs. This is the "producer" side - it lives in
 * User Service because User Service is the one place that owns the
 * User entity, the password hash, and can therefore prove someone is
 * who they claim to be.
 *
 * The gateway's JwtAuthenticationFilter is the "consumer" side - it only
 * verifies tokens signed here, using the SAME shared HMAC secret. It never
 * generates one.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs // default 1h
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String userId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId)          // read as claims.getSubject() by the gateway
                .claim("role", role)      // read as claims.get("role") by the gateway
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }
}