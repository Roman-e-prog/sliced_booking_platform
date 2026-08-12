package com.roman.user_service.security.jwt;

import com.roman.user_service.security.user.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    // ------------------ PUBLIC API ------------------

    public Long extractUserId(String token) {
        return Long.valueOf(extractClaim(token, Claims::getSubject));
    }

    public String generateToken(UserDetails userDetails) {

        Long userId = ((CustomUserDetails) userDetails).getId();
        String role = ((CustomUserDetails) userDetails).getUser().getRole().name();

        return buildToken(
                Map.of("role", role),
                String.valueOf(userId),     // subject = userId
                accessTokenExpiration
        );
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        Long userIdFromToken = extractUserId(token);
        Long userIdFromDetails = ((CustomUserDetails) userDetails).getId();
        return userIdFromToken.equals(userIdFromDetails) && !isTokenExpired(token);
    }


    // ------------------ INTERNAL HELPERS ------------------

    private String buildToken(Map<String, Object> extraClaims,
                              String subject,
                              long expirationMillis) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }
    //public gemacht, um userdaten für microservice zu erfassen
    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
