package org.lostnomore.backend.subscribe.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.SubscribeErrorCode;
import org.lostnomore.backend.item.domain.Category;
import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.elastic.LostItemDocument;
import org.lostnomore.backend.item.elastic.LostItemSearchService;
import org.lostnomore.backend.item.manager.CategoryRetriever;
import org.lostnomore.backend.item.manager.LostItemRetriever;
import org.lostnomore.backend.subscribe.domain.Subscribe;
import org.lostnomore.backend.subscribe.dto.request.SubscribeCreateDto;
import org.lostnomore.backend.subscribe.dto.response.RecentItemsDto;
import org.lostnomore.backend.subscribe.dto.response.SubscribeListDto;
import org.lostnomore.backend.subscribe.manager.SubscribeCreator;
import org.lostnomore.backend.subscribe.manager.SubscribeRetriever;
import org.lostnomore.backend.user.domain.User;
import org.lostnomore.backend.user.manager.UserRetriever;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final UserRetriever userRetriever;
    private final CategoryRetriever categoryRetriever;
    private final SubscribeCreator subscribeCreator;

    @Transactional(readOnly = true)
    public RecentItemsDto getRecentItems(final Long userId) {
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        LocalDate dateStart = dateEnd.minusDays(7);

        List<Subscribe> subscribes = subscribeRetriever.findByUserId(userId);
        Set<Long> lostItemIds = searchLostItemIds(subscribes, dateStart, dateEnd, 9);

        if (lostItemIds.isEmpty()) {
            return new RecentItemsDto(Collections.emptyList());
        }

        return RecentItemsDto.from(lostItemRetriever.findByIdIn(new ArrayList<>(lostItemIds)));
    }

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

        Set<Long> lostItemIds = searchLostItemIds(subscribes, dateStart, dateEnd, null);

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

    private Set<Long> searchLostItemIds(List<Subscribe> subscribes, LocalDate dateStart, LocalDate dateEnd, Integer size) {
        Set<Long> lostItemIds = new HashSet<>();

        for (Subscribe subscribe : subscribes) {
            SearchHits<LostItemDocument> searchHits = lostItemSearchService.searchLostItemsForSubscription(
                    dateStart, dateEnd,
                    subscribe.getKeyword(),
                    subscribe.getCategory().getId(),
                    subscribe.getRegion(),
                    size
            );

            lostItemIds.addAll(
                    searchHits.getSearchHits()
                            .stream()
                            .map(hit -> hit.getContent().getId())
                            .collect(Collectors.toSet())
            );
        }

        return lostItemIds;
    }

    public void createSubscribe(
            final Long userId,
            final SubscribeCreateDto subscribeCreateDto
    ) {
       User user = userRetriever.findById(userId);
        Category category = categoryRetriever.findByName(subscribeCreateDto.category());

       Subscribe subscribe = Subscribe.builder()
               .user(user)
               .keyword(subscribeCreateDto.keyword())
               .category(category)
               .region(subscribeCreateDto.region())
               .build();

        try {
            subscribeCreator.save(subscribe);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(SubscribeErrorCode.SUBSCRIBE_DUPLICATE);
        }
    }
}
