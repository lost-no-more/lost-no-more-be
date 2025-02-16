package org.lostnomore.backend.notification.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.notification.dto.response.UserNotificationsDto;
import org.lostnomore.backend.notification.manager.NotificationRetriever;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRetriever notificationRetriever;

    public UserNotificationsDto getAlarms(final Long userId) {
        return UserNotificationsDto.from(notificationRetriever.findByUserId(userId));
    }
}
