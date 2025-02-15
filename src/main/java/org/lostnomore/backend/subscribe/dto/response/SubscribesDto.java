package org.lostnomore.backend.subscribe.dto.response;

import org.lostnomore.backend.subscribe.domain.Subscribe;

import java.util.List;

public record SubscribesDto(
        int totalCount,
        List<SubscribeDto> subscribes
) {
    public static SubscribesDto from (List<Subscribe> subscribes) {
        return new SubscribesDto(
                subscribes.size(),
                subscribes.stream()
                        .map(SubscribeDto::from)
                        .toList()
        );
    }

    public record SubscribeDto(
            Long subscribeId,
            String keyword,
            String category,
            String region
    ) {
        public static SubscribeDto from(Subscribe subscribe) {
            return new SubscribeDto(
                    subscribe.getId(),
                    subscribe.getKeyword(),
                    subscribe.getCategory().getName(),
                    subscribe.getRegion()
            );
        }
    }
}
