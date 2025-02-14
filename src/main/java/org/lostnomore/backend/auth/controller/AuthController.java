package org.lostnomore.backend.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.dto.UserTokenDto;
import org.lostnomore.backend.auth.provider.CookieProvider;
import org.lostnomore.backend.auth.service.AuthService;
import org.lostnomore.backend.global.LoginUser;
import org.lostnomore.backend.global.dto.LoginUserDto;
import org.lostnomore.backend.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieProvider cookieProvider;

    @GetMapping("/oauth/{provider}/code")
    public ResponseEntity<ResponseDto> getCodeLink(@PathVariable String provider) {
        String loginLink = authService.getCodeLink(provider);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(loginLink));
    }

    @PostMapping("/oauth/{provider}/login")
    public ResponseEntity<ResponseDto> oauth(@PathVariable String provider, @RequestParam String code,
                                             HttpServletResponse response) {
        UserTokenDto userTokenDto = authService.oauthLogin(provider, code);
        cookieProvider.createCookie(userTokenDto.getRefreshToken(), response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(userTokenDto.getAccessToken()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ResponseDto> reissue(@CookieValue("refresh-token") final String refreshToken,
                                             @RequestHeader("Authorization") final String authorizationHeader,
                                             HttpServletResponse response) {

        UserTokenDto userTokenDto = authService.reissue(refreshToken, authorizationHeader);
        cookieProvider.createCookie(userTokenDto.getRefreshToken(), response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(userTokenDto.getAccessToken()));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ResponseDto> logout(@LoginUser final Long userId,
                                       @CookieValue("refresh-token") final String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success());
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ResponseDto> withdraw(@LoginUser final Long userId,
                                       @CookieValue("refresh-token") final String refreshToken) {
        authService.withdraw(userId, refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success());
    }
}
