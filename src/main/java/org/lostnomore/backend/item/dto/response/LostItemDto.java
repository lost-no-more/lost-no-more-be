package org.lostnomore.backend.item.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.lostnomore.backend.item.domain.LostItem;

import java.time.LocalDate;

public record LostItemDto(
        Long id,
        String name,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        String category,
        String location,
        Double latitude,
        Double longitude,
        String imageUrl
) {
    public static LostItemDto from(LostItem lostItem) {
        return new LostItemDto(
                lostItem.getId(),
                lostItem.getName(),
                lostItem.getDate(),
                lostItem.getCategory().getName(),
                lostItem.getLocation().getName(),
                lostItem.getLocation().getLatitude(),
                lostItem.getLocation().getLongitude(),
                lostItem.getImage()
        );
    }
}
