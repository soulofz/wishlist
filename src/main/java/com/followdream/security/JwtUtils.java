package com.followdream.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JwtUtils {

    @Value("${jwt.expiration}")
    private String jwtExpirationMinutes;

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(Integer.parseInt(jwtExpirationMinutes))))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
    }
}
