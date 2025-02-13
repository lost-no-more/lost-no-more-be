package org.lostnomore.backend.auth.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.lostnomore.backend.auth.dto.UserTokenDto;
import org.lostnomore.backend.auth.provider.CookieProvider;
import org.lostnomore.backend.auth.provider.OAuthProvider;
import org.lostnomore.backend.auth.service.AuthService;
import org.lostnomore.backend.common.ControllerTest;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;
import org.lostnomore.backend.global.exception.code.BusinessErrorCode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTest {

    @Mock
    private OAuthProvider oAuthProvider;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieProvider cookieProvider;

    @InjectMocks
    private AuthController authController;

    private final static String REFRESH_TOKEN = "refreshToken";
    private final static String ACCESS_TOKEN = "accessToken";

    @Test
    void OAuth_소셜_로그인을_위한_링크와_상태코드_200을_반환() throws Exception {
        // given
        String provider = "google";
        String mockLoginLink = "https://mocked-auth-url.com";
        given(authService.getCodeLink(provider)).willReturn(mockLoginLink);

        // when & then
        mockMvc.perform(get("/auth/oauth/{provider}/code", provider))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value(mockLoginLink))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void OAuth_소셜_로그인을_위한_링크_api_호출_시_존재하지_않는_provider면_오류() throws Exception {
        // given
        String provider = "nonExistentProvider";
        given(authService.getCodeLink(provider)).willThrow(new BusinessException(AuthErrorCode.NON_EXISTENT_SOCIAL_TYPE));

        // when & then
        mockMvc.perform(get("/auth/oauth/{provider}/code", provider))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.error.code").value(AuthErrorCode.NON_EXISTENT_SOCIAL_TYPE.getHttpStatus().value()))
                .andExpect(jsonPath("$.error.message").value(AuthErrorCode.NON_EXISTENT_SOCIAL_TYPE.getMessage()));
    }

    @Test
    void OAuth_로그인_api_호출_시_code_없으면_오류() throws Exception {
        // given
        String provider = "google";

        // when & then
        mockMvc.perform(post("/auth/oauth/{provider}/login", provider))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.error.code").value(BusinessErrorCode.MISSING_REQUIRED_PARAMETER.getHttpStatus().value()))
                .andExpect(jsonPath("$.error.message").value(BusinessErrorCode.MISSING_REQUIRED_PARAMETER.getMessage()));
    }

    @Test
    void OAuth_로그인_api_호출_시_JWT_토큰_발급() throws Exception {
        // given
        String provider = "google";
        String code = "test";
        UserTokenDto userTokenDto = new UserTokenDto(ACCESS_TOKEN, REFRESH_TOKEN);

        // when
        when(authService.oauthLogin(anyString(), anyString())).thenReturn(userTokenDto);

        // then
        mockMvc.perform(post("/auth/oauth/{provider}/login", provider)
                        .param("code", code))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void 리프레시_토큰을_통해_새로운_엑세스_토큰을_발급하면_상태코드_200을_반환() throws Exception {
        // given
        UserTokenDto newUserTokenDto = new UserTokenDto("newAccessToken", REFRESH_TOKEN);
        when(authService.reissue(anyString(), anyString())).thenReturn(newUserTokenDto);

        // when & then
        mockMvc.perform(post("/auth/reissue")
                        .cookie(new Cookie("refresh-token", REFRESH_TOKEN))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value("newAccessToken"))
                .andExpect(jsonPath("$.error").doesNotExist());
    }
}