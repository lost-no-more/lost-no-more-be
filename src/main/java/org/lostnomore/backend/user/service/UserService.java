package org.lostnomore.backend.user.service;

import org.lostnomore.backend.user.domain.SocialType;
import org.lostnomore.backend.user.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public User register(String email, SocialType socialType) {
        return User.builder()
                .email(email)
                .name(getName(email))
                .socialType(socialType)
                .build();
    }

    private String getName(String email) {
        return email.split("@")[0];
    }
}
