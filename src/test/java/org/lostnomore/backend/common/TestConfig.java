package org.lostnomore.backend.common;

import org.lostnomore.backend.auth.provider.JwtTokenProvider;
import org.lostnomore.backend.auth.util.BearerAuthorizationExtractor;
import org.lostnomore.backend.global.resolver.AccessTokenArgumentResolver;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return Mockito.mock(JwtTokenProvider.class);
    }

    @Bean
    public BearerAuthorizationExtractor bearerAuthorizationExtractor() {
        return Mockito.mock(BearerAuthorizationExtractor.class);
    }

    @Bean
    public AccessTokenArgumentResolver accessTokenArgumentResolver() {
        return Mockito.mock(AccessTokenArgumentResolver.class);
    }
}