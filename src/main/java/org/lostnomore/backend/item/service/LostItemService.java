package org.lostnomore.backend.item.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.item.domain.Category;
import org.lostnomore.backend.item.domain.Location;
import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.dto.request.LostItemCreateDto;
import org.lostnomore.backend.item.dto.response.LostItemsListDto;
import org.lostnomore.backend.item.dto.response.LostItemsSearchDto;
import org.lostnomore.backend.item.elastic.LostItemDocument;
import org.lostnomore.backend.item.elastic.LostItemSearchRepository;
import org.lostnomore.backend.item.elastic.LostItemSearchService;
import org.lostnomore.backend.item.manager.*;
import jakarta.persistence.Tuple;
import org.lostnomore.backend.item.dto.response.ItemsCountDto;
import org.lostnomore.backend.item.dto.response.RecentItemsDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LostItemService {

    private final LostItemRetriever lostItemRetriever;
    private final CategoryRetriever categoryRetriever;
    private final LocationRetriever locationRetriever;
    private final LocationCreator locationCreator;
    private final LostItemCreator lostItemCreator;
    private final LostItemSearchRepository lostItemSearchRepository;
    private final LostItemSearchService lostItemSearchService;

    @Transactional(readOnly = true)
    public ItemsCountDto getItemsCount() {
        Tuple stats = lostItemRetriever.findItemCountByCreatedAtAfter(LocalDate.now().atStartOfDay());

        Long todayCount = stats.get(0, Long.class);
        Long totalCount = stats.get(1, Long.class);

        return ItemsCountDto.of(todayCount.intValue(), totalCount.intValue());
    }

    @Transactional(readOnly = true)
    public RecentItemsDto getRecentItems(final Long userId) {
        Pageable pageable = PageRequest.of(0, 9);
        List<LostItem> recentItems = lostItemRetriever.findRecentItemsByUserId(userId, pageable).getContent();

        return RecentItemsDto.from(recentItems);
    }

    @Transactional
    public void saveLostItem(final LostItemCreateDto request) {
        Category category = categoryRetriever.findById(request.categoryId());

        Location location = locationRetriever.findByName(request.location());

        if (location == null) {
            location = locationCreator.save(Location.builder()
                    .name(request.location())
                    .region("부산")
                    .longitude(129.0756)
                    .latitude(35.1796)
                    .build());
        }

        LostItem savedItem = lostItemCreator.save(LostItem.builder()
                .name(request.name())
                .date(request.date())
                .category(category)
                .location(location)
                .color(request.color())
                .image(request.image())
                .build());

        LostItemDocument document = LostItemDocument.builder()
                .id(savedItem.getId())
                .name(savedItem.getName())
                .date(savedItem.getDate())
                .categoryId(savedItem.getCategory().getId())
                .region(savedItem.getLocation().getRegion())
                .location(
                        new GeoPoint(
                                savedItem.getLocation().getLatitude(),
                                savedItem.getLocation().getLongitude()
                        )
                )
                .build();

        lostItemSearchRepository.save(document);
    }

    @Transactional(readOnly = true)
    public LostItemsSearchDto searchLostItems(
            LocalDate dateStart, LocalDate dateEnd,
            Double topLeftLat, Double topLeftLon,
            Double bottomRightLat, Double bottomRightLon,
            String keyword, Long categoryId, String region
    ) {

        SearchHits<LostItemDocument> lostItems = lostItemSearchService.searchLostItems(
                dateStart, dateEnd,
                topLeftLat, topLeftLon,
                bottomRightLat, bottomRightLon,
                keyword, categoryId, region
        );

        return LostItemsSearchDto.from(lostItems);
    }

    @Transactional(readOnly = true)
    public LostItemsListDto searchLostItemsList(final List<Long> ids) {
        return LostItemsListDto.from(lostItemRetriever.findByIdIn(ids));
    }
}
