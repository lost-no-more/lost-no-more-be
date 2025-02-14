package org.lostnomore.backend.subscribe.controller;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.dto.ResponseDto;
import org.lostnomore.backend.subscribe.dto.response.SubscribeListDto;
import org.lostnomore.backend.subscribe.service.SubscribeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscribeService;

    @GetMapping("/subscribe/list")
    public ResponseEntity<ResponseDto<SubscribeListDto>> getSubscribeList(
            final Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date_start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date_end,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate cursorDate,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok().body(ResponseDto.success(subscribeService.getSubscribeList(
                userId, date_start, date_end, keyword,
                cursorDate, cursorId, size
        )));
    }
}
