package org.lostnomore.backend.item.manager;

import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.dto.request.LostItemIdsDto;
import org.lostnomore.backend.item.repository.LostItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LostItemRetriever {

    private final LostItemRepository lostItemRepository;

    public Tuple findItemCountByCreatedAtAfter(final LocalDateTime today) {
        return lostItemRepository.findItemCountByCreatedAtAfter(today);
    }

    public List<LostItem> findByIdIn(List<Long> ids) {
        return lostItemRepository.findByIdIn(ids);
    }

    public List<LostItem> findByIdInWithCursorPagination(
            final ArrayList<Long> ids,
            final LocalDate cursorDate,
            final Long cursorId,
            final int size
    ) {
        return lostItemRepository.findByIdInWithCursorPagination(ids, cursorDate, cursorId, size);
    }
}
