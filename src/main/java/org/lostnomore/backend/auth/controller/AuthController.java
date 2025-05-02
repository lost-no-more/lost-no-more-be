package org.lostnomore.backend.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.oauth.dto.UserTokenResponse;
import org.lostnomore.backend.auth.provider.CookieProvider;
import org.lostnomore.backend.auth.service.AuthService;
import org.lostnomore.backend.global.LoginUser;
import org.lostnomore.backend.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieProvider cookieProvider;

    @GetMapping("/code")
    public ResponseEntity<ResponseDto<String>> getCodeLink() {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(authService.getCodeLink()));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<String>> oauth(@RequestParam String code, HttpServletResponse response) {
        UserTokenResponse userTokenResponse = authService.oauthLogin(code);
        cookieProvider.createCookie(userTokenResponse.refreshToken(), response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(userTokenResponse.accessToken()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ResponseDto<String>> reissue(@CookieValue("refresh-token") final String refreshToken,
                                                       HttpServletResponse response) {
        UserTokenResponse userTokenResponse = authService.reissue(refreshToken);
        cookieProvider.createCookie(userTokenResponse.refreshToken(), response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(userTokenResponse.accessToken()));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ResponseDto<Void>> logout(@LoginUser final Long userId) {
        authService.logout(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseDto.success());
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ResponseDto<Void>> withdraw(@LoginUser final Long userId) {
        authService.withdraw(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseDto.success());
    }
}