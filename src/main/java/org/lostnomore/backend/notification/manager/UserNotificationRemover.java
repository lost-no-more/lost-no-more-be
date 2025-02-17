package org.lostnomore.backend.notification.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.notification.repository.UserNotificationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserNotificationRemover {

    private final UserNotificationRepository userNotificationRepository;

    public void deleteByUserId(Long userId) {
        userNotificationRepository.deleteByUserId(userId);
    }
}
