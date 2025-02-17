package org.lostnomore.backend.item.dto.response;

import org.lostnomore.backend.item.elastic.LostItemDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public record LostItemsSearchDto(
        List<LostItemSearchDto> lostItems
) {
    public static LostItemsSearchDto from(SearchHits<LostItemDocument> lostItems) {
        return new LostItemsSearchDto(
                lostItems.stream()
                        .map(hit -> new LostItemSearchDto(
                        hit.getContent().getId(),
                        hit.getContent().getLocation().getLat(),
                        hit.getContent().getLocation().getLon()
                ))
                        .toList()
        );
    }

    public record LostItemSearchDto(
            Long lostItemId,
            Double latitude,
            Double longitude
    ) {
        public static LostItemSearchDto from (LostItemDocument lostItem) {
            return new LostItemSearchDto(
                    lostItem.getId(),
                    lostItem.getLocation().getLat(),
                    lostItem.getLocation().getLon()
            );
        }
    }
}
