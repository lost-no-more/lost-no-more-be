package org.lostnomore.backend.item.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record LostItemCreateDto(
        String name,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        Long categoryId,
        String location,
        String color,
        String image
) {
}
