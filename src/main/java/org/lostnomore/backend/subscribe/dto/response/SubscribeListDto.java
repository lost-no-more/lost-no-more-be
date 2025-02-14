package org.lostnomore.backend.subscribe.dto.response;

import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.dto.response.LostItemListDto;

import java.time.LocalDate;
import java.util.List;

public record SubscribeListDto(
        int totalCount,
        List<LostItemListDto> lostItems,
        LocalDate nextCursorDate,
        Long nextCursorId
) {
    public static SubscribeListDto from(List<LostItem> lostItems, LocalDate nextCursorDate, Long nextCursorId) {
        return new SubscribeListDto(
                lostItems.size(),
                lostItems.stream()
                        .map(LostItemListDto::from)
                        .toList(),
                nextCursorDate,
                nextCursorId
        );
    }
}
