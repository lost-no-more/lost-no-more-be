package org.lostnomore.backend.auth.provider;

import static org.lostnomore.backend.global.exception.code.AuthErrorCode.EXPIRED_PERIOD_ACCESS_TOKEN;
import static org.lostnomore.backend.global.exception.code.AuthErrorCode.EXPIRED_PERIOD_REFRESH_TOKEN;
import static org.lostnomore.backend.global.exception.code.AuthErrorCode.INVALID_ACCESS_TOKEN;
import static org.lostnomore.backend.global.exception.code.AuthErrorCode.INVALID_REFRESH_TOKEN;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.lostnomore.backend.auth.oauth.dto.UserTokenResponse;
import org.lostnomore.backend.global.exception.ExpiredPeriodJwtException;
import org.lostnomore.backend.global.exception.InvalidJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpireLength;
    private final long refreshTokenExpireLength;

    public JwtTokenProvider(@Value("${security.jwt.token.secret-key}") final String secretKey,
                            @Value("${security.jwt.token.access-expire-length}") final long accessTokenExpireLength,
                            @Value("${security.jwt.token.refresh-expire-length}") final long refreshTokenExpireLength) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpireLength = accessTokenExpireLength;
        this.refreshTokenExpireLength = refreshTokenExpireLength;
    }

    public UserTokenResponse createLoginToken(final Long payload) {
        String accessToken = createAccessToken(payload);
        String refreshToken = createRefreshToken(payload);

        return new UserTokenResponse(accessToken, refreshToken);
    }

    public String createAccessToken(final Long payload) {
        return createToken(payload, accessTokenExpireLength);
    }

    public String createRefreshToken(final Long payload) {
        return createToken(payload, refreshTokenExpireLength);
    }

    private String createToken(final Long payload, final Long expireLength) {
        final Claims claims = Jwts.claims().setSubject("user");
        claims.put("userId", payload);

        Date now = new Date();
        Date validity = new Date(now.getTime() + expireLength);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateAccessToken(final String accessToken) {
        try {
            parseToken(accessToken);
        } catch (final ExpiredJwtException e) {
            throw new ExpiredPeriodJwtException(EXPIRED_PERIOD_ACCESS_TOKEN);
        } catch (final JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException(INVALID_ACCESS_TOKEN);
        }
    }

    public void validateRefreshToken(final String refreshToken) {
        try {
            parseToken(refreshToken);
        } catch (final ExpiredJwtException e) {
            throw new ExpiredPeriodJwtException(EXPIRED_PERIOD_REFRESH_TOKEN);
        } catch (final JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException(INVALID_REFRESH_TOKEN);
        }
    }

    public Long getSubject(final String token) {
        return parseToken(token)
                .getBody()
                .get("userId", Long.class);
    }

    private Jws<Claims> parseToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
    public Long getExpiredSubject(final String token) {
        Claims claims = getClaimsFromExpiredToken(token);
        return claims.get("userId", Long.class);
    }

    private Claims getClaimsFromExpiredToken(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean isValidRefreshAndInvalidAccess(final String refreshToken, final String accessToken) {
        validateRefreshToken(refreshToken);
        try {
            validateAccessToken(accessToken);
        } catch (final ExpiredPeriodJwtException e) {
            return true;
        }
        return false;
    }

    public String regenerateAccessToken(final Long subject) {
        return createToken(subject, accessTokenExpireLength);
    }

    public String regenerateRefreshToken(final Long subject) {
        return createToken(subject, refreshTokenExpireLength);
    }
}