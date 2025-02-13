package org.lostnomore.backend.item.dto.response;

import org.lostnomore.backend.item.domain.LostItem;

import java.util.List;

public record LostItemsListDto(
        int totalCount,
        List<LostItemListDto> lostItemList
) {
    public static LostItemsListDto from (List<LostItem> lostItems) {
        return new LostItemsListDto(
                lostItems.size(),
                lostItems.stream()
                        .map(LostItemListDto::from)
                        .toList()
        );
    }

}
