package org.lostnomore.backend.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lostnomore.backend.global.resolver.AccessTokenArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@Import(TestConfig.class)
@ActiveProfiles("test")
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AccessTokenArgumentResolver accessTokenArgumentResolver;
}