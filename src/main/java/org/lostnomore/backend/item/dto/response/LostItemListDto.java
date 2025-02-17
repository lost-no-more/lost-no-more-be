package org.lostnomore.backend.item.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.lostnomore.backend.item.domain.LostItem;

import java.time.LocalDate;

public record LostItemListDto(
        Long lostItemId,
        String name,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        String location,
        String category,
        String imageUrl
) {
    public static LostItemListDto from(LostItem lostItem) {
        return new LostItemListDto(
                lostItem.getId(),
                lostItem.getName(),
                lostItem.getDate(),
                lostItem.getLocation().getName(),
                lostItem.getCategory().getName(),
                lostItem.getImage()
        );
    }
}
