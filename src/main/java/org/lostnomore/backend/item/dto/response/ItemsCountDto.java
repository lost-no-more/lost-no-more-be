package org.lostnomore.backend.item.dto.response;

public record ItemsCountDto(
        int today,
        int total
) {
    public static ItemsCountDto of(int todayCount, int totalCount) {
        return new ItemsCountDto(
                todayCount,
                totalCount
        );
    }
}
