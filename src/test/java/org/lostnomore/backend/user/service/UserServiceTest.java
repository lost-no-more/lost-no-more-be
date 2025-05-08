package org.lostnomore.backend.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.UserErrorCode;
import org.lostnomore.backend.user.domain.User;

class UserServiceTest {

    private final UserService userService = new UserService();

    private final Long TEST_PROVIDER_ID = 1L;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_NAME = "test";

    @Test
    @DisplayName("사용자 등록 테스트 - 기본 케이스")
    void register_ShouldCreateUserWithCorrectInfo() {
        User user = userService.register(TEST_PROVIDER_ID, TEST_EMAIL);

        assertNotNull(user);
        assertEquals(TEST_PROVIDER_ID, user.getProviderId());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_NAME, user.getName());
    }

    @ParameterizedTest
    @DisplayName("다양한 이메일 형식에 대한 사용자 이름 추출 테스트")
    @CsvSource({
            "john.doe@example.com, john.doe",
            "simple@test.org, simple",
            "user-name@domain.com, user-name",
            "short@x.co, short",
            "very.long.email.address@company.subdomain.country, very.long.email.address"
    })
    void register_WithVariousEmailFormats_ShouldExtractCorrectName(String email, String expectedName) {
        User user = userService.register(TEST_PROVIDER_ID, email);

        assertEquals(expectedName, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(TEST_PROVIDER_ID, user.getProviderId());
    }

    @Test
    @DisplayName("빈 이메일로 사용자 등록 시 예외 테스트")
    void register_WithEmptyEmail_ShouldThrowException() {
        String emptyEmail = "";

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.register(TEST_PROVIDER_ID, emptyEmail));
        assertEquals(UserErrorCode.INVALID_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 형식이 잘못된 경우 예외 테스트")
    void register_WithInvalidEmail_ShouldThrowException() {
        String invalidEmail = "invalid-email";

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.register(TEST_PROVIDER_ID, invalidEmail));
        assertEquals(UserErrorCode.INVALID_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("@ 문자만 있는 이메일 테스트")
    void register_WithOnlyAtSymbol_ShouldReturnEmptyName() {
        String emailWithOnlyAt = "@domain.com";
        String expectedEmptyName = "";

        User user = userService.register(TEST_PROVIDER_ID, emailWithOnlyAt);

        assertEquals(expectedEmptyName, user.getName());
        assertEquals(emailWithOnlyAt, user.getEmail());
    }

    @Test
    @DisplayName("여러 @ 문자가 있는 이메일 테스트")
    void register_WithMultipleAtSymbols_ShouldExtractUntilFirstAt() {
        String multipleAtEmail = "user@company@domain.com";
        String expectedName = "user";

        User user = userService.register(TEST_PROVIDER_ID, multipleAtEmail);

        assertEquals(expectedName, user.getName());
        assertEquals(multipleAtEmail, user.getEmail());
    }
}