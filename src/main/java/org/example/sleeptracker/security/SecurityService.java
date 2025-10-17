package org.example.sleeptracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.sleeptracker.exceptions.JwtAuthenticationException;
import org.example.sleeptracker.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class SecurityService {

    @Value("${jwt.token.access-token.secret}")
    private String secretKey;

    @Value("${jwt.token.validity.in-millis}")
    private Long tokenValidityInMillis;

    public String createToken(User user) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("username", user.getUsername());

        return generateToken(claims, user.getUsername());
    }

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token) {
        try {
            Date expiration = getClaim(token, Claims::getExpiration);
            return expiration.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("Invalid JWT token: " + e.getMessage());
        }
    }

    private String generateToken(Map<String, String> claims, String subject) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + tokenValidityInMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
