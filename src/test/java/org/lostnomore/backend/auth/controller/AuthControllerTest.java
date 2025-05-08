package org.lostnomore.backend.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lostnomore.backend.auth.oauth.dto.UserTokenResponse;
import org.lostnomore.backend.auth.provider.CookieProvider;
import org.lostnomore.backend.auth.service.AuthService;
import org.lostnomore.backend.common.ControllerTest;
import org.lostnomore.backend.global.dto.ResponseDto;
import org.lostnomore.backend.global.exception.InvalidJwtException;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieProvider cookieProvider;


    @Test
    @DisplayName("OAuth 로그인 성공 테스트")
    void oauth_login_success() throws Exception {
        String code = "oauth-auth-code";
        String accessToken = "access-token-value";
        String refreshToken = "refresh-token-value";
        UserTokenResponse userTokenResponse = new UserTokenResponse(accessToken, refreshToken);

        when(authService.oauthLogin(anyString())).thenReturn(userTokenResponse);
        doNothing().when(cookieProvider).createCookie(anyString(), any(HttpServletResponse.class));

        ResultActions result = mockMvc.perform(
                post("/auth/login")
                        .param("code", code)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value(accessToken));

        ResponseDto<String> expectedResponse = ResponseDto.success(accessToken);
        String expectedJson = objectMapper.writeValueAsString(expectedResponse);

        result.andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("토큰 재발급 성공 테스트")
    void reissue_success() throws Exception {
        String refreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        UserTokenResponse userTokenResponse = new UserTokenResponse(newAccessToken, newRefreshToken);

        when(authService.reissue(refreshToken)).thenReturn(userTokenResponse);
        doNothing().when(cookieProvider).createCookie(anyString(), any(HttpServletResponse.class));

        ResultActions result = mockMvc.perform(
                post("/auth/reissue")
                        .cookie(new jakarta.servlet.http.Cookie("refresh-token", refreshToken))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value(newAccessToken));

        ResponseDto<String> expectedResponse = ResponseDto.success(newAccessToken);
        String expectedJson = objectMapper.writeValueAsString(expectedResponse);

        result.andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_success() throws Exception {
        Long userId = 1L;

        doNothing().when(authService).logout(userId);

        when(accessTokenArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(userId);

        ResultActions result = mockMvc.perform(
                delete("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-token")
        ).andDo(print());

        result.andExpect(status().isNoContent())
                .andExpect(jsonPath("$.isSuccess").value(true));

        ResponseDto<Void> expectedResponse = ResponseDto.success();
        String expectedJson = objectMapper.writeValueAsString(expectedResponse);

        result.andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    void withdraw_success() throws Exception {
        Long userId = 1L;

        doNothing().when(authService).withdraw(userId);

        when(accessTokenArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(userId);

        ResultActions result = mockMvc.perform(
                delete("/auth/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-token")
        ).andDo(print());

        result.andExpect(status().isNoContent())
                .andExpect(jsonPath("$.isSuccess").value(true));

        ResponseDto<Void> expectedResponse = ResponseDto.success();
        String expectedJson = objectMapper.writeValueAsString(expectedResponse);

        result.andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("잘못된 코드일 시 OAuth 로그인 실패")
    void oauth_login_failure_invalid_code() throws Exception {
        String invalidCode = "invalid-oauth-code";

        when(authService.oauthLogin(invalidCode))
                .thenThrow(new IllegalArgumentException("유효하지 않은 인증 코드입니다."));

        ResultActions result = mockMvc.perform(
                post("/auth/login")
                        .param("code", invalidCode)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value(AuthErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰일 시 토큰 재발급 실패")
    void reissue_failure_invalid_refresh_token() throws Exception {
        String invalidRefreshToken = "invalid-refresh-token";

        when(authService.reissue(invalidRefreshToken))
                .thenThrow(new InvalidJwtException(AuthErrorCode.INVALID_TOKEN));

        ResultActions result = mockMvc.perform(
                post("/auth/reissue")
                        .cookie(new jakarta.servlet.http.Cookie("refresh-token", invalidRefreshToken))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").value(AuthErrorCode.INVALID_TOKEN.getMessage()));
    }

    @Test
    @DisplayName("로그아웃 실패")
    void logout_failure_service_error() throws Exception {
        doThrow(new IllegalArgumentException("로그아웃 처리 중 오류가 발생했습니다."))
                .when(authService).logout(any());

        ResultActions result = mockMvc.perform(
                delete("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value(AuthErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

    @Test
    @DisplayName("회원 탈퇴 실패")
    void withdraw_failure_forbidden() throws Exception {
        doThrow(new IllegalArgumentException("회원 탈퇴 중 오류가 발생했습니다."))
                .when(authService).withdraw(any());

        ResultActions result = mockMvc.perform(
                delete("/auth/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-token")
        ).andDo(print());

        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value(AuthErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}