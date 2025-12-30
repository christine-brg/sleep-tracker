package org.example.sleeptracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.sleeptracker.exceptions.JwtAuthenticationException;
import org.example.sleeptracker.models.TokenType;
import org.example.sleeptracker.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.example.sleeptracker.models.TokenType.ACCESS_TOKEN;
import static org.example.sleeptracker.models.TokenType.REFRESH_TOKEN;

@Service
public class SecurityService {

    @Value("${jwt.token.access-token.secret}")
    private String accessTokenSecretKey;

    @Value("${jwt.token.refresh-token.secret}")
    private String refreshTokenSecretKey;

    @Value("${jwt.token.validity.in-millis}")
    private long jwtAccessTokenValidityInMillis;

    @Value("${jwt.token.refresh-token.validity-in-millis}")
    private long jwtRefreshTokenValidityInMillis;

    public String createAccessToken(User user) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("username", user.getUsername());

        return generateToken(claims, user.getUsername(), ACCESS_TOKEN);
    }

    public String createRefreshToken(User user) {
        return generateToken(new HashMap<>(), user.getUsername(), REFRESH_TOKEN);
    }

    public String getUsername(String token) {
        return getUsername(token, ACCESS_TOKEN);
    }

    public String getUsername(String token, TokenType tokenType) {
        return getClaim(token, Claims::getSubject, tokenType);
    }

    public boolean isTokenValid(String token) {
        return isTokenValid(token, ACCESS_TOKEN);
    }

    public boolean isTokenValid(String token, TokenType tokenType) {
        try {
            Date tokenExpirationDate = getClaim(token, Claims::getExpiration, tokenType);
            return tokenExpirationDate.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            if (e.getMessage().startsWith("JWT signature does not match locally computed signature.")) {
                throw new JwtAuthenticationException("Jwt token is invalid");
            }
            throw new JwtAuthenticationException(e.getMessage());
        }
    }

    private String generateToken(Map<String, String> claims, String username, TokenType tokenType) {
        Date tokenStartDate = new Date();
        long validity = (tokenType == ACCESS_TOKEN)
                ? jwtAccessTokenValidityInMillis
                : jwtRefreshTokenValidityInMillis;

        Date tokenEndDate = new Date(tokenStartDate.getTime() + validity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(tokenStartDate)
                .setExpiration(tokenEndDate)
                .setSubject(username)
                .signWith(getSigningKey(tokenType), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T getClaim(String token, Function<Claims, T> claimExtractor, TokenType tokenType) {
        final Claims claims = getAllClaims(token, tokenType);
        return claimExtractor.apply(claims);
    }

    private Key getSigningKey(TokenType tokenType) {
        String tokenSecretKey = getTokenSecret(tokenType);
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getAllClaims(String token, TokenType tokenType) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(tokenType))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String getTokenSecret(TokenType tokenType) {
        String tokenSecretKey;
        if (tokenType == ACCESS_TOKEN) {
            tokenSecretKey = accessTokenSecretKey;
        } else {
            tokenSecretKey = refreshTokenSecretKey;
        }
        return tokenSecretKey;
    }

    public long getTokenRemainingSeconds(String token, TokenType tokenType) {
        Date expiration = getClaim(token, Claims::getExpiration, tokenType);
        long remainingMillis = expiration.getTime() - System.currentTimeMillis();
        return Math.max(remainingMillis / 1000, 0);
    }
}
