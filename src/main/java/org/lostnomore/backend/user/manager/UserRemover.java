package org.lostnomore.backend.user.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRemover {

    private final UserRepository userRepository;

    public void deleteByUserId(Long userId) {
        userRepository.deleteByUserId(userId);
    }
}
