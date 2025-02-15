package org.lostnomore.backend.global.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.auth.provider.JwtTokenProvider;
import org.lostnomore.backend.auth.util.BearerAuthorizationExtractor;
import org.lostnomore.backend.global.interceptor.AccessTokenInterceptor;
import org.lostnomore.backend.global.resolver.AccessTokenArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtTokenProvider jwtTokenProvider;
    private final BearerAuthorizationExtractor bearerAuthorizationExtractor;
    private final AccessTokenArgumentResolver accessTokenArgumentResolver;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccessTokenInterceptor(jwtTokenProvider, bearerAuthorizationExtractor))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",
                        "/test/**",
                        "/items/search/**",
                        "/items/count"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(accessTokenArgumentResolver);
    }
}