package org.lostnomore.backend.subscribe.dto.request;

public record SubscribeUpdateDto(
        String keyword,
        String category,
        String region
) {
}
