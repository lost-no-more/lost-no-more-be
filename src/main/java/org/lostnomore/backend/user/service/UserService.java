package org.lostnomore.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.UserErrorCode;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public User register(Long providerId, String email) {
        return User.builder()
                .providerId(providerId)
                .email(email)
                .name(getName(email))
                .build();
    }

    private String getName(String email) {
        if (email == null || !email.contains("@")) {
            throw new BusinessException(UserErrorCode.INVALID_EMAIL);
        }
        return email.split("@")[0];
    }
}
