package org.lostnomore.backend.item.dto.response;

import org.lostnomore.backend.item.domain.LostItem;

import java.time.LocalDate;
import java.util.List;

public record RecentItemsDto(
        List<RecentItemDto> recentItems
) {
    public static RecentItemsDto from(List<LostItem> items) {
        return new RecentItemsDto(
                items.stream()
                        .map(RecentItemDto::from)
                        .toList()
        );
    }

    public record RecentItemDto(
            Long id,
            String name,
            LocalDate date,
            String location,
            String imageUrl
    ) {
        public static RecentItemDto from(LostItem item) {
            return new RecentItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDate(),
                    item.getLocation().getName(),
                    item.getImage()
            );
        }
    }
}
