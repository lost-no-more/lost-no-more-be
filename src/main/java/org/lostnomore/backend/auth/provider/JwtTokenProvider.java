package org.lostnomore.backend.auth.provider;

import static org.lostnomore.backend.global.exception.code.AuthErrorCode.EXPIRED_PERIOD_TOKEN;
import static org.lostnomore.backend.global.exception.code.AuthErrorCode.INVALID_TOKEN;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.oauth.dto.UserTokenResponse;
import org.lostnomore.backend.global.exception.ExpiredPeriodJwtException;
import org.lostnomore.backend.global.exception.InvalidJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String USER_ID_CLAIM = "userId";
    private static final String SUBJECT_VALUE = "user";
    private static final int MINUTE_IN_MILLISECONDS = 60 * 1000;
    private static final long DAYS_IN_MILLISECONDS = 24 * 60 * 60 * 1000L;
    private static final int ACCESS_TOKEN_EXPIRATION_MINUTE = 30;
    private static final int REFRESH_TOKEN_EXPIRATION_DAYS = 14;

    @Value("${security.jwt.token.secret-key}")
    private String secretKeyString;

    private SecretKey secretKey;

    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public UserTokenResponse createLoginToken(final String userId) {
        String accessToken = createAccessToken(userId);
        String refreshToken = createRefreshToken(userId);

        saveRefreshToken(userId, refreshToken);

        return new UserTokenResponse(
                accessToken,
                refreshToken
        );
    }

    public String createAccessToken(final String userId) {
        return createToken(userId, ACCESS_TOKEN_EXPIRATION_MINUTE * MINUTE_IN_MILLISECONDS);
    }

    public String createRefreshToken(final String userId) {
        return createToken(userId, REFRESH_TOKEN_EXPIRATION_DAYS * DAYS_IN_MILLISECONDS);
    }

    public void validateToken(final String token) {
        try {
            parseToken(token);
        } catch (final ExpiredJwtException e) {
            throw new ExpiredPeriodJwtException(EXPIRED_PERIOD_TOKEN);
        } catch (final JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException(INVALID_TOKEN);
        }
    }

    public String getUserIdOnToken(final String token) {
        return parseToken(token)
                .getBody()
                .get(USER_ID_CLAIM, String.class);
    }

    public boolean compareRefreshToken(final String userId, final String refreshToken) {
        final String storedRefreshToken = redisTemplate.opsForValue().get(userId);
        if (storedRefreshToken == null) {
            return false;
        }
        return storedRefreshToken.equals(refreshToken);
    }

    public String regenerateAccessToken(final String userId) {
        return createAccessToken(userId);
    }

    public String regenerateRefreshToken(final String userId) {
        String refreshToken = createRefreshToken(userId);
        saveRefreshToken(userId, refreshToken);
        return refreshToken;
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(userId);
    }

    private void saveRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(userId, refreshToken, REFRESH_TOKEN_EXPIRATION_DAYS, TimeUnit.DAYS);
    }

    private String createToken(final String userId, final long expireLength) {
        final Claims claims = Jwts.claims().setSubject(SUBJECT_VALUE);
        claims.put(USER_ID_CLAIM, userId);

        Date now = new Date();
        Date validity = new Date(now.getTime() + expireLength);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Jws<Claims> parseToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}