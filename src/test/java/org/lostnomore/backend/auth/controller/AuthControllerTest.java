package org.lostnomore.backend.auth.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.auth.provider.OAuthProvider;
import org.lostnomore.backend.auth.service.AuthService;
import org.lostnomore.backend.common.ControllerTest;
import org.lostnomore.backend.global.exception.AuthException;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;
import org.lostnomore.backend.global.exception.code.CommonErrorCode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest extends ControllerTest {

    @Mock
    private OAuthProvider oAuthProvider;

    @MockitoBean
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

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
        given(authService.getCodeLink(provider)).willThrow(new AuthException(AuthErrorCode.NON_EXISTENT_SOCIAL_TYPE));

        // when & then
        mockMvc.perform(get("/auth/oauth/{provider}/code", provider))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.error.code").value(AuthErrorCode.NON_EXISTENT_SOCIAL_TYPE.getHttpStatus().value()))
                .andExpect(jsonPath("$.error.message").value(AuthErrorCode.NON_EXISTENT_SOCIAL_TYPE.getMessage()));
    }

    @Test
    void OAuth_로그인_성공하면_200을_반환() throws Exception {
        // given
        String provider = "google";
        String code = "validCode";
        String loginLink = "https://mocked-auth-url.com";
        given(authService.oauthLogin(provider, code)).willReturn(loginLink);

        // when & then
        mockMvc.perform(post("/auth/oauth/{provider}/login", provider)
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value(loginLink))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void OAuth_로그인_api_호출_시_code_없으면_오류() throws Exception {
        // given
        String provider = "google";

        // when & then
        mockMvc.perform(post("/auth/oauth/{provider}/login", provider))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.error.code").value(CommonErrorCode.MISSING_REQUIRED_PARAMETER.getHttpStatus().value()))
                .andExpect(jsonPath("$.error.message").value(CommonErrorCode.MISSING_REQUIRED_PARAMETER.getMessage()));
    }
}