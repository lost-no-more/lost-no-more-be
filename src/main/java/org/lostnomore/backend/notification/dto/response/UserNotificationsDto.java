package org.lostnomore.backend.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.lostnomore.backend.notification.domain.UserNotification;

import java.time.LocalDate;
import java.util.List;

public record UserNotificationsDto(
        List<UserNotificationDto> alarms
) {
    public static UserNotificationsDto from(List<UserNotification> userNotifications) {
        return new UserNotificationsDto(
                userNotifications.stream()
                .map(UserNotificationDto::from)
                .toList()
        );
    }

    public record UserNotificationDto(
            Long alarmId,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate date,
            int totalCount,
            String keyword,
            boolean readStatus
    ) {
        public static UserNotificationDto from(UserNotification userNotification) {
            return new UserNotificationDto(
                    userNotification.getNotification().getId(),
                    userNotification.getNotification().getDate(),
                    userNotification.getNotification().getTotalCount(),
                    userNotification.getNotification().getKeyword(),
                    userNotification.getReadStatus()
            );
        }
    }
}
