package org.lostnomore.backend.subscribe.controller;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.resolver.LoginUser;
import org.lostnomore.backend.global.dto.ResponseDto;
import org.lostnomore.backend.subscribe.dto.request.SubscribeCreateDto;
import org.lostnomore.backend.subscribe.dto.request.SubscribeUpdateDto;
import org.lostnomore.backend.subscribe.dto.response.RecentItemsDto;
import org.lostnomore.backend.subscribe.dto.response.SubscribeListDto;
import org.lostnomore.backend.subscribe.dto.response.SubscribesDto;
import org.lostnomore.backend.subscribe.service.SubscribeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscribeService;

    @GetMapping("/items/recent")
    public ResponseEntity<ResponseDto<RecentItemsDto>> getRecentItems (
            @LoginUser final Long userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(subscribeService.getRecentItems(userId)));
    }

    @GetMapping("/subscribe/list")
    public ResponseEntity<ResponseDto<SubscribeListDto>> getSubscribeList(
            @LoginUser final Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date_start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date_end,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate cursorDate,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok().body(ResponseDto.success(subscribeService.getSubscribeList(
                userId, date_start, date_end,
                keyword, category, region,
                cursorDate, cursorId, size
        )));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ResponseDto<Void>> createSubscribe(
            @LoginUser final Long userId,
            @RequestBody SubscribeCreateDto subscribeCreateDto
    ) {
        subscribeService.createSubscribe(userId, subscribeCreateDto);
        return ResponseEntity.ok().body(ResponseDto.success());
    }

    @GetMapping("/subscribe")
    public ResponseEntity<ResponseDto<SubscribesDto>> getSubscribes (
            @LoginUser final Long userId
    ) {
        return ResponseEntity.ok().body(ResponseDto.success(subscribeService.getSubscribes(userId)));
    }

    @DeleteMapping("/subscribe/{subscribeId}")
    public ResponseEntity<ResponseDto<Void>> deleteSubscribe (
            @LoginUser final Long userId,
            @PathVariable final Long subscribeId
    ) {
        subscribeService.deleteSubscribe(userId, subscribeId);
        return ResponseEntity.ok().body(ResponseDto.success());
    }

    @PutMapping("/subscribe/{subscribeId}")
    public ResponseEntity<ResponseDto<Void>> updateSubscribe (
            @LoginUser final Long userId,
            @PathVariable final Long subscribeId,
            @RequestBody final SubscribeUpdateDto subscribeUpdateDto
    ) {
        subscribeService.updateSubscribe(userId, subscribeId, subscribeUpdateDto);
        return ResponseEntity.ok().body(ResponseDto.success());
    }
}
