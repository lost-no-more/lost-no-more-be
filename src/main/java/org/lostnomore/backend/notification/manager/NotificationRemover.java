package org.lostnomore.backend.notification.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.notification.repository.UserNotificationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRemover {

    private final UserNotificationRepository userNotificationRepository;

    public void deleteById(final Long alarmId) {
        userNotificationRepository.deleteById(alarmId);
    }
}
