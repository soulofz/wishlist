package com.followdream.security;

import com.followdream.model.Security;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JwtUtils {

    @Value("${jwt.expiration}")
    private String jwtExpirationMinutes;

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(Security user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(Integer.parseInt(jwtExpirationMinutes))))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
    }

    public boolean validateToken(String token) {
        log.info("IN JwtUtils::validateToken");
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        } catch (JwtException e){
            log.error(e.getMessage());
            return false;
        } finally {
            log.info("IN JwtUtils::validateToken");
        }
        return true;
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
