package org.lostnomore.backend.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lostnomore.backend.auth.provider.JwtTokenProvider;
import org.lostnomore.backend.auth.util.BearerAuthorizationExtractor;
import org.lostnomore.backend.global.resolver.AccessTokenArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ActiveProfiles("test")
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    protected BearerAuthorizationExtractor bearerAuthorizationExtractor;

    @MockitoBean
    protected AccessTokenArgumentResolver accessTokenArgumentResolver;
}