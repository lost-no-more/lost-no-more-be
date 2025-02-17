package org.lostnomore.backend.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class KakaoUserDto {

    @JsonProperty("id")
    Long id;

    @JsonProperty("kakao_account")
    KakaoAccount kakaoAccount;
}