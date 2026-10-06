package Hotel_Manager_OOP_Class_.demo.service;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generateToken(Authentication authentication){
        SecretKey key = getKey();
        String authority = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();
        
        String role = authority.startsWith("ROLE_") ? authority.substring(5) : authority;
        
        return Jwts.builder()
                .subject(authentication.getName())
                .claim("role", role)
                .claim("authorities", List.of(authority))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+86400000))
                .signWith(key)
                .compact();
    }
    public String extractUsername(String token){
        return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
    }
}
