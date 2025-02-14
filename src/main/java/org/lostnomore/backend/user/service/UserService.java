package org.lostnomore.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.manager.UserRemover;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRemover userRemover;

    public User register(String providerId, String email, SocialType socialType) {
        return User.builder()
                .providerId(providerId)
                .email(email)
                .name(getName(email))
                .socialType(socialType)
                .build();
    }

    private String getName(String email) {
        return email.split("@")[0];
    }

    public void deleteByUserId(Long userId) {
        userRemover.deleteByUserId(userId);
    }
}
