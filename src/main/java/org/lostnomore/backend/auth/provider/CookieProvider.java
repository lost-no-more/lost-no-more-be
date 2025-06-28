package org.lostnomore.backend.auth.provider;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

    private static final long DAYS_IN_MILLISECONDS = 24 * 60 * 60 * 1000L;
    private static final int REFRESH_TOKEN_EXPIRATION_DAYS = 14;

    public void createCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh-token", refreshToken)
                .maxAge(REFRESH_TOKEN_EXPIRATION_DAYS * DAYS_IN_MILLISECONDS)
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());
    }
}
