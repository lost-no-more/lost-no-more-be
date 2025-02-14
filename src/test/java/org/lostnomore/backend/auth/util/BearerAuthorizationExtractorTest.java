package org.lostnomore.backend.auth.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.global.exception.BusinessException;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BearerAuthorizationExtractorTest {

    @InjectMocks
    private BearerAuthorizationExtractor bearerAuthorizationExtractor;

    @Test
    void 헤더에_엑세스_토큰_형식이_잘못_될_시_오류() {
        // given & when & then
        assertThatThrownBy(() -> bearerAuthorizationExtractor.extractAccessToken("header"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void 헤더에_엑세스_토큰_추출() {
        // given & when & then
        assertThat(bearerAuthorizationExtractor.extractAccessToken("Bearer accessToken")).isEqualTo("accessToken");
    }
}