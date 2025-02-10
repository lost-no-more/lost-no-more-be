package org.lostnomore.backend.auth.controller;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.service.AuthService;
import org.lostnomore.backend.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/oauth/{provider}/code")
    public ResponseEntity<ResponseDto> getCodeLink(@PathVariable String provider) {
        String loginLink = authService.getCodeLink(provider);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(loginLink));
    }

    @PostMapping("/oauth/{provider}/login")
    public ResponseEntity<ResponseDto> oauth(@PathVariable String provider, @RequestParam String code) {
        String loginLink = authService.oauthLogin(provider, code);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(loginLink));
    }
}
