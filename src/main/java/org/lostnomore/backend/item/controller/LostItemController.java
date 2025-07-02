package org.lostnomore.backend.item.controller;

import java.time.LocalDate;
import java.util.List;

import org.lostnomore.backend.global.dto.ResponseDto;
import org.lostnomore.backend.item.dto.request.LostItemCreateDto;
import org.lostnomore.backend.item.dto.response.ItemsCountDto;
import org.lostnomore.backend.item.dto.response.LostItemDto;
import org.lostnomore.backend.item.dto.response.LostItemsListDto;
import org.lostnomore.backend.item.dto.response.LostItemsSearchDto;
import org.lostnomore.backend.item.service.LostItemService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LostItemController {

    private final LostItemService lostItemService;

    @GetMapping("/items/count")
    public ResponseEntity<ResponseDto<ItemsCountDto>> getItemsCount () {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(lostItemService.getItemsCount()));
    }

    @PostMapping("/items")
    public ResponseEntity<ResponseDto<Void>> saveLostItem(
            @RequestBody final LostItemCreateDto request
    ) {
        lostItemService.saveLostItem(request);
        return ResponseEntity.ok(ResponseDto.success());
    }

    @GetMapping("/items/search/map")
    public ResponseEntity<ResponseDto<LostItemsSearchDto>> searchLostItems(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date_start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date_end,
            @RequestParam Double top_left_lat,
            @RequestParam Double top_left_lon,
            @RequestParam Double bottom_right_lat,
            @RequestParam Double bottom_right_lon,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String region
    ) {
        return ResponseEntity.ok(ResponseDto.success(lostItemService.searchLostItems(
                date_start, date_end,
                top_left_lat, top_left_lon,
                bottom_right_lat, bottom_right_lon,
                keyword,
                category,
                region
        )));
    }

    @GetMapping("/items/search/list")
    public ResponseEntity<ResponseDto<LostItemsListDto>> searchLostItemsList(
            @RequestParam final List<Long> lostItemIds
    ) {
        return ResponseEntity.ok().body(ResponseDto.success(lostItemService.searchLostItemsList(lostItemIds)));
    }

    @GetMapping("/items/search/{lostItemId}")
    public ResponseEntity<ResponseDto<LostItemDto>> getLostItem (
            @PathVariable final Long lostItemId
    ) {
        return ResponseEntity.ok().body(ResponseDto.success(lostItemService.getLostItem(lostItemId)));
    }
}
