package co.edu.corhuila.taskmanager.common.security;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Issues and validates HS256 JWTs (ADR-003). Claims: sub = user id, email, role.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationSeconds;
    private final Clock clock;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes}") long expirationMinutes,
                      Clock clock) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET must have at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirationSeconds = expirationMinutes * 60;
        this.clock = clock;
    }

    public String generateToken(UUID userId, String email, String role) {
        Instant now = clock.instant();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
    }

    /** Returns the identity in the token, or empty if it is invalid or expired. */
    public Optional<AuthenticatedUser> parse(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.of(new AuthenticatedUser(
                    UUID.fromString(claims.getSubject()),
                    claims.get("email", String.class),
                    claims.get("role", String.class)));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
