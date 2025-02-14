package org.lostnomore.backend.subscribe.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.elastic.LostItemDocument;
import org.lostnomore.backend.item.elastic.LostItemSearchService;
import org.lostnomore.backend.item.manager.LostItemRetriever;
import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.lostnomore.backend.subscribe.dto.response.SubscribeListDto;
import org.lostnomore.backend.subscribe.manager.SubscribeRetriever;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRetriever subscribeRetriever;
    private final LostItemSearchService lostItemSearchService;
    private final LostItemRetriever lostItemRetriever;

    @Transactional(readOnly = true)
    public SubscribeListDto getSubscribeList(
            final Long userId,
            final LocalDate dateStart, final LocalDate dateEnd,
            final String keyword,
            final LocalDate cursorDate,
            final Long cursorId,
            final int size
    ) {
        List<Subscribe> subscribes = subscribeRetriever.findByUserId(userId);

        if (keyword != null) {
            subscribes = subscribes.stream()
                    .filter(subscribe -> keyword.equals(subscribe.getKeyword()))
                    .toList();
        }

        Set<Long> lostItemIds = new HashSet<>();

        for (Subscribe subscribe : subscribes) {
            SearchHits<LostItemDocument> searchHits = lostItemSearchService.searchLostItemsForSubscription(
                    dateStart, dateEnd,
                    subscribe.getKeyword(),
                    subscribe.getCategory().getId(),
                    subscribe.getRegion()
            );

            lostItemIds.addAll(
                    searchHits.getSearchHits()
                            .stream()
                            .map(hit -> hit.getContent().getId())
                            .collect(Collectors.toSet())
            );
        }

        if (lostItemIds.isEmpty()) {
            return new SubscribeListDto(0, Collections.emptyList(), null, null);
        }

        List<LostItem> finalLostItems = lostItemRetriever.findByIdInWithCursorPagination(
                new ArrayList<>(lostItemIds),
                cursorDate,
                cursorId,
                size
        );

        LocalDate nextCursorDate = finalLostItems.isEmpty() ? null : finalLostItems.get(finalLostItems.size() - 1).getDate();
        Long nextCursorId = finalLostItems.isEmpty() ? null : finalLostItems.get(finalLostItems.size() - 1).getId();

        return SubscribeListDto.from(finalLostItems, nextCursorDate, nextCursorId);
    }
}
