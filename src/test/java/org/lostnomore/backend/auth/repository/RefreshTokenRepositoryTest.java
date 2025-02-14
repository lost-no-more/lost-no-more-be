package org.lostnomore.backend.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.auth.domain.RefreshToken;
import org.lostnomore.backend.common.RepositoryTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryTest extends RepositoryTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void 리프레시_토큰이_존재할_경우_userId를_반환한다() {
        // given
        RefreshToken refreshToken = new RefreshToken("refresh-token", 1L);
        when(refreshTokenRepository.findById(refreshToken.getToken())).thenReturn(Optional.of(refreshToken));

        // when
        RefreshToken token = refreshTokenRepository.findById(refreshToken.getToken()).get();

        // then
        assertThat(token.getUserId()).isEqualTo(refreshToken.getUserId());
    }
}