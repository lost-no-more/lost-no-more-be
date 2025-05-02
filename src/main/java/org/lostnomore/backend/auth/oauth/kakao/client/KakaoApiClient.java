package org.lostnomore.backend.auth.oauth.kakao.client;

import org.lostnomore.backend.auth.oauth.kakao.dto.KakaoUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@ComponentScan
@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {

    @GetMapping(value = "/v2/user/me")
    KakaoUserResponse getUserInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);

    @PostMapping(value = "/v1/user/unlink", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void withdraw(@RequestHeader(HttpHeaders.AUTHORIZATION) String key,
                  @RequestParam("target_id_type") String type,
                  @RequestParam("target_id") Long id);
}
