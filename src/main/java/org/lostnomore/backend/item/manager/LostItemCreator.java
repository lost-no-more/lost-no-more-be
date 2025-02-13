package org.lostnomore.backend.item.manager;

import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.item.domain.LostItem;
import org.lostnomore.backend.item.repository.LostItemRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LostItemCreator {

    private final LostItemRepository lostItemRepository;

    public LostItem save(final LostItem lostItem) {
        return lostItemRepository.save(lostItem);
    }
}
