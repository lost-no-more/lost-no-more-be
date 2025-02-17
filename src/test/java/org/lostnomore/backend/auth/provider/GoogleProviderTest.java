package org.lostnomore.backend.auth.provider;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.global.exception.BusinessException;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleProviderTest {

    @InjectMocks
    private GoogleProvider googleProvider;

    @Test
    void 구글_회원탈퇴시_인가코드가_없으면_오류() {
        // when & then
        assertThatThrownBy(() -> googleProvider.unLink("test_provider_id", null))
                .isInstanceOf(BusinessException.class);
    }
}