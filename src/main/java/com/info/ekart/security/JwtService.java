package com.info.ekart.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	
	// Secret key for signing JWT
    // MUST be long enough (>= 256 bits)
	//we store it in properties files
	@Value("${jwt.secretKey}")
	private  String SECRET;
	
	 // Token validity (1 hour)
    private final long EXPIRATION_TIME = 1000 * 60 * 60;
    
    
    private Key getSigningKey() {
    	return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
    
    // 🔐 Generate JWT
    public String generateToken(String username,String role) {
    	return Jwts.builder()
    			.setSubject(username)
    			.claim("role", role)
    			.setIssuedAt(new Date())
    			.setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
    			.signWith(getSigningKey(),SignatureAlgorithm.HS256)
    			.compact();
    	
    }
    
    // 📦 Extract username from token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 📦 Extract role from token
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // 📦 Extract all claims
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 🔎 Validate token
    public boolean isTokenValid(String token, String username) {

        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }
    
    
    
    
}
