package com.portalempleos.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration}")
    private long jwtExpirationMs;

    private static final long CLOCK_SKEW_MS = 5 * 60 * 1000L;

    /** Genera un JWT con el email y rol */
    public String generateJwtToken(String email, String role) {
        Key key = getSigningKey();
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Valida la firma y la expiración del token */
    public boolean validateJwtToken(String token) {
        try {
            String raw = stripBearer(token);
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .setAllowedClockSkewSeconds(CLOCK_SKEW_MS / 1000)
                    .build()
                    .parseClaimsJws(raw);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("❌ Token inválido: " + e.getMessage());
            return false;
        }
    }

    public String getEmailFromJwtToken(String token) {
        return getAllClaims(token).getSubject();
    }

    public String getRoleFromJwtToken(String token) {
        Object role = getAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    private Claims getAllClaims(String token) {
        String raw = stripBearer(token);
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(CLOCK_SKEW_MS / 1000)
                .build()
                .parseClaimsJws(raw)
                .getBody();
    }

    private Key getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            byte[] raw = jwtSecret.getBytes(StandardCharsets.UTF_8);
            byte[] digest = sha256(raw);
            return Keys.hmacShaKeyFor(digest);
        }
    }

    private static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public String stripBearer(String tokenOrHeader) {
        if (tokenOrHeader == null)
            return null;
        String t = tokenOrHeader.trim();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return t.substring(7).trim();
        }
        return t;
    }
}
