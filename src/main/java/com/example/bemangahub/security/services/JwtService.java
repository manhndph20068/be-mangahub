package com.example.bemangahub.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.refresh-token-expiration-time-seconds}")
    private int REFRESH_TOKEN_EXPIRATION_TIME_SECONDS;

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        System.out.println("Claims: " + claims);
        return extractClaim(token, claims1 -> claims.get("id", String.class));
    }

    public String extractTokenToEmail(String token) {
        Claims claims = extractAllClaims(token);
        System.out.println("Claims: " + claims);
        return extractClaim(token, claims1 -> claims.get("sub", String.class));
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        System.out.println("date" + claims.getExpiration());
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(String email, String type, Integer id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", type);
        claims.put("id", id.toString());
        return createAccessToken(claims, email);
    }

    public String generateRefreshToken(String email, String type, Integer id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", type);
        claims.put("id", id.toString());
        return createRefreshToken(claims, email);
    }

    private String createAccessToken(Map<String, Object> claims, String email) {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationMillis = currentTimeMillis + (10 * 60 * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationMillis))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }


    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractEmail(token);
        System.out.println("username" + username);
        System.out.println("userDetails.getUsername()" + userDetails.getUsername());
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String createRefreshToken(Map<String, Object> claims, String email) {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationMillis = currentTimeMillis + (30L * 24 * 60 * 60 * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationMillis))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }


    public void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        Claims claims = extractAllClaims(refreshToken);
        Date expirationDate = claims.getExpiration();
        Date currentDate = new Date();
        long maxAgeSeconds = (expirationDate.getTime() - currentDate.getTime()) / 1000;
        Cookie cookie = new Cookie("mangahub_r_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Math.toIntExact(maxAgeSeconds));
        cookie.setPath("/");
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("mangahub_r_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
