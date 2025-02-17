package org.lostnomore.backend.notification.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.notification.domain.UserNotification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEditor {

    public void updateReadStatus(final UserNotification userNotification) {
        userNotification.updateReadStatus();
    }
}
