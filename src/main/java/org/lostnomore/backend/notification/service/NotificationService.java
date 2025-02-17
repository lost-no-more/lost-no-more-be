package org.lostnomore.backend.notification.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.UserNotificationErrorCode;
import org.lostnomore.backend.notification.domain.UserNotification;
import org.lostnomore.backend.notification.dto.response.UserNotificationsDto;
import org.lostnomore.backend.notification.manager.NotificationEditor;
import org.lostnomore.backend.notification.manager.NotificationRemover;
import org.lostnomore.backend.notification.manager.NotificationRetriever;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRetriever notificationRetriever;
    private final NotificationEditor notificationEditor;
    private final NotificationRemover notificationRemover;

    @Transactional(readOnly = true)
    public UserNotificationsDto getAlarms(final Long userId) {
        return UserNotificationsDto.from(notificationRetriever.findByUserId(userId));
    }

    @Transactional
    public void readAlarm(
            final Long userId,
            final Long alarmId) {
        UserNotification userNotification = notificationRetriever.findById(alarmId);
        validateUserNotificationOwner(userId, userNotification);

        notificationEditor.updateReadStatus(userNotification);
    }

    @Transactional
    public void deleteAlarm(
            final Long userId,
            final Long alarmId) {
        UserNotification userNotification = notificationRetriever.findById(alarmId);
        validateUserNotificationOwner(userId, userNotification);

        notificationRemover.deleteById(alarmId);
    }

    private void validateUserNotificationOwner(Long userId, UserNotification userNotification) {
        if (!userNotification.getUser().getId().equals(userId)) {
            throw new BusinessException(UserNotificationErrorCode.USER_NOTIFICATION_FORBIDDEN);
        }
    }
}
