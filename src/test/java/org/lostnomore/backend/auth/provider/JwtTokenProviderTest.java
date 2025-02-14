package org.lostnomore.backend.auth.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.lostnomore.backend.global.exception.ExpiredPeriodJwtException;
import org.lostnomore.backend.global.exception.InvalidJwtException;

class JwtTokenProviderTest {

    private static final String JWT_SECRET_KEY = "A".repeat(32); // Secret Key는 최소 32바이트 이상이어야함.
    private static final int JWT_ACCESS_TOKEN_EXPIRE_LENGTH = 3600;
    private static final int JWT_REFRESH_TOKEN_EXPIRE_LENGTH = 3600;
    private static final Long PAYLOAD = 1L;

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(JWT_SECRET_KEY,
            JWT_ACCESS_TOKEN_EXPIRE_LENGTH, JWT_REFRESH_TOKEN_EXPIRE_LENGTH);

    @Test
    void 엑세스_토큰을_생성() {
        // given & when
        String accessToken = jwtTokenProvider.createAccessToken(PAYLOAD);

        // then
        assertThat(accessToken.split("\\.")).hasSize(3);
    }

    @Test
    void 리프레시_토큰을_생성() {
        // given & when
        String refreshToken = jwtTokenProvider.createRefreshToken(PAYLOAD);

        // then
        assertThat(refreshToken.split("\\.")).hasSize(3);
    }

    @Test
    void 토큰의_Payload_검증() {
        // given
        String token = jwtTokenProvider.createAccessToken(PAYLOAD);

        // when
        Long userId = jwtTokenProvider.getSubject(token);

        // then
        assertThat(userId).isEqualTo(PAYLOAD);
    }

    @Test
    void 엑세스_토큰을_검증하여_만료된_경우_예외() {
        // given
        JwtTokenProvider tokenProvider = new JwtTokenProvider(JWT_SECRET_KEY, 0, 10000000);
        String expiredAccessToken = tokenProvider.createAccessToken(PAYLOAD);

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateAccessToken(expiredAccessToken))
                .isInstanceOf(ExpiredPeriodJwtException.class);
    }

    @Test
    void 리프레시_토큰을_검증하여_만료된_경우_예외() {
        // given
        JwtTokenProvider tokenProvider = new JwtTokenProvider(JWT_SECRET_KEY, 10000000, 0);
        String expiredRefreshToken = tokenProvider.createRefreshToken(PAYLOAD);

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateRefreshToken(expiredRefreshToken))
                .isInstanceOf(ExpiredPeriodJwtException.class);
    }

    @Test
    void 잘못된_토큰_양식일_경우_예외() {
        // given
        String wrongToken = "wrong-token";

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateAccessToken(wrongToken))
                .isInstanceOf(InvalidJwtException.class);
    }
}