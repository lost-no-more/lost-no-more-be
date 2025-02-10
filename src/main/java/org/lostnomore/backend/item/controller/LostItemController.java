package org.lostnomore.backend.item.controller;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.dto.ResponseDto;
import org.lostnomore.backend.item.dto.response.ItemsCountDto;
import org.lostnomore.backend.item.service.LostItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LostItemController {

    private final LostItemService lostItemService;

    @GetMapping("items/count")
    public ResponseEntity<ResponseDto<ItemsCountDto>> getItemsCount () {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(lostItemService.getItemsCount()));
    }

}
