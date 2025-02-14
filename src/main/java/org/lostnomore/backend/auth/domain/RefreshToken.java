package org.lostnomore.backend.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 1210000000)
public class RefreshToken {

    @Id
    private String token;

    private Long userId;
}