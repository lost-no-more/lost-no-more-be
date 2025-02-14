package org.lostnomore.backend.auth.provider;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

    public static final int COOKIE_AGE_SECONDS = 1209600;

    public void createCookie(String refreshToken, HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("refresh-token", refreshToken)
                .maxAge(COOKIE_AGE_SECONDS)
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());
    }
}
