package com.example.psicowise_backend_spring.util;

import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        key = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getId().toString())
                .claim("email", usuario.getEmail())
                .claim("roles", usuario.getRoles().stream()
                        .map(role -> role.getRole().name())
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30 ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String userId) {
        return (userId.equals(extractUserId(token)) && !isTokenExpired(token));
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }
}