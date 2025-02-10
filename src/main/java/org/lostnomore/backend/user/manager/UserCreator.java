package org.lostnomore.backend.user.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreator {

    private final UserRepository userRepository;

    public void save(final User user) {
        userRepository.save(user);
    }
}
