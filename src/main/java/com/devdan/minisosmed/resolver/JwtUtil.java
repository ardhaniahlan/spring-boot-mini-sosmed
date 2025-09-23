package com.devdan.minisosmed.resolver;

import com.devdan.minisosmed.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private final Key secretKey;
    private final Long expirationMillis;

    public JwtUtil(String secret, Long expirationMillis){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMillis = expirationMillis;
    }

    public String generatedToken(User user){
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + (1000 * 60 * 60);
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setSubject(user.getId())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .setIssuer("devdan-app")
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateAndGetClaims(String token){
        try {
            Jws<Claims> claimsJws =Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT Token", e);
        }
    }

    public Long getExpirationTime(String token){
        try{
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return claimsJws.getBody().getExpiration().getTime();
        }catch (JwtException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token", e);
        }
    }

    public String generateTokenWithExpiration(String userId, long expirationMillis) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + expirationMillis;
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(exp)
                .setIssuer("devdan-app")
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

}
