package com.portalempleos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtils {

    // OJO: usamos los nombres internos para evitar el bucle de placeholders.
    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration}")
    private long jwtExpirationMs;

    // Tolerancia para diferencias de reloj (5 min)
    private static final long CLOCK_SKEW_MS = 5 * 60 * 1000L;

    /** Genera un JWT con email como subject y el rol en "role". */
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

    /** Valida firma, expiración y formato. */
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

    /** Extrae el email (subject) del token. */
    public String getEmailFromJwtToken(String token) {
        return getAllClaims(token).getSubject();
    }

    /** Extrae el role guardado en la claim "role". */
    public Optional<String> getRoleFromJwtToken(String token) {
        Object role = getAllClaims(token).get("role");
        return role == null ? Optional.empty() : Optional.of(role.toString());
    }

    /** Si recibes "Bearer xxx", devuelve "xxx". Si no, devuelve tal cual. */
    public String stripBearer(String tokenOrHeader) {
        if (tokenOrHeader == null) return null;
        String t = tokenOrHeader.trim();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return t.substring(7).trim();
        }
        return t;
        }

    /* ------------------------- PRIVADOS ------------------------- */

    private Claims getAllClaims(String token) {
        String raw = stripBearer(token);
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(CLOCK_SKEW_MS / 1000)
                .build()
                .parseClaimsJws(raw)
                .getBody();
    }

    /**
     * Construye la clave HMAC:
     * - Si el secreto es Base64 válido, lo usa.
     * - Si no, toma el texto plano y lo pasa por SHA-256 para obtener 32 bytes (256 bits).
     *   Esto garantiza una clave del tamaño mínimo requerido por HS256.
     */
    private Key getSigningKey() {
        try {
            // Intento Base64
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException ignored) {
            // No era Base64: derivamos 256 bits desde el texto plano
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
            // No debería ocurrir en JVM estándar
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
