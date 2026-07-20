package divar.aut.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for handling JSON Web Token (JWT) operations.
 * <p>
 * Provides methods to generate, validate, and extract information from
 * JWTs used for authentication. The signing key is loaded from application
 * properties via {@code jwt.secret}.
 * </p>
 */
@Component
public class JwtUtils {

    /**
     * The secret key used for signing and verifying JWTs.
     * Injected from the {@code jwt.secret} property.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Creates a {@link SecretKey} instance from the configured secret string.
     * <p>
     * Uses HMAC-SHA algorithm for signing. The key is derived from the raw
     * secret string bytes.
     * </p>
     *
     * @return the secret key for signing JWT tokens.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Generates a JWT token for the given username.
     * <p>
     * The token is issued with the current time and expires after 24 hours
     * (86400000 milliseconds). It is signed using the configured secret key.
     * </p>
     *
     * @param username the subject (username) to embed in the token.
     * @return a compact JWT string.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates a JWT token by checking its signature and structural integrity.
     * <p>
     * This method attempts to parse the token using the verification key.
     * If the token is malformed, expired, or has an invalid signature,
     * {@code false} is returned.
     * </p>
     *
     * @param token the JWT string to validate.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts the username (subject) from a validated JWT token.
     * <p>
     * This method assumes the token has already been validated or is guaranteed
     * to be well-formed. If the token is invalid, a {@link io.jsonwebtoken.JwtException}
     * will be thrown.
     * </p>
     *
     * @param token the JWT string from which to extract the username.
     * @return the username stored as the subject claim.
     * @throws io.jsonwebtoken.JwtException if the token is invalid or cannot be parsed.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}