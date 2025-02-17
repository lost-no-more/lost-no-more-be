package org.lostnomore.backend.subscribe.dto.request;

public record SubscribeCreateDto(
        String keyword,
        String category,
        String region
) {
}
