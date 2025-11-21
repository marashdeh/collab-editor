package com.example.collab_code_editor.core.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;

import java.nio.charset.StandardCharsets;

@Service
public class JwtTokenService {

    private final byte[] key;

    public JwtTokenService(@Value("${jwt.secret}") String secret) {
        this.key = secret.getBytes(StandardCharsets.UTF_8);
    }
    public String getUserId(String token) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String sub = claims.getSubject();
        return (sub != null) ? sub : String.valueOf(claims.get("userId"));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(key))
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (Exception e) {
            return false; // invalid, expired, malformed
        }
    }

    public String extractUsername(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(key))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // If your username = userId → return that
            return claims.getSubject();

        } catch (Exception e) {
            return null;
        }
    }

    public String generateAccessToken(String userId, long ttlMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMillis))
                .signWith(Keys.hmacShaKeyFor(key), SignatureAlgorithm.HS256)
                .compact();
    }
}
