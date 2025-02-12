package org.lostnomore.backend.item.service;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.item.domain.Category;
import org.lostnomore.backend.item.domain.Location;
import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.dto.request.LostItemCreateDto;
import org.lostnomore.backend.item.elastic.LostItemDocument;
import org.lostnomore.backend.item.elastic.LostItemSearchRepository;
import org.lostnomore.backend.item.repository.CategoryRepository;
import org.lostnomore.backend.item.repository.LocationRepository;
import org.lostnomore.backend.item.repository.LostItemRepository;
import jakarta.persistence.Tuple;
import org.lostnomore.backend.item.dto.response.ItemsCountDto;
import org.lostnomore.backend.item.dto.response.RecentItemsDto;
import org.lostnomore.backend.item.manager.LostItemRetriever;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LostItemService {

    private final LostItemRetriever lostItemRetriever;

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

    private final LostItemRepository lostItemRepository;
    private final LostItemSearchRepository lostItemSearchRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void saveLostItem(LostItemCreateDto request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 Category가 존재하지 않습니다."));

        Location location = locationRepository.findByName(request.location());

        if (location == null) {
            location = locationRepository.save(Location.builder()
                    .name(request.location())
                    .region("부산")
                    .longitude(129.0756)
                    .latitude(35.1796)
                    .build());
        }

        LostItem savedItem = lostItemRepository.save(LostItem.builder()
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
                .lat(savedItem.getLocation().getLatitude())
                .lon(savedItem.getLocation().getLongitude())
                .build();

        lostItemSearchRepository.save(document);
    }
}
