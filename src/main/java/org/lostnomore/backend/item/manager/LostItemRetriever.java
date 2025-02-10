package org.lostnomore.backend.item.manager;

import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.lostnomore.backend.item.repository.LostItemRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LostItemRetriever {

    private final LostItemRepository lostItemRepository;

    public Tuple findItemCountByCreatedAtAfter(final LocalDateTime today) {
        return lostItemRepository.findItemCountByCreatedAtAfter(today);
    }
}
