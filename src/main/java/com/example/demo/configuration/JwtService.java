package com.example.demo.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.common.Enums;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtService {

    private final String JWT_KEY = "HarlestXakasjdhh12sadadwqdasdeascfasddacxajkasdjndhwnas";

    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;
    private static final long HOUR = MINUTE * 60;
    private static final long DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final long MONTH = DAY * 30;

    private final long JWT_EXPIRATION = WEEK * 1; // 1 tuần

    // Thêm role vào token
    public String generateToken(String email, Enums.role role) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);

        long issuedAtMillis = System.currentTimeMillis();

        return JWT.create()
                .withSubject(email)
                .withIssuer("lap-shop")
                .withIssuedAt(new Date(issuedAtMillis))
                .withExpiresAt(new Date(issuedAtMillis + JWT_EXPIRATION))
                .withClaim("role", role.toString())
                .sign(algorithm);
    }

    private DecodedJWT extractAllClaims(String token) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("lap-shop")
                .build();

        return verifier.verify(token);
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).getClaim("role").asString();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiresAt();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
