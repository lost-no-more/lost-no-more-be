package org.lostnomore.backend.item.manager;

import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.global.exception.BusinessException;
import org.lostnomore.backend.global.exception.code.ItemErrorCode;
import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.dto.request.LostItemIdsDto;
import org.lostnomore.backend.item.repository.LostItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LostItemRetriever {

    private final LostItemRepository lostItemRepository;

    public Tuple findItemCountByCreatedAtAfter(final LocalDateTime today) {
        return lostItemRepository.findItemCountByCreatedAtAfter(today);
    }

    public Page<LostItem> findRecentItemsByUserId(
            final Long userId,
            final Pageable pageable
    ) {
        return lostItemRepository.findRecentItemsByUserId(userId, pageable);
    }

    public List<LostItem> findByIdIn(List<Long> ids) {
        return lostItemRepository.findByIdIn(ids);
    }

    public LostItem findById(final Long lostItemId) {
        return lostItemRepository.findById(lostItemId)
                .orElseThrow(() -> new BusinessException(ItemErrorCode.ITEM_NOT_FOUND));
    }
}
