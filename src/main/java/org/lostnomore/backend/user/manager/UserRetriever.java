package org.lostnomore.backend.user.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.UserErrorCode;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.global.exception.code.AuthErrorCode;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRetriever {

    private final UserRepository userRepository;

    public User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.NON_EXISTENT_USER));
    }

    public User findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId)
                .orElse(null);
    }

    public User findById(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}
