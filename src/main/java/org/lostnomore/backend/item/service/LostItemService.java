package org.lostnomore.backend.item.service;

import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.item.dto.response.ItemsCountDto;
import org.lostnomore.backend.item.manager.LostItemRetriever;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
}
