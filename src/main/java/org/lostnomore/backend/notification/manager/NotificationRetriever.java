package org.lostnomore.backend.notification.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.UserNotificationErrorCode;
import org.lostnomore.backend.notification.domain.UserNotification;
import org.lostnomore.backend.notification.repository.UserNotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationRetriever {

    private final UserNotificationRepository userNotificationRepository;

    public List<UserNotification> findByUserId(final Long userId) {
        return userNotificationRepository.findByUserId(userId);
    }

    public UserNotification findById(final Long alarmId) {
        return userNotificationRepository.findById(alarmId)
                .orElseThrow(() -> new BusinessException(UserNotificationErrorCode.USER_NOTIFICATION_NOT_FOUND));
    }
}
