package org.lostnomore.backend.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.common.ServiceTest;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends ServiceTest {

    @InjectMocks
    private UserService userService;

    @Test
    void 신규회원_가입시_이름_추출() {
        // given
        User register = userService.register("test@exampl.com", SocialType.GOOGLE);

        // when & then
        assertThat(register.getName()).isEqualTo("test");
    }

}