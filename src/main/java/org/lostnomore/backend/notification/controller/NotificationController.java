package org.lostnomore.backend.notification.controller;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.LoginUser;
import org.lostnomore.backend.global.dto.ResponseDto;
import org.lostnomore.backend.notification.dto.response.UserNotificationsDto;
import org.lostnomore.backend.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/alarm")
    public ResponseEntity<ResponseDto<UserNotificationsDto>> getAlarms(
            @LoginUser Long userId
    ) {
        return ResponseEntity.ok().body(ResponseDto.success(notificationService.getAlarms(userId)));
    }
}
