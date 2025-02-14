package org.lostnomore.backend.notification.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.notification.manager.UserNotificationRemover;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserNotificationRemover userNotificationRemover;

    public void deleteByUserId(Long userId) {
        userNotificationRemover.deleteByUserId(userId);
    }
}
